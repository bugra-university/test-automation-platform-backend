package com.project_team09.service;

import com.project_team09.model.*;
import com.project_team09.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelParsingService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProductBacklogItemRepository backlogItemRepository;
    @Autowired
    private TestCaseRepository testCaseRepository;

    @Transactional
    public void parseAndSaveExcel(MultipartFile file, Long projectId) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        List<ProductBacklogItem> backlogItems = new ArrayList<>();
        List<TestCase> testCases = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                    continue; // Skip empty sheets
                }

                // Identify sheet type by header row
                Row headerRow = sheet.getRow(0);
                if (isBacklogSheet(headerRow)) {
                    parseBacklogSheet(sheet, project, backlogItems);
                } else if (isTestCaseSheet(headerRow)) {
                    parseTestCaseSheet(sheet, project, testCases);
                }
            }

            backlogItemRepository.saveAll(backlogItems);
            testCaseRepository.saveAll(testCases);
        }
    }

    private boolean isBacklogSheet(Row headerRow) {
        if (headerRow == null) return false;
        String firstCell = getCellStringValue(headerRow.getCell(0));
        String secondCell = getCellStringValue(headerRow.getCell(1));
        // A simple check based on the first two columns
        return "User ID".equalsIgnoreCase(firstCell) && "Description (Team)".equalsIgnoreCase(secondCell);
    }

    private boolean isTestCaseSheet(Row headerRow) {
        if (headerRow == null) return false;
        String firstCell = getCellStringValue(headerRow.getCell(0));
        String secondCell = getCellStringValue(headerRow.getCell(1));
        // A simple check based on the first two columns
        return "US ID".equalsIgnoreCase(firstCell) && "TC ID".equalsIgnoreCase(secondCell);
    }

    private void parseBacklogSheet(Sheet sheet, Project project, List<ProductBacklogItem> backlogItems) {
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Skip header row

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            ProductBacklogItem item = new ProductBacklogItem();
            item.setProject(project);
            item.setUserStoryId(getCellStringValue(row.getCell(0)));
            item.setDescription(getCellStringValue(row.getCell(1)));
            item.setAcceptanceCriteria(getCellStringValue(row.getCell(2)));
            item.setHome(getCellStringValue(row.getCell(3)));
            item.setValidation(getCellStringValue(row.getCell(4)));
            backlogItems.add(item);
        }
    }

    private void parseTestCaseSheet(Sheet sheet, Project project, List<TestCase> testCases) {
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Skip header row

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            TestCase testCase = new TestCase();
            testCase.setProject(project);
            testCase.setUserStoryId(getCellStringValue(row.getCell(1))); // US ID is in the second column (index 1) in some files
            testCase.setTestCaseId(getCellStringValue(row.getCell(2))); // TC ID is in the third column (index 2)
            testCase.setObjective(getCellStringValue(row.getCell(3)));
            testCase.setPreCondition(getCellStringValue(row.getCell(4)));
            
            // Steps are in one cell, separated by newlines
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
        }
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