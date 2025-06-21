package com.project_team09.service;

import com.project_team09.model.*;
import com.project_team09.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExcelParsingService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelParsingService.class);

    @Autowired
    private ExcelFileRepository excelFileRepository;

    @Autowired
    private ExcelSheetRepository excelSheetRepository;

    @Autowired
    private ProductBacklogItemRepository productBacklogItemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private TestStepRepository testStepRepository;

    // Upload directory
    private static final String UPLOAD_DIR = "uploads/excel-files/";

    public String parseExcelFile(MultipartFile file, Project project) {
        logger.info("Starting Excel parsing for project: {} with file: {}", project.getName(), file.getOriginalFilename());

        try {
            // Clean existing data for this project
            cleanExistingProjectData(project);

            // Save file physically
            String filePath = saveFilePhysically(file);

            // Create Excel file record
            ExcelFile excelFile = createExcelFileRecord(file, project, filePath);

            // Parse Excel content
            parseExcelContent(file, project, excelFile);

            logger.info("Excel parsing completed successfully for project: {}", project.getName());
            return "Excel file parsed and saved successfully";

        } catch (Exception e) {
            logger.error("Error parsing Excel file for project: {}", project.getName(), e);
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }
    }

    private void cleanExistingProjectData(Project project) {
        logger.info("Cleaning existing data for project: {}", project.getName());
        
        // Delete in correct order to respect foreign key constraints
        testStepRepository.deleteByProjectId(project.getId());
        testCaseRepository.deleteByProjectId(project.getId());
        productBacklogItemRepository.deleteByProjectId(project.getId());
        excelSheetRepository.deleteByProjectId(project.getId());
        excelFileRepository.deleteByProjectId(project.getId());
        
        logger.info("Existing data cleaned for project: {}", project.getName());
    }

    private String saveFilePhysically(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        String filePath = UPLOAD_DIR + uniqueFilename;

        // Save file
        File destinationFile = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
            fos.write(file.getBytes());
        }

        logger.info("File saved physically at: {}", filePath);
        return filePath;
    }

    private ExcelFile createExcelFileRecord(MultipartFile file, Project project, String filePath) {
        ExcelFile excelFile = new ExcelFile();
        excelFile.setProject(project);
        excelFile.setFileName(file.getOriginalFilename());
        excelFile.setFilePath(filePath);
        excelFile.setFileSize(file.getSize());
        excelFile.setUploadDate(LocalDateTime.now());
        
        excelFile = excelFileRepository.save(excelFile);
        logger.info("Excel file record created with ID: {}", excelFile.getId());
        return excelFile;
    }

    private void parseExcelContent(MultipartFile file, Project project, ExcelFile excelFile) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            
            logger.info("Excel file has {} sheets", workbook.getNumberOfSheets());
            
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                
                logger.info("Processing sheet: {} (index: {})", sheetName, i);
                
                // Create sheet record
                ExcelSheet excelSheet = createExcelSheetRecord(excelFile, sheetName, i);
                
                // Parse sheet content based on determined type
                String sheetType = excelSheet.getSheetType();
                if ("BACKLOG".equals(sheetType)) {
                    parseBacklogSheet(sheet, project, excelSheet);
                } else if ("TEST_CASES".equals(sheetType)) {
                    parseTestCaseSheet(sheet, project, excelSheet);
                } else {
                    logger.info("Sheet '{}' has unknown type '{}', skipping", sheetName, sheetType);
                }
            }
        }
    }

    private ExcelSheet createExcelSheetRecord(ExcelFile excelFile, String sheetName, int sheetIndex) {
        ExcelSheet excelSheet = new ExcelSheet();
        excelSheet.setExcelFile(excelFile);
        excelSheet.setSheetName(sheetName);
        excelSheet.setSheetIndex(sheetIndex);
        
        // Determine sheet type based on name and content
        String sheetType = determineSheetType(excelFile, sheetName, sheetIndex);
        excelSheet.setSheetType(sheetType);
        
        excelSheet = excelSheetRepository.save(excelSheet);
        logger.info("Excel sheet record created: {} (type: {}) with ID: {}", sheetName, sheetType, excelSheet.getId());
        return excelSheet;
    }
    
    private String determineSheetType(ExcelFile excelFile, String sheetName, int sheetIndex) {
        // First check by name
        if (sheetName.toLowerCase().contains("backlog")) {
            return "BACKLOG";
        } else if (sheetName.toLowerCase().contains("us")) {
            return "TEST_CASES";
        } else if (sheetName.toLowerCase().contains("test") || sheetName.toLowerCase().contains("case")) {
            return "TEST_CASES";
        }
        
        // If name doesn't match, check content by reading headers
        try {
            // Re-read the Excel file to check headers
            java.io.File file = new java.io.File(excelFile.getFilePath());
            if (file.exists()) {
                try (Workbook workbook = new XSSFWorkbook(new java.io.FileInputStream(file))) {
                    Sheet sheet = workbook.getSheetAt(sheetIndex);
                    if (sheet != null && sheet.getPhysicalNumberOfRows() > 0) {
                        Row headerRow = sheet.getRow(0);
                        if (headerRow != null) {
                            String firstCell = getCellStringValue(headerRow.getCell(0));
                            String secondCell = getCellStringValue(headerRow.getCell(1));
                            
                            // Check for backlog headers
                            if ("User ID".equalsIgnoreCase(firstCell) && "Description".toLowerCase().contains(secondCell.toLowerCase())) {
                                return "BACKLOG";
                            }
                            
                            // Check for test case headers by looking for Pre-Condition and Steps in all cells
                            boolean hasPreCondition = false;
                            boolean hasSteps = false;
                            
                            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                                String cellValue = getCellStringValue(headerRow.getCell(cellIndex));
                                if (cellValue.toLowerCase().contains("pre-condition") || 
                                    cellValue.toLowerCase().contains("precondition") ||
                                    cellValue.toLowerCase().contains("pre condition")) {
                                    hasPreCondition = true;
                                }
                                if (cellValue.toLowerCase().contains("steps") || 
                                    cellValue.toLowerCase().contains("step")) {
                                    hasSteps = true;
                                }
                            }
                            
                            if (hasPreCondition && hasSteps) {
                                return "TEST_CASES";
                            }
                            
                            // Check for test case headers
                            if ("US ID".equalsIgnoreCase(firstCell) && "TC ID".equalsIgnoreCase(secondCell)) {
                                return "TEST_CASES";
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Could not determine sheet type by content for sheet: {}", sheetName, e);
        }
        
        return "UNKNOWN";
    }

    private void parseBacklogSheet(Sheet sheet, Project project, ExcelSheet excelSheet) {
        logger.info("Parsing backlog sheet: {}", sheet.getSheetName());
        
        List<ProductBacklogItem> backlogItems = new ArrayList<>();
        int dataRowCount = 0;
        int skippedRowCount = 0;
        
        // Find first data row (skip headers)
        int firstDataRow = findFirstDataRow(sheet);
        logger.info("First data row found at index: {}", firstDataRow);
        
        for (int rowIndex = firstDataRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            
            // Get basic values
            String userId = getCellStringValue(row.getCell(0));
            String description = getCellStringValue(row.getCell(1));
            
            // Skip empty rows
            if (isEmptyRow(userId, description)) {
                skippedRowCount++;
                continue;
            }
            
            logger.debug("Processing backlog row {}: userId='{}', description='{}'", 
                rowIndex + 1, userId, description);
            
            ProductBacklogItem item = new ProductBacklogItem();
            item.setProject(project);
            item.setExcelSheet(excelSheet);
            item.setUserStoryId(userId);
            item.setDescription(description);
            item.setAcceptanceCriteria(getCellStringValue(row.getCell(2)));
            item.setHome(getCellStringValue(row.getCell(3)));
            item.setValidation(getCellStringValue(row.getCell(4)));
            item.setRowIndex(rowIndex);
            item.setCreatedAt(LocalDateTime.now());
            
            backlogItems.add(item);
            dataRowCount++;
        }
        
        // Save all items
        if (!backlogItems.isEmpty()) {
            productBacklogItemRepository.saveAll(backlogItems);
            logger.info("Saved {} backlog items (skipped {} empty rows)", dataRowCount, skippedRowCount);
        } else {
            logger.warn("No backlog items found in sheet: {}", sheet.getSheetName());
        }
    }

    private void parseTestCaseSheet(Sheet sheet, Project project, ExcelSheet excelSheet) {
        logger.info("Parsing test case sheet: {}", sheet.getSheetName());
        
        Map<String, TestCase> testCaseMap = new HashMap<>();
        List<TestStep> testSteps = new ArrayList<>();
        
        int dataRowCount = 0;
        int skippedRowCount = 0;
        
        // Merged cell değerlerini hatırlamak için
        String lastUsId = "";
        String lastTcId = "";
        String lastObjective = "";
        String lastPreCondition = "";
        
        // Find first data row (skip headers)
        int firstDataRow = findFirstDataRow(sheet);
        logger.info("First data row found at index: {}", firstDataRow);
        
        for (int rowIndex = firstDataRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            
            // Get basic values - Excel structure: US ID (A), TC ID (B), Test Objective (C), Pre-Condition (D), No (E), Steps (F), Test Data (G), Expected Result (H), Actual Result (I), Home (J)
            String usId = getCellStringValue(row.getCell(0));  // A: US ID
            String tcId = getCellStringValue(row.getCell(1));  // B: TC ID
            String objective = getCellStringValue(row.getCell(2));  // C: Test Objective
            String preCondition = getCellStringValue(row.getCell(3));  // D: Pre-Condition
            String stepNoStr = getCellStringValue(row.getCell(4)); // E: No (step number)
            String stepDescription = getCellStringValue(row.getCell(5)); // F: Steps
            
            // Merged cell handling - boş olanları önceki değerlerle doldur
            if (!usId.isEmpty()) lastUsId = usId;
            else usId = lastUsId;
            
            if (!tcId.isEmpty()) lastTcId = tcId;
            else tcId = lastTcId;
            
            if (!objective.isEmpty()) lastObjective = objective;
            else objective = lastObjective;
            
            if (!preCondition.isEmpty()) lastPreCondition = preCondition;
            else preCondition = lastPreCondition;
            
            // Skip completely empty rows
            if (isEmptyRow(usId, tcId, stepDescription)) {
                skippedRowCount++;
                continue;
            }
            
            // Skip rows that are just separators or notes
            if (usId.toLowerCase().contains("note") || tcId.toLowerCase().contains("note") || 
                usId.toLowerCase().contains("bug") || tcId.toLowerCase().contains("bug")) {
                skippedRowCount++;
                continue;
            }
            
            // Skip header rows
            if ("US ID".equalsIgnoreCase(usId) && "TC ID".equalsIgnoreCase(tcId)) {
                logger.info("Skipping header row at index: {}", rowIndex);
                // Header tespit edildiğinde merged cell değişkenlerini sıfırla
                lastUsId = "";
                lastTcId = "";
                lastObjective = "";
                lastPreCondition = "";
                skippedRowCount++;
                continue;
            }
            
            logger.info("Processing test case row {}: usId='{}', tcId='{}', stepNo='{}', objective='{}'", 
                rowIndex + 1, usId, tcId, stepNoStr, objective);
            
            // Create test case key
            String testCaseKey = usId + "-" + tcId;
            
            // Create or get existing test case
            TestCase testCase = testCaseMap.get(testCaseKey);
            if (testCase == null) {
                // Create new test case only if we have US ID and TC ID
                if (usId.isEmpty() || tcId.isEmpty()) {
                    skippedRowCount++;
                    continue;
                }
                
                testCase = new TestCase();
                testCase.setProject(project);
                testCase.setExcelSheet(excelSheet);
                testCase.setUserStoryId(usId);                                    // A: US ID (merged cell handled)
                testCase.setTestCaseId(tcId);                                     // B: TC ID (merged cell handled)
                testCase.setObjective(objective);                                 // C: Test Objective (merged cell handled)
                testCase.setPreCondition(preCondition);                           // D: Pre-Condition (merged cell handled)
                testCase.setRowIndex(rowIndex);
                testCase.setCreatedAt(LocalDateTime.now());
                
                testCaseMap.put(testCaseKey, testCase);
                logger.debug("Created new test case: {}", testCaseKey);
            }
            
            // Create test step if we have step description
            if (!stepDescription.isEmpty()) {
                TestStep testStep = new TestStep();
                testStep.setTestCase(testCase);
                
                // Parse step number
                int stepNumber = 1;
                try {
                    if (!stepNoStr.isEmpty()) {
                        stepNumber = Integer.parseInt(stepNoStr);
                    }
                } catch (NumberFormatException e) {
                    logger.debug("Could not parse step number '{}', using default", stepNoStr);
                }
                
                testStep.setStepNumber(stepNumber);
                testStep.setDescription(stepDescription);                         // F: Steps
                testStep.setTestData(getCellStringValue(row.getCell(6)));         // G: Test Data
                testStep.setExpectedResult(getCellStringValue(row.getCell(7)));   // H: Expected Result
                testStep.setActualResult(getCellStringValue(row.getCell(8)));     // I: Actual Result
                testStep.setIsHome("Home".equalsIgnoreCase(getCellStringValue(row.getCell(9)))); // J: Home
                testStep.setStatus("PENDING");
                testStep.setRowIndex(rowIndex);
                testStep.setCreatedAt(LocalDateTime.now());
                
                testSteps.add(testStep);
                dataRowCount++;
                
                logger.debug("Added step {} to test case {}: '{}'", stepNumber, testCaseKey, stepDescription);
            }
        }
        
        // Save all items
        if (!testCaseMap.isEmpty()) {
            List<TestCase> testCases = new ArrayList<>(testCaseMap.values());
            testCaseRepository.saveAll(testCases);
            testStepRepository.saveAll(testSteps);
            logger.info("Saved {} test cases with {} total steps (skipped {} empty/invalid rows)", 
                testCases.size(), testSteps.size(), skippedRowCount);
        } else {
            logger.warn("No test cases found in sheet: {}", sheet.getSheetName());
        }
    }

    private int findFirstDataRow(Sheet sheet) {
        // Look for first row that has meaningful data (not headers)
        for (int rowIndex = 0; rowIndex <= Math.min(10, sheet.getLastRowNum()); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            
            String firstCell = getCellStringValue(row.getCell(0));
            String secondCell = getCellStringValue(row.getCell(1));
            
            // Skip typical header patterns
            if (isHeaderRow(firstCell, secondCell)) {
                continue;
            }
            
            // If we have non-header content, this is likely the first data row
            if (!firstCell.isEmpty() || !secondCell.isEmpty()) {
                logger.debug("First data row detected at index: {}", rowIndex);
                return rowIndex;
            }
        }
        
        // Default to row 1 if we can't detect headers
        return 1;
    }

    private boolean isHeaderRow(String cell1, String cell2) {
        if (cell1.isEmpty() && cell2.isEmpty()) {
            return false;
        }
        
        String combined = (cell1 + " " + cell2).toLowerCase();
        
        // Common header patterns
        return combined.contains("user id") || 
               combined.contains("description") ||
               combined.equals("us id tc id") ||
               combined.contains("us id") ||
               combined.contains("tc id") ||
               combined.contains("test case") ||
               combined.contains("objective") ||
               combined.contains("pre-condition") ||
               combined.contains("steps") ||
               combined.contains("expected");
    }

    private boolean isEmptyRow(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue().toString();
                    } else {
                        // Handle numeric values properly
                        double numValue = cell.getNumericCellValue();
                        if (numValue == (long) numValue) {
                            return String.valueOf((long) numValue);
                        } else {
                            return String.valueOf(numValue);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return cell.getStringCellValue().trim();
                    } catch (IllegalStateException e) {
                        try {
                            double numValue = cell.getNumericCellValue();
                            if (numValue == (long) numValue) {
                                return String.valueOf((long) numValue);
                            } else {
                                return String.valueOf(numValue);
                            }
                        } catch (IllegalStateException e2) {
                            return "";
                        }
                    }
                case BLANK:
                    return "";
                default:
                    return "";
            }
        } catch (Exception e) {
            logger.warn("Error reading cell value: {}", e.getMessage());
            return "";
        }
    }
} 