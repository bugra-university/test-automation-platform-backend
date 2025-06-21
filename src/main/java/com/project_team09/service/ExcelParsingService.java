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
                
                // Parse sheet content based on name
                if (sheetName.toLowerCase().contains("backlog")) {
                    parseBacklogSheet(sheet, project, excelSheet);
                } else if (sheetName.toLowerCase().contains("test") || sheetName.toLowerCase().contains("case")) {
                    parseTestCaseSheet(sheet, project, excelSheet);
                } else {
                    logger.info("Sheet '{}' doesn't match known patterns, skipping", sheetName);
                }
            }
        }
    }

    private ExcelSheet createExcelSheetRecord(ExcelFile excelFile, String sheetName, int sheetIndex) {
        ExcelSheet excelSheet = new ExcelSheet();
        excelSheet.setExcelFile(excelFile);
        excelSheet.setSheetName(sheetName);
        excelSheet.setSheetIndex(sheetIndex);
        
        // Determine sheet type based on name
        String sheetType = "UNKNOWN";
        if (sheetName.toLowerCase().contains("backlog")) {
            sheetType = "BACKLOG";
        } else if (sheetName.toLowerCase().contains("test") || sheetName.toLowerCase().contains("case")) {
            sheetType = "TEST_CASES";
        }
        excelSheet.setSheetType(sheetType);
        
        excelSheet = excelSheetRepository.save(excelSheet);
        logger.info("Excel sheet record created: {} (type: {}) with ID: {}", sheetName, sheetType, excelSheet.getId());
        return excelSheet;
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
        
        List<TestCase> testCases = new ArrayList<>();
        List<TestStep> testSteps = new ArrayList<>();
        Set<String> processedTestCases = new HashSet<>();
        
        int dataRowCount = 0;
        int skippedRowCount = 0;
        
        // Find first data row (skip headers)
        int firstDataRow = findFirstDataRow(sheet);
        logger.info("First data row found at index: {}", firstDataRow);
        
        for (int rowIndex = firstDataRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            
            // Get basic values
            String usId = getCellStringValue(row.getCell(0));
            String tcId = getCellStringValue(row.getCell(1));
            
            // Skip empty rows
            if (isEmptyRow(usId, tcId)) {
                skippedRowCount++;
                continue;
            }
            
            logger.debug("Processing test case row {}: usId='{}', tcId='{}'", 
                rowIndex + 1, usId, tcId);
            
            // Check for duplicates
            String testCaseKey = usId + "-" + tcId;
            if (processedTestCases.contains(testCaseKey)) {
                logger.warn("Duplicate test case found: {} at row {}, skipping", testCaseKey, rowIndex + 1);
                skippedRowCount++;
                continue;
            }
            processedTestCases.add(testCaseKey);
            
            // Create test case
            TestCase testCase = new TestCase();
            testCase.setProject(project);
            testCase.setExcelSheet(excelSheet);
            testCase.setUserStoryId(usId);
            testCase.setTestCaseId(tcId);
            testCase.setObjective(getCellStringValue(row.getCell(2)));
            testCase.setPreCondition(getCellStringValue(row.getCell(3)));
            testCase.setRowIndex(rowIndex);
            testCase.setCreatedAt(LocalDateTime.now());
            
            testCases.add(testCase);
            
            // Create test step
            TestStep testStep = new TestStep();
            testStep.setTestCase(testCase);
            testStep.setDescription(getCellStringValue(row.getCell(4)));
            testStep.setTestData(getCellStringValue(row.getCell(5)));
            testStep.setExpectedResult(getCellStringValue(row.getCell(6)));
            testStep.setActualResult("");
            testStep.setStatus("PENDING");
            testStep.setRowIndex(rowIndex);
            testStep.setCreatedAt(LocalDateTime.now());
            
            testSteps.add(testStep);
            dataRowCount++;
        }
        
        // Save all items
        if (!testCases.isEmpty()) {
            testCaseRepository.saveAll(testCases);
            testStepRepository.saveAll(testSteps);
            logger.info("Saved {} test cases with steps (skipped {} empty/duplicate rows)", 
                dataRowCount, skippedRowCount);
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