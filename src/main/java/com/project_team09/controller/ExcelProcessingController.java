package com.project_team09.controller;

import com.project_team09.model.ExcelFile;
import com.project_team09.model.Project;
import com.project_team09.repository.ExcelFileRepository;
import com.project_team09.repository.ProjectRepository;
import com.project_team09.service.ExcelParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ExcelProcessingController {

    private static final Logger logger = LoggerFactory.getLogger(ExcelProcessingController.class);

    @Autowired
    private ExcelParsingService excelParsingService;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ExcelFileRepository excelFileRepository;

    @PostMapping("/projects/{projectId}/upload-and-parse")
    public ResponseEntity<Map<String, Object>> uploadAndParseExcel(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "File is empty. Please select a file to upload.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // Find project
            Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
            
            // Parse Excel file
            String result = excelParsingService.parseExcelFile(file, project);
            
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to parse and save Excel file: " + e.getMessage());
            // Log the full exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/projects/{projectId}/latest-excel")
    public ResponseEntity<ByteArrayResource> getLatestExcelFile(@PathVariable Long projectId) {
        logger.info("[EXCEL-API] Getting latest Excel file for project ID: {}", projectId);
        try {
            // Verify project exists
            Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
            logger.info("[EXCEL-API] Project found: {}", project.getName());
            
            // Find the latest Excel file for this project
            List<ExcelFile> allFiles = excelFileRepository.findByProjectId(projectId);
            logger.info("[EXCEL-API] Found {} Excel files for project {}", allFiles.size(), projectId);
            
            Optional<ExcelFile> latestExcelFile = allFiles.stream().findFirst();
            
            if (latestExcelFile.isEmpty()) {
                logger.warn("[EXCEL-API] No Excel file found for project {}", projectId);
                return ResponseEntity.notFound().build();
            }
            
            ExcelFile excelFile = latestExcelFile.get();
            String filePath = excelFile.getFilePath();
            logger.info("[EXCEL-API] Found Excel file: {} at path: {}", excelFile.getOriginalFileName(), filePath);
            
            // Read the file from disk
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                logger.error("[EXCEL-API] File not found on disk: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileContent = Files.readAllBytes(path);
            ByteArrayResource resource = new ByteArrayResource(fileContent);
            logger.info("[EXCEL-API] Successfully read file: {} bytes", fileContent.length);
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + excelFile.getOriginalFileName() + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            
            logger.info("[EXCEL-API] Returning Excel file: {}", excelFile.getOriginalFileName());
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileContent.length)
                .body(resource);
                
        } catch (IOException e) {
            logger.error("[EXCEL-API] IO Error reading Excel file for project {}: {}", projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("[EXCEL-API] General error for project {}: {}", projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/projects/{projectId}/excel")
    public ResponseEntity<Map<String, Object>> deleteProjectExcel(@PathVariable Long projectId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verify project exists
            Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
            
            // Get Excel files for this project
            List<ExcelFile> excelFiles = excelFileRepository.findByProjectId(projectId);
            
            if (excelFiles.isEmpty()) {
                response.put("success", false);
                response.put("message", "No Excel file found for this project");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Delete physical files first
            for (ExcelFile excelFile : excelFiles) {
                try {
                    Path filePath = Paths.get(excelFile.getFilePath());
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                        logger.info("Deleted physical file: {}", filePath);
                    }
                } catch (IOException e) {
                    logger.warn("Could not delete physical file: {}", excelFile.getFilePath(), e);
                }
            }

            // Clean existing data for this project (cascade delete)
            excelParsingService.cleanExistingProjectData(project);
            
            response.put("success", true);
            response.put("message", "Excel file and all related data deleted successfully");
            response.put("deletedFilesCount", excelFiles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete Excel file: " + e.getMessage());
            logger.error("Error deleting Excel file for project: {}", projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 