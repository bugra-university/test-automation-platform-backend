package com.project_team09.service;

import com.project_team09.model.*;
import com.project_team09.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExcelParsingService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelParsingService.class);

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProductBacklogItemRepository backlogItemRepository;
    @Autowired
    private TestCaseRepository testCaseRepository;
    @Autowired
    private ExcelFileRepository excelFileRepository;
    @Autowired
    private ExcelSheetRepository excelSheetRepository;

    @Transactional
    public void parseAndSaveExcel(MultipartFile file, Long projectId) throws Exception {
        logger.info("Starting Excel processing for project ID: {}", projectId);
        
        // 1. Proje kontrolü
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
        logger.debug("Found project: {}", project.getName());

        // 2. Excel dosyasını kaydet
        logger.debug("Saving Excel file to database...");
        ExcelFile excelFile = new ExcelFile();
        excelFile.setProject(project);
        excelFile.setFileName(file.getOriginalFilename());
        excelFile = excelFileRepository.save(excelFile);
        logger.info("Excel file saved with ID: {}", excelFile.getId());

        Map<String, ExcelSheet> sheetMap = new HashMap<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            logger.debug("Successfully opened Excel workbook with {} sheets", workbook.getNumberOfSheets());
            
            // 3. Excel sayfalarını kaydet
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                    logger.debug("Sheet {} is empty or null, skipping", i);
                    continue;
                }

                Row headerRow = sheet.getRow(0);
                String sheetType = "";
                
                if (isBacklogSheet(headerRow)) {
                    sheetType = "BACKLOG";
                    logger.debug("Found Backlog sheet at index {}", i);
                } else if (isTestCaseSheet(headerRow)) {
                    sheetType = "TEST_CASES";
                    logger.debug("Found Test Case sheet at index {}", i);
                }

                if (!sheetType.isEmpty()) {
                    ExcelSheet excelSheet = new ExcelSheet();
                    excelSheet.setExcelFile(excelFile);
                    excelSheet.setSheetName(sheet.getSheetName());
                    excelSheet.setSheetType(sheetType);
                    excelSheet = excelSheetRepository.save(excelSheet);
                    sheetMap.put(sheetType, excelSheet);
                    logger.info("Saved {} sheet with ID: {}", sheetType, excelSheet.getId());
                }
            }

            // 4. Backlog öğelerini işle ve kaydet
            if (sheetMap.containsKey("BACKLOG")) {
                logger.debug("Processing Backlog items...");
                Sheet backlogSheet = workbook.getSheetAt(0);
                List<ProductBacklogItem> backlogItems = parseBacklogSheet(backlogSheet, project, sheetMap.get("BACKLOG"));
                logger.debug("Saving {} backlog items to database...", backlogItems.size());
                backlogItemRepository.saveAll(backlogItems);
                logger.info("Successfully saved all backlog items");
            }

            // 5. Test case'leri işle ve kaydet
            if (sheetMap.containsKey("TEST_CASES")) {
                logger.debug("Processing Test Cases...");
                Sheet testCaseSheet = workbook.getSheetAt(1);
                List<TestCase> testCases = parseTestCaseSheet(testCaseSheet, project, sheetMap.get("TEST_CASES"));
                logger.debug("Saving {} test cases to database...", testCases.size());
                testCaseRepository.saveAll(testCases);
                logger.info("Successfully saved all test cases");
            }

            logger.info("Excel processing completed successfully");
        } catch (Exception e) {
            logger.error("Error during Excel processing: ", e);
            throw new RuntimeException("Failed to process Excel file: " + e.getMessage(), e);
        }
    }

    private List<ProductBacklogItem> parseBacklogSheet(Sheet sheet, Project project, ExcelSheet excelSheet) {
        logger.debug("Starting to parse Backlog sheet");
        List<ProductBacklogItem> backlogItems = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Skip header row
        
        int rowCount = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            ProductBacklogItem item = new ProductBacklogItem();
            item.setProject(project);
            item.setExcelSheet(excelSheet); // Önemli: Excel sayfası referansını ekle
            
            String userStoryId = getCellStringValue(row.getCell(0));
            logger.debug("Processing User Story ID: {}", userStoryId);
            
            item.setUserStoryId(userStoryId);
            item.setDescription(getCellStringValue(row.getCell(1)));
            item.setAcceptanceCriteria(getCellStringValue(row.getCell(2)));
            item.setHome(getCellStringValue(row.getCell(3)));
            item.setValidation(getCellStringValue(row.getCell(4)));
            item.setRowIndex(row.getRowNum());
            backlogItems.add(item);
            rowCount++;
        }
        logger.debug("Finished parsing Backlog sheet, processed {} rows", rowCount);
        return backlogItems;
    }

    private List<TestCase> parseTestCaseSheet(Sheet sheet, Project project, ExcelSheet excelSheet) {
        logger.debug("Starting to parse Test Case sheet");
        List<TestCase> testCases = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Skip header row

        int rowCount = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            TestCase testCase = new TestCase();
            testCase.setProject(project);
            testCase.setExcelSheet(excelSheet); // Önemli: Excel sayfası referansını ekle
            
            String userStoryId = getCellStringValue(row.getCell(1));
            logger.debug("Processing Test Case for US ID: {}", userStoryId);
            
            testCase.setUserStoryId(userStoryId);
            testCase.setTestCaseId(getCellStringValue(row.getCell(2)));
            testCase.setObjective(getCellStringValue(row.getCell(3)));
            testCase.setPreCondition(getCellStringValue(row.getCell(4)));
            testCase.setRowIndex(row.getRowNum());
            
            String stepsRaw = getCellStringValue(row.getCell(5));
            if (stepsRaw != null && !stepsRaw.isEmpty()) {
                String[] steps = stepsRaw.split("\n");
                for (int i = 0; i < steps.length; i++) {
                    TestStep testStep = new TestStep();
                    testStep.setStepNumber(i + 1);
                    testStep.setDescription(steps[i]);
                    testCase.addTestStep(testStep);
                }
            }

            testCase.setTestData(getCellStringValue(row.getCell(6)));
            testCase.setExpectedResult(getCellStringValue(row.getCell(7)));
            testCases.add(testCase);
            rowCount++;
        }
        logger.debug("Finished parsing Test Case sheet, processed {} rows", rowCount);
        return testCases;
    }

    private boolean isBacklogSheet(Row headerRow) {
        if (headerRow == null) {
            logger.debug("Header row is null");
            return false;
        }
        String firstCell = getCellStringValue(headerRow.getCell(0));
        String secondCell = getCellStringValue(headerRow.getCell(1));
        logger.debug("Checking if Backlog sheet - First cell: '{}', Second cell: '{}'", firstCell, secondCell);
        return "User ID".equalsIgnoreCase(firstCell) && "Description (Team)".equalsIgnoreCase(secondCell);
    }

    private boolean isTestCaseSheet(Row headerRow) {
        if (headerRow == null) {
            logger.debug("Header row is null");
            return false;
        }
        String firstCell = getCellStringValue(headerRow.getCell(0));
        String secondCell = getCellStringValue(headerRow.getCell(1));
        logger.debug("Checking if Test Case sheet - First cell: '{}', Second cell: '{}'", firstCell, secondCell);
        return "US ID".equalsIgnoreCase(firstCell) && "TC ID".equalsIgnoreCase(secondCell);
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        String result;
        switch (cell.getCellType()) {
            case STRING:
                result = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = cell.getLocalDateTimeCellValue().toString();
                } else {
                    result = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                result = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                try {
                    result = cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    result = String.valueOf(cell.getNumericCellValue());
                }
                break;
            default:
                result = "";
        }
        return result != null ? result.trim() : "";
    }
} 