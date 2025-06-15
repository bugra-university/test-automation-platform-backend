package com.project_team09.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project_team09.api.service.FileTrackingService;
import com.project_team09.api.model.FileTrackingRecord;

import java.util.Map;

@RestController
@RequestMapping("/api/file-tracking")
@CrossOrigin(origins = "*")
public class FileTrackingController {
    @Autowired(required = false)
    private FileTrackingService fileTrackingService;

    /**
     * Simple health check endpoint that doesn't depend on database
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "controller", "FileTrackingController",
                "status", "OK",
                "message", "Controller is loaded and working"));
    }

    @GetMapping("/check/{fileName}")
    public ResponseEntity<?> checkFileStatus(@PathVariable String fileName) {
        if (fileTrackingService == null) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "FileTrackingService is not available"));
        }

        try {
            FileTrackingRecord record = fileTrackingService.getFileRecord(fileName);
            if (record != null) {
                return ResponseEntity.ok(record);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to check file status: " + e.getMessage()));
        }
    }

    @GetMapping("/content/{recordId}")
    public ResponseEntity<?> getFileContent(@PathVariable String recordId) {
        if (fileTrackingService == null) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "FileTrackingService is not available"));
        }

        try {
            Map<String, Object> content = fileTrackingService.getFileContent(recordId);
            if (content != null) {
                return ResponseEntity.ok(content);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get file content: " + e.getMessage()));
        }
    }

    @PostMapping("/update-sync")
    public ResponseEntity<?> updateSyncStatus(@RequestBody Map<String, Object> request) {
        if (fileTrackingService == null) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "FileTrackingService is not available"));
        }

        try {
            String fileName = (String) request.get("fileName");
            String fileHash = (String) request.get("fileHash");
            String contentHash = (String) request.get("contentHash");
            String syncDate = (String) request.get("syncDate");

            fileTrackingService.updateSyncStatus(fileName, fileHash, contentHash, syncDate);

            return ResponseEntity.ok(Map.of("message", "Sync status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to update sync status: " + e.getMessage()));
        }
    }

    @PostMapping("/store-content")
    public ResponseEntity<?> storeFileContent(@RequestBody Map<String, Object> request) {
        if (fileTrackingService == null) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "FileTrackingService is not available"));
        }

        try {
            String fileName = (String) request.get("fileName");
            String fileHash = (String) request.get("fileHash");
            String contentHash = (String) request.get("contentHash");
            @SuppressWarnings("unchecked")
            Map<String, Object> content = (Map<String, Object>) request.get("content");

            String recordId = fileTrackingService.storeFileContent(fileName, fileHash, contentHash, content);

            return ResponseEntity.ok(Map.of("recordId", recordId, "message", "File content stored successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to store file content: " + e.getMessage()));
        }
    }
}
