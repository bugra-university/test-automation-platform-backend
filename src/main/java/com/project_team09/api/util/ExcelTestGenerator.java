package com.project_team09.api.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTestGenerator {

    public static void generateSampleTestFile(String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // US01 sheet'ini oluştur
            Sheet sheet1 = workbook.createSheet("US01");
            createHeaderRow(sheet1);

            // İlk test case'i ekle (6 adım)
            int currentRow = 1;
            currentRow = addTestCase(sheet1, currentRow, "US01", "TC01",
                    "Login işlemi başarılı olmalıdır",
                    "Kullanıcı kayıtlı olmalıdır",
                    new String[][] {
                            { "1", "Go to Site", "https://www.allovercommerce.com/", "Site homepage opens", "",
                                    "true" },
                            { "2", "Click on Sign in link", "", "Signin window opens", "", "false" },
                            { "3", "Enter email in username box", "test@example.com", "", "", "false" },
                            { "4", "Enter password in password box", "Test123!", "", "", "false" },
                            { "5", "Click Sign in button", "", "", "", "false" },
                            { "6", "Verify successful login", "", "User is logged in", "", "false" }
                    });

            // Boş satır ekle
            currentRow += 2;

            // İkinci test case'i ekle (8 adım)
            currentRow = addTestCase(sheet1, currentRow, "US01", "TC02",
                    "Yanlış şifre ile giriş başarısız olmalıdır",
                    "Kullanıcı kayıtlı olmalıdır",
                    new String[][] {
                            { "1", "Go to Site", "https://www.allovercommerce.com/", "Site homepage opens", "",
                                    "true" },
                            { "2", "Click on Sign in link", "", "Signin window opens", "", "false" },
                            { "3", "Enter email in username box", "test@example.com", "", "", "false" },
                            { "4", "Enter wrong password", "WrongPass123!", "", "", "false" },
                            { "5", "Click Sign in button", "", "", "", "false" },
                            { "6", "Verify error message", "", "Wrong password message appears",
                                    "BUG: Hata mesajı görünmüyor", "false" },
                            { "7", "Wait for 5 seconds", "", "", "", "false" },
                            { "8", "Refresh the page", "", "Page refreshes", "", "false" }
                    });

            // US02 sheet'ini oluştur
            Sheet sheet2 = workbook.createSheet("US02");
            createHeaderRow(sheet2);

            // US02'nin test case'lerini ekle
            currentRow = 1;
            currentRow = addTestCase(sheet2, currentRow, "US02", "TC08",
                    "The username must not be clicked on the SIGN UP button without entering the email address",
                    "Access to the Site",
                    new String[][] {
                            { "1", "Go to Site", "https://www.allovercommerce.com/", "Site homepage opens", "",
                                    "true" },
                            { "2", "Click on Sign in link", "", "Signin window opens", "", "false" },
                            { "3", "Enter the email or username registered in the username or email address box",
                                    "kajetan.juanito@feerock.com or Aa1", "", "", "false" },
                            { "4", "Enter a data into the password box", "", "", "", "false" },
                            { "5", "Click here", "", "", "", "false" },
                            { "6", "Verify that the input process does not occur", "",
                                    "If the input process does not happen, \" Fill out this area.\" error is assigned",
                                    "", "false" }
                    });

            // Renkleri ve stil ayarlarını uygula
            applyStyles(workbook);

            // Dosyayı kaydet
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }

    private static void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = { "User Story ID", "Test Case ID", "Test Objective", "Pre-Condition", "No", "Steps",
                "Test Data", "Expected Result", "Actual Result", "Home" };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Sütun genişliklerini ayarla
        sheet.setColumnWidth(0, 3000); // User Story ID
        sheet.setColumnWidth(1, 3000); // Test Case ID
        sheet.setColumnWidth(2, 8000); // Test Objective
        sheet.setColumnWidth(3, 6000); // Pre-Condition
        sheet.setColumnWidth(4, 1500); // No
        sheet.setColumnWidth(5, 10000); // Steps
        sheet.setColumnWidth(6, 8000); // Test Data
        sheet.setColumnWidth(7, 8000); // Expected Result
        sheet.setColumnWidth(8, 8000); // Actual Result
        sheet.setColumnWidth(9, 3000); // Home
    }

    private static int addTestCase(Sheet sheet, int startRow, String userStoryId, String testCaseId,
            String objective, String preCondition, String[][] steps) {

        // İlk iki hücreyi birleştir
        int lastRow = startRow + steps.length - 1;
        sheet.addMergedRegion(new CellRangeAddress(startRow, lastRow, 0, 0)); // User Story ID
        sheet.addMergedRegion(new CellRangeAddress(startRow, lastRow, 1, 1)); // Test Case ID
        sheet.addMergedRegion(new CellRangeAddress(startRow, lastRow, 2, 2)); // Test Objective
        sheet.addMergedRegion(new CellRangeAddress(startRow, lastRow, 3, 3)); // Pre-Condition

        // Test case bilgilerini ekle
        Row firstRow = sheet.createRow(startRow);
        firstRow.createCell(0).setCellValue(userStoryId);
        firstRow.createCell(1).setCellValue(testCaseId);
        firstRow.createCell(2).setCellValue(objective);
        firstRow.createCell(3).setCellValue(preCondition);

        // Adımları ekle
        for (int i = 0; i < steps.length; i++) {
            Row row = (i == 0) ? firstRow : sheet.createRow(startRow + i);

            row.createCell(4).setCellValue(steps[i][0]); // No
            row.createCell(5).setCellValue(steps[i][1]); // Steps
            row.createCell(6).setCellValue(steps[i][2]); // Test Data
            row.createCell(7).setCellValue(steps[i][3]); // Expected Result
            row.createCell(8).setCellValue(steps[i][4]); // Actual Result
            row.createCell(9).setCellValue(Boolean.parseBoolean(steps[i][5])); // Home
        }

        return lastRow + 1;
    }

    private static void applyStyles(Workbook workbook) {
        // Başlık stili
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Yeşil hücre stili (Home sütunu için)
        CellStyle greenStyle = workbook.createCellStyle();
        greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Sarı hücre stili (highlight için)
        CellStyle yellowStyle = workbook.createCellStyle();
        yellowStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Turuncu hücre stili (BUG notları için)
        CellStyle orangeStyle = workbook.createCellStyle();
        orangeStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        orangeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Stilleri uygula
        for (Sheet sheet : workbook) {
            // Başlık stilini uygula
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for (Cell cell : headerRow) {
                    cell.setCellStyle(headerStyle);
                }
            }

            // Diğer hücrelere stilleri uygula
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue; // Başlık satırını atla

                // Home sütunundaki true değerleri için yeşil stil
                Cell homeCell = row.getCell(9);
                if (homeCell != null && homeCell.getCellType() == CellType.BOOLEAN && homeCell.getBooleanCellValue()) {
                    homeCell.setCellStyle(greenStyle);
                }

                // "BUG:" içeren hücreler için turuncu stil
                Cell actualResultCell = row.getCell(8);
                if (actualResultCell != null && actualResultCell.getCellType() == CellType.STRING
                        && actualResultCell.getStringCellValue().contains("BUG:")) {
                    actualResultCell.setCellStyle(orangeStyle);
                }

                // Bazı adım hücrelerine sarı highlight
                Cell stepsCell = row.getCell(5);
                if (stepsCell != null && stepsCell.getCellType() == CellType.STRING
                        && stepsCell.getStringCellValue().contains("Click")) {
                    stepsCell.setCellStyle(yellowStyle);
                }
            }
        }
    }
}
