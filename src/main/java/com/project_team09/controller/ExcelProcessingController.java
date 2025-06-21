package com.project_team09.controller;

import com.project_team09.service.ExcelParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ExcelProcessingController {

    @Autowired
    private ExcelParsingService excelParsingService;

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
            excelParsingService.parseAndSaveExcel(file, projectId);
            response.put("success", true);
            response.put("message", "File uploaded and data saved successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to parse and save Excel file: " + e.getMessage());
            // Log the full exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 