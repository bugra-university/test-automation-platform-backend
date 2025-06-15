package com.project_team09.api.service;

import com.project_team09.api.model.dto.ExcelTestCaseDTO;
import com.project_team09.api.model.dto.ExcelTestStepDTO;
import com.project_team09.api.model.entity.TestCase;
import com.project_team09.api.model.entity.TestStep;
import com.project_team09.api.repository.TestCaseRepository;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ExcelParserService {
    // Excel sütun indexleri
    private static final int USER_STORY_ID_COL = 0;
    private static final int TEST_CASE_ID_COL = 1;
    private static final int TEST_OBJECTIVE_COL = 2;
    private static final int PRE_CONDITION_COL = 3;
    private static final int STEP_NO_COL = 4;
    private static final int STEPS_COL = 5;
    private static final int TEST_DATA_COL = 6;
    private static final int EXPECTED_RESULT_COL = 7;
    private static final int ACTUAL_RESULT_COL = 8;
    private static final int HOME_COL = 9;

    @Autowired
    private TestCaseRepository testCaseRepository;

    /**
     * Excel dosyasını parse eder, verileri veritabanına kaydeder ve test case'leri
     * döndürür
     */
    public List<TestCase> parseAndSaveExcelFile(MultipartFile file) throws IOException {
        List<ExcelTestCaseDTO> testCaseDTOs = parseExcelFile(file);
        List<TestCase> savedTestCases = new ArrayList<>();

        for (ExcelTestCaseDTO dto : testCaseDTOs) {
            // Önce mevcut test case'i kontrol et
            Optional<TestCase> existingTestCase = testCaseRepository
                    .findByUserStoryIdAndTestCaseId(dto.getUserStoryId(), dto.getTestCaseId());

            TestCase testCase = existingTestCase.orElse(new TestCase());

            // Test case bilgilerini güncelle
            updateTestCaseFromDTO(testCase, dto);

            // Test case'i kaydet
            savedTestCases.add(testCaseRepository.save(testCase));
        }

        return savedTestCases;
    }

    private void updateTestCaseFromDTO(TestCase testCase, ExcelTestCaseDTO dto) {
        testCase.setUserStoryId(dto.getUserStoryId());
        testCase.setTestCaseId(dto.getTestCaseId());
        testCase.setTestObjective(dto.getTestObjective());
        testCase.setPreCondition(dto.getPreCondition());
        testCase.setNote(dto.getNote());
        testCase.setName(dto.getUserStoryId() + "_" + dto.getTestCaseId());
        testCase.setDescription(dto.getTestObjective());
        testCase.setClassName(dto.getUserStoryId().toLowerCase() + "." + dto.getTestCaseId().toLowerCase());
        testCase.setMethodName("test" + dto.getTestCaseId());

        // Test steps'leri güncelle
        if (testCase.getSteps() == null) {
            testCase.setSteps(new ArrayList<>());
        } else {
            testCase.getSteps().clear();
        }

        for (ExcelTestStepDTO stepDTO : dto.getSteps()) {
            TestStep step = new TestStep();
            step.setTestCase(testCase);
            step.setStepNumber(stepDTO.getStepNumber());
            step.setStepDescription(stepDTO.getStepDescription());
            step.setTestData(stepDTO.getTestData());
            step.setExpectedResult(stepDTO.getExpectedResult());
            step.setActualResult(stepDTO.getActualResult());
            step.setIsHighlighted(stepDTO.getIsHighlighted());
            step.setIsHome(stepDTO.getIsHome());
            step.setName("Step " + stepDTO.getStepNumber());
            step.setDescription(stepDTO.getStepDescription());
            step.setStatus("PENDING");
            step.setOrderNumber(stepDTO.getStepNumber());

            testCase.getSteps().add(step);
        }
    }

    /**
     * Excel dosyasını parse eder ve test case'leri döndürür
     */
    public List<ExcelTestCaseDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<ExcelTestCaseDTO> testCases = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Sadece US01, US02 gibi sheet'leri bul ve işle
            for (Sheet sheet : workbook) {
                String sheetName = sheet.getSheetName();
                // Check if sheet name contains US followed by numbers, case insensitive
                if (sheetName.toUpperCase().matches(".*US\\d+.*")) {
                    testCases.addAll(parseSheet(sheet));
                }
            }
        }

        return testCases;
    }

    /**
     * Bir Excel sheet'ini parse eder
     */
    private List<ExcelTestCaseDTO> parseSheet(Sheet sheet) {
        List<ExcelTestCaseDTO> testCases = new ArrayList<>();
        Map<String, ExcelTestCaseDTO> testCaseMap = new HashMap<>();
        ExcelTestCaseDTO currentTestCase = null;
        int stepCount = 0;

        // İlk satır başlık olduğu için 1'den başla
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null || isEmptyRow(row)) {
                // Boş satır bulundu, bu test case'in sonu olabilir
                if (currentTestCase != null && stepCount > 0) {
                    // Test case'i tamamla
                    currentTestCase = null;
                    stepCount = 0;
                }
                continue;
            }

            String userStoryId = getCellValueAsString(row.getCell(USER_STORY_ID_COL));
            String testCaseId = getCellValueAsString(row.getCell(TEST_CASE_ID_COL));

            // Eğer yeni bir test case başlıyorsa
            if (!userStoryId.isEmpty() && !testCaseId.isEmpty()) {
                // Eğer önceki test case varsa ve adımları varsa, onu tamamla
                if (currentTestCase != null && stepCount > 0) {
                    currentTestCase = null;
                    stepCount = 0;
                }

                String testCaseKey = userStoryId + "_" + testCaseId;
                // Eğer bu test case zaten işlendiyse, atla
                if (!testCaseMap.containsKey(testCaseKey)) {
                    currentTestCase = new ExcelTestCaseDTO();
                    currentTestCase.setUserStoryId(userStoryId);
                    currentTestCase.setTestCaseId(testCaseId);
                    currentTestCase.setTestObjective(getCellValueAsString(row.getCell(TEST_OBJECTIVE_COL)));
                    currentTestCase.setPreCondition(getCellValueAsString(row.getCell(PRE_CONDITION_COL)));
                    currentTestCase.setSteps(new ArrayList<>());
                    testCaseMap.put(testCaseKey, currentTestCase);
                }
            }

            // Test adımını ekle
            if (currentTestCase != null) {
                String stepNoStr = getCellValueAsString(row.getCell(STEP_NO_COL));
                if (stepNoStr != null && !stepNoStr.trim().isEmpty()) {
                    ExcelTestStepDTO step = new ExcelTestStepDTO();
                    step.setStepNumber(Integer.parseInt(stepNoStr));
                    step.setStepDescription(getCellValueAsString(row.getCell(STEPS_COL)));
                    step.setTestData(getCellValueAsString(row.getCell(TEST_DATA_COL)));
                    step.setExpectedResult(getCellValueAsString(row.getCell(EXPECTED_RESULT_COL)));
                    step.setActualResult(getCellValueAsString(row.getCell(ACTUAL_RESULT_COL)));

                    // Home değerini kontrol et (yeşil renk kontrolü)
                    Cell homeCell = row.getCell(HOME_COL);
                    if (homeCell != null && isCellGreen(homeCell)) {
                        step.setIsHome(true);
                    }

                    // Sarı renkli hücreleri kontrol et
                    Cell stepsCell = row.getCell(STEPS_COL);
                    step.setIsHighlighted(isCellHighlighted(stepsCell));

                    currentTestCase.getSteps().add(step);
                    stepCount++;
                }

                // BUG notlarını kontrol et
                String note = getNotesFromRow(row);
                if (note != null) {
                    currentTestCase.setNote(note);
                }
            }
        }

        testCases.addAll(testCaseMap.values());
        return testCases;
    }

    /**
     * Hücre değerini String olarak alır
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    /**
     * Hücrenin yeşil renkli olup olmadığını kontrol eder
     */
    private boolean isCellGreen(Cell cell) {
        if (cell == null)
            return false;

        CellStyle style = cell.getCellStyle();
        if (style == null || !(style.getFillForegroundColorColor() instanceof XSSFColor))
            return false;

        XSSFColor color = (XSSFColor) style.getFillForegroundColorColor();
        byte[] rgb = color.getRGB();
        if (rgb == null)
            return false;

        // Excel'de yeşil renk için yaklaşık RGB değerleri
        return isColorMatch(new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF),
                new Color(198, 239, 206));
    }

    /**
     * Hücrenin sarı ile vurgulanmış olup olmadığını kontrol eder
     */
    private boolean isCellHighlighted(Cell cell) {
        if (cell == null)
            return false;

        CellStyle style = cell.getCellStyle();
        if (style == null || !(style.getFillForegroundColorColor() instanceof XSSFColor))
            return false;

        XSSFColor color = (XSSFColor) style.getFillForegroundColorColor();
        byte[] rgb = color.getRGB();
        if (rgb == null)
            return false;

        // Excel'de sarı renk için yaklaşık RGB değerleri
        return isColorMatch(new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF),
                new Color(255, 255, 0));
    }

    /**
     * İki rengin yaklaşık olarak eşleşip eşleşmediğini kontrol eder
     */
    private boolean isColorMatch(Color color1, Color color2) {
        if (color1 == null || color2 == null)
            return false;

        int tolerance = 20; // Renk toleransı
        return Math.abs(color1.getRed() - color2.getRed()) <= tolerance &&
                Math.abs(color1.getGreen() - color2.getGreen()) <= tolerance &&
                Math.abs(color1.getBlue() - color2.getBlue()) <= tolerance;
    }

    /**
     * Satırdan BUG notlarını alır (turuncu hücreler)
     */
    private String getNotesFromRow(Row row) {
        StringBuilder notes = new StringBuilder();

        for (Cell cell : row) {
            if (isCellOrange(cell)) {
                String note = getCellValueAsString(cell);
                if (!note.trim().isEmpty()) {
                    if (notes.length() > 0) {
                        notes.append("; ");
                    }
                    notes.append(note);
                }
            }
        }

        return notes.length() > 0 ? notes.toString() : null;
    }

    /**
     * Hücrenin turuncu renkli olup olmadığını kontrol eder (BUG notları için)
     */
    private boolean isCellOrange(Cell cell) {
        if (cell == null)
            return false;

        CellStyle style = cell.getCellStyle();
        if (style == null || !(style.getFillForegroundColorColor() instanceof XSSFColor))
            return false;

        XSSFColor color = (XSSFColor) style.getFillForegroundColorColor();
        byte[] rgb = color.getRGB();
        if (rgb == null)
            return false;

        // Excel'de turuncu renk için yaklaşık RGB değerleri
        return isColorMatch(new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF),
                new Color(255, 192, 0));
    }

    /**
     * Satırın boş olup olmadığını kontrol eder
     */
    private boolean isEmptyRow(Row row) {
        if (row == null)
            return true;

        for (int cellNum = 0; cellNum < 10; cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Veritabanındaki tüm test case'leri ve test step'leri siler
     * 
     * @return Silinen test case sayısı
     */
    public int deleteAllTestData() {
        int count = (int) testCaseRepository.count();
        testCaseRepository.deleteAll();
        return count;
    }

    /**
     * Belirli bir dosya adına ait test case'leri ve test step'leri siler
     * 
     * @param fileName Silinecek dosya adı
     * @return Silinen test case sayısı
     */
    public int deleteTestDataByFileName(String fileName) {
        // Not: Bu metod için source_file alanı olmadığı için şimdilik tüm verileri
        // siliyor
        // Gelecekte source_file alanı eklendiğinde bu metod güncellenebilir
        return deleteAllTestData();
    }

    /**
     * Database tablo istatistiklerini getirir
     * 
     * @return Tablo istatistikleri
     */
    public Map<String, Object> getTableStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // Test case sayısını al
            long totalTestCases = testCaseRepository.count();
            statistics.put("totalTestCases", totalTestCases);

            // Benzersiz dosya adlarını al (user_story_id'den çıkarılan)
            List<String> uniqueFileNames = testCaseRepository.findAll()
                    .stream()
                    .map(tc -> tc.getUserStoryId() != null ? tc.getUserStoryId() : "Unknown")
                    .distinct()
                    .sorted()
                    .collect(java.util.stream.Collectors.toList());

            statistics.put("uniqueFiles", uniqueFileNames);
            statistics.put("totalUniqueFiles", uniqueFileNames.size());

            // Her dosya için detayları al
            List<Map<String, Object>> fileDetails = new ArrayList<>();
            for (String fileName : uniqueFileNames) {
                Map<String, Object> fileDetail = new HashMap<>();
                fileDetail.put("fileName", fileName);

                // Bu dosya adına ait test case'leri bul
                List<com.project_team09.api.model.entity.TestCase> testCases = testCaseRepository.findAll()
                        .stream()
                        .filter(tc -> fileName.equals(tc.getUserStoryId()))
                        .collect(java.util.stream.Collectors.toList());

                fileDetail.put("testCaseCount", testCases.size());

                // En eski ve en yeni oluşturma tarihlerini bul
                if (!testCases.isEmpty()) {
                    java.time.LocalDateTime earliest = testCases.stream()
                            .map(tc -> tc.getCreatedAt())
                            .filter(date -> date != null)
                            .min(java.time.LocalDateTime::compareTo)
                            .orElse(null);

                    java.time.LocalDateTime latest = testCases.stream()
                            .map(tc -> tc.getUpdatedAt() != null ? tc.getUpdatedAt() : tc.getCreatedAt())
                            .filter(date -> date != null)
                            .max(java.time.LocalDateTime::compareTo)
                            .orElse(null);

                    fileDetail.put("firstCreated", earliest);
                    fileDetail.put("lastUpdated", latest);
                }

                fileDetails.add(fileDetail);
            }

            statistics.put("fileDetails", fileDetails);
            statistics.put("timestamp", java.time.LocalDateTime.now());

        } catch (Exception e) {
            statistics.put("error", "Error fetching statistics: " + e.getMessage());
        }

        return statistics;
    }
}
