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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ExcelProcessingController {

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
        try {
            // Verify project exists
            projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
            
            // Find the latest Excel file for this project
            Optional<ExcelFile> latestExcelFile = excelFileRepository.findByProjectId(projectId)
                .stream()
                .findFirst(); // Assuming the repository returns files in order (newest first)
            
            if (latestExcelFile.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ExcelFile excelFile = latestExcelFile.get();
            String filePath = excelFile.getFilePath();
            
            // Read the file from disk
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileContent = Files.readAllBytes(path);
            ByteArrayResource resource = new ByteArrayResource(fileContent);
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + excelFile.getOriginalFileName() + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileContent.length)
                .body(resource);
                
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
} 