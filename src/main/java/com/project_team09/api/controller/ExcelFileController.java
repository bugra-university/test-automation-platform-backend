package com.project_team09.api.controller;

import com.project_team09.api.service.FileStorageService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@CrossOrigin(origins = "*")
public class ExcelFileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("controller", "ExcelFileController");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Please upload a file");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            String storedFileName = fileStorageService.storeFile(file);
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("fileName", file.getOriginalFilename());
            responseData.put("storedFileName", storedFileName);
            responseData.put("sheets", processExcelSheets(workbook));
            responseData.put("excelData", convertWorkbookToBase64(workbook));

            workbook.close();
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process Excel file: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadExcelFile(@PathVariable String fileName) {
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity<?> getExcelFileData(@PathVariable String fileName) {
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            Workbook workbook = new XSSFWorkbook(resource.getInputStream());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("fileName", fileName);
            responseData.put("sheets", processExcelSheets(workbook));
            responseData.put("excelData", convertWorkbookToBase64(workbook));

            workbook.close();
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "File not found or could not be processed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    private Map<String, Object> processExcelSheets(Workbook workbook) {
        Map<String, Object> sheetsData = new HashMap<>();
        List<String> sheetNames = new ArrayList<>();
        String productBacklogSheet = null;

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            sheetNames.add(sheetName);

            if (productBacklogSheet == null &&
                    (sheetName.toLowerCase().contains("product backlog") ||
                            sheetName.toLowerCase().contains("user stor") ||
                            isProductBacklogSheetByContent(sheet))) {
                productBacklogSheet = sheetName;
            }
        }

        sheetsData.put("allSheets", sheetNames);
        sheetsData.put("productBacklogSheet", productBacklogSheet != null ? productBacklogSheet : sheetNames.get(0));

        return sheetsData;
    }

    private boolean isProductBacklogSheetByContent(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null)
            return false;

        int columnCount = 0;
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String headerValue = cell.getStringCellValue().toLowerCase();
                if (headerValue.contains("id") || headerValue.contains("user story")) {
                    columnCount++;
                }
                if (headerValue.contains("description") || headerValue.contains("title")) {
                    columnCount++;
                }
                if (headerValue.contains("priority") || headerValue.contains("importance")) {
                    columnCount++;
                }
            }
        }
        return columnCount >= 2;
    }

    private String convertWorkbookToBase64(Workbook workbook) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] bytes = bos.toByteArray();
        bos.close();
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }
}
