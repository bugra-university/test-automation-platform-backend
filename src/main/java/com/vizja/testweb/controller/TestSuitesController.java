package com.vizja.testweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.vizja.testweb.service.TestSuitesService;
import com.vizja.testweb.service.TestExecutionService;
import com.vizja.testweb.service.StepTrackingService;
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
    @Autowired
    private StepTrackingService stepTrackingService;

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

    @PostMapping("/{userStoryId}/run")
    public ResponseEntity<Map<String, Object>> runTestSuite(
            @PathVariable Long projectId,
            @PathVariable String userStoryId,
            @RequestBody(required = false) Map<String, Object> config) {
        try {
            boolean isHeadless = config != null ? (Boolean) config.getOrDefault("isHeadless", true) : true;
            String browser = config != null ? (String) config.getOrDefault("browser", "chrome") : "chrome";
            testExecutionService.executeTestSuiteAsync(projectId, userStoryId, isHeadless, browser);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test suite execution started");
            response.put("runResult", Map.of(
                    "userStoryId", userStoryId,
                    "status", "started",
                    "startTime", java.time.LocalDateTime.now(),
                    "configuration", Map.of(
                            "isHeadless", isHeadless,
                            "browser", browser),
                    "async", true));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to start test suite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

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
            boolean isHeadless = config != null ? (Boolean) config.getOrDefault("isHeadless", true) : true;
            String browser = config != null ? (String) config.getOrDefault("browser", "chrome") : "chrome";
            System.out.println("[API] Extracted config - isHeadless: " + isHeadless + ", browser: " + browser);
            System.out.println("[API] testExecutionService is null? " + (testExecutionService == null));
            System.out.println("[API] About to call testExecutionService.executeTestCaseAsync...");
            CompletableFuture<Map<String, Object>> future = testExecutionService.executeTestCaseAsync(projectId,
                    testCaseId, isHeadless, browser);
            System.out.println("[API] future is null? " + (future == null));
            if (future != null) {
                future.exceptionally(throwable -> {
                    System.out.println("[API] ❌ Exception in async execution: " + throwable.getMessage());
                    throwable.printStackTrace();
                    return null;
                });
                future.thenAccept(result -> {
                    if (result != null && result.containsKey("testRunId")) {
                        System.out
                                .println("[API] ✅ Test execution completed with testRunId: " + result.get("testRunId"));
                    }
                });
            }
            System.out.println("[API] executeTestCaseAsync called successfully, returning response...");
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test case execution started");
            Map<String, Object> runResult = new HashMap<>();
            runResult.put("testCaseId", testCaseId);
            runResult.put("status", "started");
            runResult.put("startTime", java.time.LocalDateTime.now());
            runResult.put("configuration", Map.of(
                    "isHeadless", isHeadless,
                    "browser", browser));
            runResult.put("async", true);
            runResult.put("pollHint", "Check latest test runs for this project to get testRunId for polling");
            response.put("runResult", runResult);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to start test case: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

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

    @GetMapping("/reports/latest/download")
    public ResponseEntity<Resource> downloadLatestReport(@PathVariable Long projectId) {
        try {
            File reportsDir = new File("TestOutput/reports");
            if (!reportsDir.exists() || !reportsDir.isDirectory()) {
                return ResponseEntity.notFound().build();
            }
            File[] reportFiles = reportsDir
                    .listFiles((dir, name) -> name.startsWith("extentReport__") && name.endsWith(".html"));
            if (reportFiles == null || reportFiles.length == 0) {
                return ResponseEntity.notFound().build();
            }
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

    @GetMapping("/test-runs/{testRunId}/status")
    public ResponseEntity<Map<String, Object>> getTestRunStatus(
            @PathVariable Long projectId,
            @PathVariable Long testRunId) {
        try {
            Map<String, Object> status = testExecutionService.getTestRunStatus(testRunId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("testRunStatus", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get test run status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/test-runs/latest")
    public ResponseEntity<Map<String, Object>> getLatestTestRuns(@PathVariable Long projectId) {
        try {
            List<Map<String, Object>> latestRuns = testExecutionService.getLatestTestRuns(projectId, 10);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("testRuns", latestRuns);
            response.put("count", latestRuns.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get latest test runs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTestEvents(@PathVariable Long projectId) {
        SseEmitter emitter = new SseEmitter(300000L);
        try {
            testExecutionService.registerEventStream(projectId, emitter);
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to test execution events for project " + projectId));
            emitter.onCompletion(() -> {
                testExecutionService.unregisterEventStream(projectId, emitter);
            });
            emitter.onTimeout(() -> {
                testExecutionService.unregisterEventStream(projectId, emitter);
            });
            emitter.onError((ex) -> {
                testExecutionService.unregisterEventStream(projectId, emitter);
            });
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @GetMapping(value = "/steps/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStepEvents(@PathVariable Long projectId) {
        SseEmitter emitter = new SseEmitter(300000L);
        try {
            stepTrackingService.registerStepEventStream(projectId, emitter);
            emitter.send(SseEmitter.event()
                    .name("step_connected")
                    .data("Connected to step execution events for project " + projectId));
            emitter.onCompletion(() -> {
                stepTrackingService.unregisterStepEventStream(projectId, emitter);
            });
            emitter.onTimeout(() -> {
                stepTrackingService.unregisterStepEventStream(projectId, emitter);
            });
            emitter.onError((ex) -> {
                stepTrackingService.unregisterStepEventStream(projectId, emitter);
            });
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @PostMapping("/{testCaseId}/steps/{stepNumber}/start")
    public ResponseEntity<Map<String, Object>> startStep(
            @PathVariable Long projectId,
            @PathVariable Long testCaseId,
            @PathVariable Integer stepNumber,
            @RequestBody(required = false) Map<String, Object> stepData) {
        try {
            String stepDescription = stepData != null
                    ? (String) stepData.getOrDefault("stepDescription", "Step " + stepNumber)
                    : "Step " + stepNumber;
            String executionId = stepData != null
                    ? (String) stepData.getOrDefault("executionId", "manual_" + System.currentTimeMillis())
                    : "manual_" + System.currentTimeMillis();
            stepTrackingService.startStep(projectId, testCaseId, stepNumber, stepDescription, executionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Step started successfully");
            response.put("stepInfo", Map.of(
                    "testCaseId", testCaseId,
                    "stepNumber", stepNumber,
                    "stepDescription", stepDescription,
                    "status", "running",
                    "executionId", executionId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to start step: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/{testCaseId}/steps/{stepNumber}/complete")
    public ResponseEntity<Map<String, Object>> completeStep(
            @PathVariable Long projectId,
            @PathVariable Long testCaseId,
            @PathVariable Integer stepNumber,
            @RequestBody(required = false) Map<String, Object> stepData) {
        try {
            String executionId = stepData != null
                    ? (String) stepData.getOrDefault("executionId", "manual_" + System.currentTimeMillis())
                    : "manual_" + System.currentTimeMillis();
            stepTrackingService.completeStep(projectId, testCaseId, stepNumber, executionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Step completed successfully");
            response.put("stepInfo", Map.of(
                    "testCaseId", testCaseId,
                    "stepNumber", stepNumber,
                    "status", "passed",
                    "executionId", executionId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to complete step: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/{testCaseId}/steps/{stepNumber}/fail")
    public ResponseEntity<Map<String, Object>> failStep(
            @PathVariable Long projectId,
            @PathVariable Long testCaseId,
            @PathVariable Integer stepNumber,
            @RequestBody(required = false) Map<String, Object> stepData) {
        try {
            String errorMessage = stepData != null ? (String) stepData.getOrDefault("errorMessage", "Step failed")
                    : "Step failed";
            String executionId = stepData != null
                    ? (String) stepData.getOrDefault("executionId", "manual_" + System.currentTimeMillis())
                    : "manual_" + System.currentTimeMillis();
            stepTrackingService.failStep(projectId, testCaseId, stepNumber, errorMessage, executionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Step failed successfully");
            response.put("stepInfo", Map.of(
                    "testCaseId", testCaseId,
                    "stepNumber", stepNumber,
                    "status", "failed",
                    "errorMessage", errorMessage,
                    "executionId", executionId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fail step: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
