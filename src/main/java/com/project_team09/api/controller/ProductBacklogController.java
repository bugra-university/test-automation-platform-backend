package com.project_team09.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.project_team09.api.service.ExcelParserService;
import com.project_team09.api.service.FileTrackingService;
import com.project_team09.api.service.FileStorageService;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

/**
 * Controller for managing Product Backlog related operations
 */
@RestController
@RequestMapping("/api/product-backlog")
@CrossOrigin(origins = "*")
public class ProductBacklogController {

    private final ExcelParserService excelParserService;
    private final FileStorageService fileStorageService;

    @Autowired(required = false)
    private FileTrackingService fileTrackingService;

    @Autowired
    public ProductBacklogController(ExcelParserService excelParserService, FileStorageService fileStorageService) {
        this.excelParserService = excelParserService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("controller", "ProductBacklogController");
        return ResponseEntity.ok(response);
    }

    /**
     * Get product backlog statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getProductBacklogStats() {
        try {
            Map<String, Object> statistics = excelParserService.getTableStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch product backlog statistics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Save Excel file to database
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveToDatabase(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please upload a file");
                return ResponseEntity.badRequest().body(error);
            }

            String contentType = file.getContentType();
            if (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                    !contentType.equals("application/vnd.ms-excel")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please upload an Excel file (.xlsx or .xls)");
                return ResponseEntity.badRequest().body(error);
            }

            var savedTestCases = excelParserService.parseAndSaveExcelFile(file);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "File saved to database successfully");
            response.put("testCaseCount", savedTestCases.size());
            response.put("fileName", file.getOriginalFilename());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to save file to database: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get list of uploaded Excel files from database
     */
    @GetMapping("/files")
    public ResponseEntity<?> getUploadedFiles() {
        try {
            if (fileTrackingService != null) {
                List<Map<String, Object>> files = fileTrackingService.getAllFileRecords();
                return ResponseEntity.ok(files);
            } else {
                // Fallback: return empty list if FileTrackingService is not available
                return ResponseEntity.ok(List.of());
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch uploaded files: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get list of physical Excel files from server storage
     */
    @GetMapping("/physical-files")
    public ResponseEntity<?> getPhysicalFiles() {
        try {
            List<Map<String, Object>> physicalFiles = new ArrayList<>();
            Path baseDir = fileStorageService.getFileStorageLocation();

            // Check both uploads/ and uploads/excel-files/ directories
            Path[] pathsToCheck = {
                    baseDir, // uploads/
                    baseDir.resolve("excel-files") // uploads/excel-files/
            };

            for (Path uploadDir : pathsToCheck) {
                if (Files.exists(uploadDir)) {
                    Files.list(uploadDir)
                            .filter(Files::isRegularFile)
                            .filter(path -> path.toString().toLowerCase().endsWith(".xlsx") ||
                                    path.toString().toLowerCase().endsWith(".xls"))
                            .forEach(path -> {
                                try {
                                    Map<String, Object> fileInfo = new HashMap<>();
                                    fileInfo.put("fileName", path.getFileName().toString());
                                    fileInfo.put("size", Files.size(path));
                                    fileInfo.put("lastModified", Files.getLastModifiedTime(path).toString());
                                    physicalFiles.add(fileInfo);
                                } catch (IOException e) {
                                    // Skip files that can't be read
                                }
                            });
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("files", physicalFiles);
            response.put("totalFiles", physicalFiles.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to list physical files: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Delete physical Excel file from server only (keep database data)
     */
    @DeleteMapping("/delete-file/{fileName}")
    public ResponseEntity<?> deletePhysicalFile(@PathVariable String fileName) {
        try {
            // Check if file exists
            if (!fileStorageService.fileExists(fileName)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "File '" + fileName + "' not found on server");
                return ResponseEntity.notFound().build();
            }

            // Delete only the physical file
            boolean deleted = fileStorageService.deleteFile(fileName);

            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Physical file '" + fileName + "' deleted successfully from server");
                response.put("fileName", fileName);
                response.put("note", "Database test data remains intact");

                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to delete file '" + fileName + "' from server");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete file '" + fileName + "': " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
