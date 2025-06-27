package com.project_team09.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportsController {

    private static final String REPORTS_DIR = "TestOutput/reports";

    /**
     * Get all available reports for a project
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getReports(@PathVariable Long projectId) {
        try {
            File reportsDir = new File(REPORTS_DIR);
            if (!reportsDir.exists() || !reportsDir.isDirectory()) {
                return ResponseEntity.ok(createResponse(true, "No reports directory found", Collections.emptyList(), 0));
            }

            // Get all HTML report files
            File[] reportFiles = reportsDir.listFiles((dir, name) -> 
                name.startsWith("extentReport_") && name.endsWith(".html"));
            
            if (reportFiles == null || reportFiles.length == 0) {
                return ResponseEntity.ok(createResponse(true, "No reports found", Collections.emptyList(), 0));
            }

            // Convert files to report data
            List<Map<String, Object>> reports = Arrays.stream(reportFiles)
                .map(this::convertFileToReportData)
                .filter(Objects::nonNull)
                .sorted((a, b) -> ((Date) b.get("createdDate")).compareTo((Date) a.get("createdDate"))) // Latest first
                .collect(Collectors.toList());

            return ResponseEntity.ok(createResponse(true, "Reports retrieved successfully", reports, reports.size()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(false, "Failed to retrieve reports: " + e.getMessage(), Collections.emptyList(), 0));
        }
    }

    /**
     * Get specific report content (HTML)
     */
    @GetMapping("/{reportId}/content")
    public ResponseEntity<String> getReportContent(@PathVariable Long projectId, @PathVariable String reportId) {
        try {
            File reportFile = new File(REPORTS_DIR, reportId);
            
            if (!reportFile.exists() || !reportFile.isFile()) {
                return ResponseEntity.notFound().build();
            }

            String htmlContent = Files.readString(reportFile.toPath());
            
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("<html><body><h1>Error loading report: " + e.getMessage() + "</h1></body></html>");
        }
    }

    /**
     * Download specific report
     */
    @GetMapping("/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long projectId, @PathVariable String reportId) {
        try {
            File reportFile = new File(REPORTS_DIR, reportId);
            
            if (!reportFile.exists() || !reportFile.isFile()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(reportFile);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + reportFile.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(reportFile.length())
                .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete specific report
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> deleteReport(@PathVariable Long projectId, @PathVariable String reportId) {
        try {
            File reportFile = new File(REPORTS_DIR, reportId);
            
            if (!reportFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            boolean deleted = reportFile.delete();
            
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Report deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Failed to delete report");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get report statistics/summary
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getReportsStatistics(@PathVariable Long projectId) {
        try {
            File reportsDir = new File(REPORTS_DIR);
            if (!reportsDir.exists()) {
                return ResponseEntity.ok(createStatisticsResponse(0, 0, 0, 0));
            }

            File[] reportFiles = reportsDir.listFiles((dir, name) -> 
                name.startsWith("extentReport_") && name.endsWith(".html"));
            
            if (reportFiles == null) {
                return ResponseEntity.ok(createStatisticsResponse(0, 0, 0, 0));
            }

            int totalReports = reportFiles.length;
            int passedReports = 0;
            int failedReports = 0;
            long totalSizeBytes = 0;

            for (File file : reportFiles) {
                totalSizeBytes += file.length();
                
                // Simple heuristic: if file contains "1 test(s) passed" and "0 test(s) failed", it's passed
                try {
                    String content = Files.readString(file.toPath());
                    if (content.contains("test(s) passed") && !content.contains("test(s) failed") || 
                        (content.contains("0 test(s) failed"))) {
                        passedReports++;
                    } else {
                        failedReports++;
                    }
                } catch (IOException e) {
                    // Skip this file
                }
            }

            return ResponseEntity.ok(createStatisticsResponse(totalReports, passedReports, failedReports, totalSizeBytes));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createStatisticsResponse(0, 0, 0, 0));
        }
    }

    // Helper methods
    private Map<String, Object> convertFileToReportData(File file) {
        try {
            Map<String, Object> reportData = new HashMap<>();
            
            // Basic file info
            reportData.put("id", file.getName());
            reportData.put("fileName", file.getName());
            reportData.put("createdDate", new Date(file.lastModified()));
            reportData.put("fileSize", formatFileSize(file.length()));
            
            // Parse filename for date/time info
            String displayName = parseDisplayName(file.getName());
            reportData.put("displayName", displayName);
            
            // Try to extract test info from HTML content
            try {
                String content = Files.readString(file.toPath());
                Map<String, Object> testInfo = parseTestInfo(content);
                reportData.putAll(testInfo);
            } catch (IOException e) {
                // Set default values if can't read file
                reportData.put("status", "unknown");
                reportData.put("passCount", 0);
                reportData.put("failCount", 0);
                reportData.put("duration", "Unknown");
                reportData.put("userStory", "Unknown");
                reportData.put("testCases", Collections.emptyList());
            }
            
            return reportData;
            
        } catch (Exception e) {
            System.err.println("Error converting file to report data: " + e.getMessage());
            return null;
        }
    }

    private String parseDisplayName(String fileName) {
        // extentReport__04_45_17_25062025.html -> "Report 25.06.2025 04:45:17"
        Pattern pattern = Pattern.compile("extentReport__(.+)\\.html");
        Matcher matcher = pattern.matcher(fileName);
        
        if (matcher.matches()) {
            String timestamp = matcher.group(1);
            // Parse: 04_45_17_25062025 -> 04:45:17 25.06.2025
            String[] parts = timestamp.split("_");
            if (parts.length >= 4) {
                String time = parts[0] + ":" + parts[1] + ":" + parts[2];
                String date = parts[3];
                if (date.length() == 8) {
                    String day = date.substring(0, 2);
                    String month = date.substring(2, 4);
                    String year = date.substring(4, 8);
                    return "Report " + day + "." + month + "." + year + " " + time;
                }
            }
        }
        
        return "Test Report";
    }

    private Map<String, Object> parseTestInfo(String htmlContent) {
        Map<String, Object> info = new HashMap<>();
        
        // Extract test status information
        Pattern passPattern = Pattern.compile("(\\d+) test\\(s\\) passed");
        Pattern failPattern = Pattern.compile("(\\d+) test\\(s\\) failed");
        Pattern durationPattern = Pattern.compile("time-taken[^>]*>([^<]+)</span>");
        Pattern testNamePattern = Pattern.compile("<span class='test-name'>([^<]+)</span>");
        
        Matcher passMatcher = passPattern.matcher(htmlContent);
        Matcher failMatcher = failPattern.matcher(htmlContent);
        Matcher durationMatcher = durationPattern.matcher(htmlContent);
        Matcher testNameMatcher = testNamePattern.matcher(htmlContent);
        
        int passCount = 0;
        int failCount = 0;
        
        if (passMatcher.find()) {
            passCount = Integer.parseInt(passMatcher.group(1));
        }
        
        if (failMatcher.find()) {
            failCount = Integer.parseInt(failMatcher.group(1));
        }
        
        info.put("passCount", passCount);
        info.put("failCount", failCount);
        
        // Determine overall status
        if (failCount > 0) {
            info.put("status", "fail");
        } else if (passCount > 0) {
            info.put("status", "pass");
        } else {
            info.put("status", "unknown");
        }
        
        // Extract duration
        if (durationMatcher.find()) {
            info.put("duration", durationMatcher.group(1));
        } else {
            info.put("duration", "Unknown");
        }
        
        // Extract test names/cases
        List<String> testCases = new ArrayList<>();
        while (testNameMatcher.find()) {
            testCases.add(testNameMatcher.group(1));
        }
        info.put("testCases", testCases);
        
        // Try to determine user story from test names
        String userStory = "Unknown";
        if (!testCases.isEmpty()) {
            String firstTest = testCases.get(0);
            if (firstTest.toLowerCase().contains("kullanıcı kaydı")) {
                userStory = "US01 - User Registration";
            } else if (firstTest.toLowerCase().contains("vendor")) {
                userStory = "Vendor Tests";
            }
            // Add more patterns as needed
        }
        info.put("userStory", userStory);
        
        return info;
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private Map<String, Object> createResponse(boolean success, String message, List<Map<String, Object>> reports, int count) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("reports", reports);
        response.put("count", count);
        return response;
    }

    private Map<String, Object> createStatisticsResponse(int total, int passed, int failed, long totalSize) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("statistics", Map.of(
            "totalReports", total,
            "passedReports", passed,
            "failedReports", failed,
            "totalSize", formatFileSize(totalSize)
        ));
        return response;
    }
} 