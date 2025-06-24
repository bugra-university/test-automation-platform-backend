package com.project_team09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import com.project_team09.service.TestSuitesService;
import com.project_team09.service.TestExecutionService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.io.File;


@RestController
@RequestMapping("/api/projects/{projectId}/test-suites")
@CrossOrigin(origins = "http://localhost:3000")
public class TestSuitesController {

    @Autowired
    private TestSuitesService testSuitesService;

    @Autowired
    private TestExecutionService testExecutionService;

    /**
     * Get all test suites (User Stories with Test Cases) for a project
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTestSuites(@PathVariable Long projectId) {
        try {
            List<Map<String, Object>> testSuites = testSuitesService.getTestSuitesByProject(projectId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("testSuites", testSuites);
            response.put("count", testSuites.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch test suites: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get test cases for a specific user story
     */
    @GetMapping("/{userStoryId}/test-cases")
    public ResponseEntity<Map<String, Object>> getTestCases(
            @PathVariable Long projectId, 
            @PathVariable String userStoryId) {
        try {
            List<Map<String, Object>> testCases = testSuitesService.getTestCasesByUserStory(projectId, userStoryId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("testCases", testCases);
            response.put("userStoryId", userStoryId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch test cases: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get test steps for a specific test case
     */
    @GetMapping("/test-cases/{testCaseId}/steps")
    public ResponseEntity<Map<String, Object>> getTestSteps(
            @PathVariable Long projectId, 
            @PathVariable Long testCaseId) {
        try {
            List<Map<String, Object>> testSteps = testSuitesService.getTestStepsByTestCase(testCaseId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("testSteps", testSteps);
            response.put("testCaseId", testCaseId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch test steps: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Run a test suite (User Story) with configuration - Asynchronous
     */
    @PostMapping("/{userStoryId}/run")
    public ResponseEntity<Map<String, Object>> runTestSuite(
            @PathVariable Long projectId, 
            @PathVariable String userStoryId,
            @RequestBody(required = false) Map<String, Object> config) {
        try {
            // Extract test configuration
            boolean isHeadless = config != null ? (Boolean) config.getOrDefault("isHeadless", true) : true;
            String browser = config != null ? (String) config.getOrDefault("browser", "chrome") : "chrome";
            
            // Start asynchronous execution (fire-and-forget)
            testExecutionService.executeTestSuiteAsync(projectId, userStoryId, isHeadless, browser);
            
            // Return immediately with execution info
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test suite execution started");
            response.put("runResult", Map.of(
                "userStoryId", userStoryId,
                "status", "started",
                "startTime", java.time.LocalDateTime.now(),
                "configuration", Map.of(
                    "isHeadless", isHeadless,
                    "browser", browser
                ),
                "async", true
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to start test suite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Run a specific test case with configuration - Asynchronous
     */
    @PostMapping("/test-cases/{testCaseId}/run")
    public ResponseEntity<Map<String, Object>> runTestCase(
            @PathVariable Long projectId, 
            @PathVariable String testCaseId,
            @RequestBody(required = false) Map<String, Object> config) {
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("[API] TEST CASE RUN REQUEST RECEIVED");
        System.out.println("[API] Project ID: " + projectId);
        System.out.println("[API] Test Case ID: " + testCaseId);
        System.out.println("[API] Config: " + config);
        System.out.println("=".repeat(60));
        
        try {
            // Extract test configuration
            boolean isHeadless = config != null ? (Boolean) config.getOrDefault("isHeadless", true) : true;
            String browser = config != null ? (String) config.getOrDefault("browser", "chrome") : "chrome";
            
            System.out.println("[API] Extracted config - isHeadless: " + isHeadless + ", browser: " + browser);
            System.out.println("[API] testExecutionService is null? " + (testExecutionService == null));
            System.out.println("[API] About to call testExecutionService.executeTestCaseAsync...");
            
            // Start asynchronous execution with proper exception handling
            CompletableFuture<Map<String, Object>> future = testExecutionService.executeTestCaseAsync(projectId, testCaseId, isHeadless, browser);
            System.out.println("[API] future is null? " + (future == null));
            
            // Add exception handling for the async operation
            if (future != null) {
                future.exceptionally(throwable -> {
                    System.out.println("[API] ❌ Exception in async execution: " + throwable.getMessage());
                    throwable.printStackTrace();
                    return null;
                });
            }
            
            System.out.println("[API] executeTestCaseAsync called successfully, returning response...");
            
            // Return immediately with execution info
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test case execution started");
            response.put("runResult", Map.of(
                "testCaseId", testCaseId,
                "status", "started",
                "startTime", java.time.LocalDateTime.now(),
                "configuration", Map.of(
                    "isHeadless", isHeadless,
                    "browser", browser
                ),
                "async", true
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to start test case: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get execution status by execution ID
     */
    @GetMapping("/executions/{executionId}/status")
    public ResponseEntity<Map<String, Object>> getExecutionStatus(
            @PathVariable Long projectId,
            @PathVariable String executionId) {
        try {
            Map<String, Object> status = testExecutionService.getExecutionStatus(executionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("executionStatus", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get execution status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get test suites statistics for a project
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTestSuitesStatistics(@PathVariable Long projectId) {
        try {
            Map<String, Object> statistics = testSuitesService.getTestSuitesStatistics(projectId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch test suites statistics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Download latest test report for a project
     */
    @GetMapping("/reports/latest/download")
    public ResponseEntity<Resource> downloadLatestReport(@PathVariable Long projectId) {
        try {
            // Get the latest report file from TestOutput/reports directory
            File reportsDir = new File("TestOutput/reports");
            if (!reportsDir.exists() || !reportsDir.isDirectory()) {
                return ResponseEntity.notFound().build();
            }

            // Find the latest HTML report file
            File[] reportFiles = reportsDir.listFiles((dir, name) -> 
                name.startsWith("extentReport__") && name.endsWith(".html"));
            
            if (reportFiles == null || reportFiles.length == 0) {
                return ResponseEntity.notFound().build();
            }

            // Get the latest file (by last modified time)
            File latestReport = Arrays.stream(reportFiles)
                .max(Comparator.comparing(File::lastModified))
                .orElse(null);

            if (latestReport == null || !latestReport.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(latestReport);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + latestReport.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(latestReport.length())
                .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 