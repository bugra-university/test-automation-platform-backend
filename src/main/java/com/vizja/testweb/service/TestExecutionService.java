package com.vizja.testweb.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.vizja.testweb.model.TestCase;
import com.vizja.testweb.model.TestRun;
import com.vizja.testweb.model.TestResult;
import com.vizja.testweb.model.TestExecutionMetadata;
import com.vizja.testweb.repository.TestCaseRepository;
import com.vizja.testweb.repository.TestRunRepository;
import com.vizja.testweb.repository.TestResultRepository;
import com.vizja.testweb.repository.TestExecutionMetadataRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.IOException;
@Service
public class TestExecutionService {
    @Autowired
    private TestCaseRepository testCaseRepository;
    @Autowired
    private TestRunRepository testRunRepository;
    @Autowired
    private TestResultRepository testResultRepository;
    @Autowired
    private TestExecutionMetadataRepository testExecutionMetadataRepository;
    @Autowired
    private StepTrackingService stepTrackingService;
    private final Map<String, TestExecutionStatus> runningTests = new ConcurrentHashMap<>();
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> projectEventStreams = new ConcurrentHashMap<>();
    public static class TestExecutionStatus {
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String testOutput;
        private boolean isHeadless;
        private String browser;
        private Long testRunId; 
        private Long testResultId; 
        private String errorMessage;
        private Long durationMs;
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public LocalDateTime getStartTime() {
            return startTime;
        }
        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }
        public LocalDateTime getEndTime() {
            return endTime;
        }
        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }
        public String getTestOutput() {
            return testOutput;
        }
        public void setTestOutput(String testOutput) {
            this.testOutput = testOutput;
        }
        public boolean isHeadless() {
            return isHeadless;
        }
        public void setHeadless(boolean headless) {
            isHeadless = headless;
        }
        public String getBrowser() {
            return browser;
        }
        public void setBrowser(String browser) {
            this.browser = browser;
        }
        public Long getTestRunId() {
            return testRunId;
        }
        public void setTestRunId(Long testRunId) {
            this.testRunId = testRunId;
        }
        public Long getTestResultId() {
            return testResultId;
        }
        public void setTestResultId(Long testResultId) {
            this.testResultId = testResultId;
        }
        public String getErrorMessage() {
            return errorMessage;
        }
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        public Long getDurationMs() {
            return durationMs;
        }
        public void setDurationMs(Long durationMs) {
            this.durationMs = durationMs;
        }
    }
    public void registerEventStream(Long projectId, SseEmitter emitter) {
        projectEventStreams.computeIfAbsent(projectId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        System.out.println("[SSE] Registered event stream for project " + projectId +
                ". Total streams: " + projectEventStreams.get(projectId).size());
    }
    public void unregisterEventStream(Long projectId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = projectEventStreams.get(projectId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                projectEventStreams.remove(projectId);
            }
            System.out.println("[SSE] Unregistered event stream for project " + projectId +
                    ". Remaining streams: " + emitters.size());
        }
    }
    private void sendEventToClients(Long projectId, String eventName, Object data) {
        CopyOnWriteArrayList<SseEmitter> emitters = projectEventStreams.get(projectId);
        if (emitters != null && !emitters.isEmpty()) {
            System.out.println("[SSE] ✅ Sending event '" + eventName + "' to " + emitters.size()
                    + " clients for project " + projectId);
            for (SseEmitter emitter : new CopyOnWriteArrayList<>(emitters)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(eventName)
                            .data(data));
                    System.out.println("[SSE] ✅ Event '" + eventName + "' sent successfully to client");
                } catch (IOException e) {
                    System.out.println("[SSE] ❌ Failed to send event to client, removing emitter: " + e.getMessage());
                    emitters.remove(emitter);
                }
            }
        } else {
            System.out.println(
                    "[SSE] ⚠️ No connected clients for project " + projectId + " to send event '" + eventName + "'");
            System.out.println("[SSE] ⚠️ Total registered projects: " + projectEventStreams.keySet());
        }
    }
    public CompletableFuture<Map<String, Object>> executeTestSuiteAsync(
            Long projectId, String userStoryId, boolean isHeadless, String browser) {
        return executeTestSuiteAsync(projectId, userStoryId, null, isHeadless, browser);
    }
    public CompletableFuture<Map<String, Object>> executeTestSuiteAsync(
            Long projectId, String userStoryId, String[] specificTestCaseIds, boolean isHeadless, String browser) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("[TestExecution] executeTestSuiteAsync CALLED");
        System.out.println("[TestExecution] Parameters: projectId=" + projectId + ", userStoryId=" + userStoryId);
        System.out.println("[TestExecution] Config: isHeadless=" + isHeadless + ", browser=" + browser);
        System.out.println("=".repeat(80));
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[TestExecution] ASYNC THREAD STARTED for user story: " + userStoryId);
            String executionId = generateExecutionId(projectId, userStoryId);
            System.out.println("[TestExecution] Generated execution ID: " + executionId);
            try {
                TestExecutionStatus status = new TestExecutionStatus();
                status.setStatus("RUNNING");
                status.setStartTime(LocalDateTime.now());
                status.setHeadless(isHeadless);
                status.setBrowser(browser);
                runningTests.put(executionId, status);
                sendEventToClients(projectId, "test_suite_started", Map.of(
                        "userStoryId", userStoryId,
                        "executionId", executionId,
                        "status", "RUNNING",
                        "startTime", status.getStartTime(),
                        "configuration", Map.of("isHeadless", isHeadless, "browser", browser)));
                String normalizedUserStoryId = userStoryId.replace("_", "");
                System.out.println("[TestExecution] Searching for test cases: projectId=" + projectId
                        + ", userStoryId='" + userStoryId + "' -> normalized: '" + normalizedUserStoryId + "'");
                List<TestCase> testCases = testCaseRepository.findByProjectIdAndUserStoryId(projectId,
                        normalizedUserStoryId);
                System.out.println("[TestExecution] Found " + testCases.size()
                        + " test cases for normalized user story: " + normalizedUserStoryId);
                if (specificTestCaseIds != null && specificTestCaseIds.length > 0) {
                    Set<String> requestedTestCaseIds = new HashSet<>(Arrays.asList(specificTestCaseIds));
                    List<TestCase> filteredTestCases = testCases.stream()
                            .filter(tc -> requestedTestCaseIds.contains(tc.getTestCaseId()))
                            .collect(java.util.stream.Collectors.toList());
                    System.out.println(
                            "[TestExecution] Filtering test cases: requested " + Arrays.toString(specificTestCaseIds));
                    System.out.println("[TestExecution] Before filtering: " + testCases.size() + " test cases");
                    System.out.println("[TestExecution] After filtering: " + filteredTestCases.size() + " test cases");
                    for (TestCase tc : filteredTestCases) {
                        System.out.println("[TestExecution] Selected test case: " + tc.getTestCaseId() + " - "
                                + tc.getObjective());
                    }
                    testCases = filteredTestCases;
                }
                List<TestCase> allTestCases = testCaseRepository.findByProjectId(projectId);
                System.out.println(
                        "[TestExecution] DEBUG: Total test cases in project " + projectId + ": " + allTestCases.size());
                for (TestCase tc : allTestCases) {
                    System.out.println("[TestExecution] DEBUG: Test case: userStoryId='" + tc.getUserStoryId()
                            + "', testCaseId='" + tc.getTestCaseId() + "'");
                }
                if (testCases.isEmpty()) {
                    status.setStatus("FAILED");
                    status.setEndTime(LocalDateTime.now());
                    status.setTestOutput("No test cases found for user story: " + userStoryId +
                            (specificTestCaseIds != null
                                    ? " with specific test cases: " + Arrays.toString(specificTestCaseIds)
                                    : ""));
                    return createExecutionResult(executionId, status, "No test cases found");
                }
                System.out.println("[TestExecution] Saving execution metadata for " + testCases.size() + " test cases");
                for (TestCase testCase : testCases) {
                    TestExecutionMetadata metadata = new TestExecutionMetadata(
                            projectId,
                            userStoryId,
                            testCase.getTestCaseId(),
                            status.getStartTime());
                    metadata.setStatus("RUNNING");
                    testExecutionMetadataRepository.save(metadata);
                    System.out.println("[TestExecution] Saved metadata: " + testCase.getTestCaseId()
                            + " for execution at " + status.getStartTime());
                }
                TestNG testng = new TestNG();
                System.setProperty("webdriver.headless", String.valueOf(isHeadless));
                System.setProperty("webdriver.browser", browser.toLowerCase());
                System.setProperty("test.userStoryId", userStoryId);
                System.setProperty("test.projectId", String.valueOf(projectId));
                System.setProperty("test.executionId", executionId);
                StringBuilder suiteMapping = new StringBuilder();
                for (TestCase tc : testCases) {
                    String testClassName = mapTestCaseToTestClass(tc);
                    String methodName = mapTestCaseToMethod(tc);
                    if (testClassName != null && methodName != null) {
                        if (suiteMapping.length() > 0)
                            suiteMapping.append(",");
                        suiteMapping.append(testClassName).append("#").append(methodName).append(":")
                                .append(tc.getId());
                    }
                }
                System.setProperty("test.suiteTestCaseMapping", suiteMapping.toString());
                System.out.println("[TestExecution] Suite step-tracking mapping: " + suiteMapping.toString());
                for (TestCase tc : testCases) {
                    stepTrackingService.resetStepsForTestCase(tc.getId());
                }
                XmlSuite suite = createTestSuite(userStoryId, testCases);
                testng.setXmlSuites(Arrays.asList(suite));
                String outputDir = "TestOutput/execution_" + executionId;
                testng.setOutputDirectory(outputDir);
                System.out
                        .println("[TestExecution] Starting test suite: " + userStoryId + " (ID: " + executionId + ")");
                System.out.println("[TestExecution] Browser: " + browser + " (headless: " + isHeadless + ")");
                System.out.println("[TestExecution] Output directory: " + outputDir);
                testng.run();
                status.setStatus("COMPLETED");
                status.setEndTime(LocalDateTime.now());
                status.setTestOutput("Test execution completed. Output: " + outputDir);
                System.out.println("[TestExecution] Updating execution metadata for completion");
                for (TestCase testCase : testCases) {
                    List<TestExecutionMetadata> metadataList = testExecutionMetadataRepository
                            .findByProjectIdAndExecutionTimeBetween(
                                    projectId,
                                    status.getStartTime().minusMinutes(5),
                                    status.getStartTime().plusMinutes(5));
                    for (TestExecutionMetadata metadata : metadataList) {
                        if (metadata.getTestCaseId().equals(testCase.getTestCaseId())) {
                            metadata.setStatus("COMPLETED");
                            String reportsDir = "TestOutput/reports";
                            metadata.setReportFilePath(reportsDir);
                            LocalDateTime execTime = metadata.getExecutionTime();
                            String timePattern = String.format("%02d_%02d_%02d_%02d%02d%04d",
                                    execTime.getHour(),
                                    execTime.getMinute(),
                                    execTime.getSecond(),
                                    execTime.getDayOfMonth(),
                                    execTime.getMonthValue(),
                                    execTime.getYear());
                            metadata.setReportFileName("extentReport__" + timePattern + ".html");
                            testExecutionMetadataRepository.save(metadata);
                            System.out.println(
                                    "[TestExecution] Updated metadata: " + testCase.getTestCaseId() + " -> COMPLETED");
                            System.out.println(
                                    "[TestExecution] Report path: " + reportsDir + "/" + metadata.getReportFileName());
                            break;
                        }
                    }
                }
                System.out.println("[TestExecution] Creating TestRun and TestResult records for Last Run update");
                TestRun testRun = new TestRun(projectId, "Schedule Test Suite: " + userStoryId, "COMPLETED");
                testRun.setTriggeredBy("schedule");
                testRun.setEnvironment("test");
                testRun.setStartTime(status.getStartTime());
                testRun.setEndTime(status.getEndTime());
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("userStoryId", userStoryId);
                parameters.put("browser", browser);
                parameters.put("headless", isHeadless);
                testRun.setParameters(parameters);
                testRun = testRunRepository.save(testRun);
                System.out.println(
                        "[TestExecution] Created TestRun for schedule execution (ID: " + testRun.getId() + ")");
                long executionDurationMs = java.time.Duration.between(status.getStartTime(), status.getEndTime())
                        .toMillis();
                for (TestCase testCase : testCases) {
                    try {
                        TestResult testResult = new TestResult();
                        testResult.setTestRunId(testRun.getId());
                        testResult.setTestCaseId(testCase.getId());
                        testResult.setStatus("PASS"); 
                        testResult.setStartTime(status.getStartTime());
                        testResult.setEndTime(status.getEndTime());
                        testResult.setDurationMs(executionDurationMs);
                        testResult.setErrorMessage(null);
                        testResult.setStackTrace(null);
                        testResult.setCreatedAt(LocalDateTime.now());
                        testResult = testResultRepository.save(testResult);
                        System.out.println("[TestExecution] Created TestResult for " + testCase.getTestCaseId()
                                + " (ID: " + testResult.getId() + ")");
                    } catch (Exception e) {
                        System.err.println("[TestExecution] Error creating TestResult for " + testCase.getTestCaseId()
                                + ": " + e.getMessage());
                    }
                }
                sendEventToClients(projectId, "test_suite_completed", Map.of(
                        "userStoryId", userStoryId,
                        "executionId", executionId,
                        "status", "COMPLETED",
                        "endTime", status.getEndTime(),
                        "duration", java.time.Duration.between(status.getStartTime(), status.getEndTime()).toMillis()));
                System.out.println("[TestExecution] Completed: " + userStoryId);
                return createExecutionResult(executionId, status, "Test suite executed successfully");
            } catch (Exception e) {
                System.err.println("[TestExecution] Error in test suite: " + userStoryId + " - " + e.getMessage());
                e.printStackTrace();
                TestExecutionStatus status = runningTests.get(executionId);
                if (status != null) {
                    status.setStatus("FAILED");
                    status.setEndTime(LocalDateTime.now());
                    status.setTestOutput("Execution failed: " + e.getMessage());
                }
                return createExecutionResult(executionId, status, "Test execution failed: " + e.getMessage());
            } finally {
                System.out.println("[TestExecution] Execution finished for: " + userStoryId);
            }
        });
    }
    public CompletableFuture<Map<String, Object>> executeTestCaseAsync(
            Long projectId, String testCaseId, boolean isHeadless, String browser) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("[TestExecution] SERVICE METHOD CALLED");
        System.out.println("[TestExecution] Parameters: projectId=" + projectId + ", testCaseId=" + testCaseId);
        System.out.println("[TestExecution] Config: isHeadless=" + isHeadless + ", browser=" + browser);
        System.out.println("=".repeat(60));
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[TestExecution] ASYNC THREAD STARTED");
            System.out.println("[TestExecution] Thread: " + Thread.currentThread().getName());
            String executionId = generateExecutionId(projectId, "TC_" + testCaseId);
            try {
                System.out.println("[TestExecution] Step 1: Creating execution status...");
                TestExecutionStatus status = new TestExecutionStatus();
                status.setStatus("RUNNING");
                status.setStartTime(LocalDateTime.now());
                status.setHeadless(isHeadless);
                status.setBrowser(browser);
                runningTests.put(executionId, status);
                System.out.println("[TestExecution] Step 1 OK: Status created");
                sendEventToClients(projectId, "test_case_started", Map.of(
                        "testCaseId", testCaseId,
                        "executionId", executionId,
                        "status", "RUNNING",
                        "startTime", status.getStartTime(),
                        "configuration", Map.of("isHeadless", isHeadless, "browser", browser)));
                TestRun testRun = new TestRun(projectId, "Test Case: " + testCaseId, "RUNNING");
                testRun.setTriggeredBy("manual");
                testRun.setEnvironment("test");
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("testCaseId", testCaseId);
                parameters.put("browser", browser);
                parameters.put("headless", isHeadless);
                testRun.setParameters(parameters);
                testRun = testRunRepository.save(testRun);
                status.setTestRunId(testRun.getId());
                System.out.println(
                        "[TestExecution] Step 1.1 OK: TestRun created in database with ID = " + testRun.getId());
                String baseTestCaseId = (testCaseId != null && testCaseId.contains("-step-"))
                        ? testCaseId.replaceFirst("-step-\\d+$", "").trim()
                        : (testCaseId != null ? testCaseId : "");
                System.out.println("[TestExecution] Step 2: Parsing test case ID '" + baseTestCaseId + "'...");
                List<TestCase> allTestCases = testCaseRepository.findByProjectId(projectId);
                System.out.println("[TestExecution] Step 3 DEBUG: Found " + allTestCases.size()
                        + " test cases for project " + projectId);
                Optional<TestCase> testCaseOpt = Optional.empty();
                if (!baseTestCaseId.isEmpty() && baseTestCaseId.contains("-")) {
                    String[] parts = baseTestCaseId.split("-", 2);
                    String userStoryPart = parts[0].trim();
                    String tcIdPart = parts[1].trim();
                    String normalizedUs = normalizeUserStoryIdForLookup(userStoryPart);
                    testCaseOpt = allTestCases.stream()
                            .filter(tc -> normalizedUs.equals(normalizeUserStoryIdForLookup(tc.getUserStoryId()))
                                    && normalizeTestCaseId(tc.getTestCaseId()).equals(normalizeTestCaseId(tcIdPart)))
                            .findFirst();
                    if (testCaseOpt.isPresent()) {
                        System.out.println("[TestExecution] Step 2 OK: Resolved composite '" + baseTestCaseId
                                + "' -> DB ID = " + testCaseOpt.get().getId());
                    }
                }
                if (!testCaseOpt.isPresent()) {
                    Long testCaseDbId;
                    try {
                        String numericPart = baseTestCaseId.replaceAll(".*TC_?", "").replaceAll("[^0-9]", "");
                        testCaseDbId = numericPart.isEmpty() ? -1L : Long.parseLong(numericPart);
                        System.out.println("[TestExecution] Step 2: Parsed DB ID = " + testCaseDbId);
                        testCaseOpt = testCaseRepository.findById(testCaseDbId)
                                .filter(tc -> tc.getProject().getId().equals(projectId));
                    } catch (NumberFormatException e) {
                        testCaseOpt = Optional.empty();
                    }
                }
                if (!testCaseOpt.isPresent()) {
                    List<TestCase> matchingCases = allTestCases.stream()
                            .filter(tc -> baseTestCaseId.equals(tc.getTestCaseId())
                                    || normalizeTestCaseId(tc.getTestCaseId())
                                            .equals(normalizeTestCaseId(baseTestCaseId)))
                            .collect(java.util.stream.Collectors.toList());
                    if (!matchingCases.isEmpty()) {
                        testCaseOpt = Optional.of(matchingCases.get(0));
                        System.out.println("[TestExecution] Step 2 RECOVERED: Found by testCaseId, DB ID = "
                                + testCaseOpt.get().getId());
                    }
                }
                if (!testCaseOpt.isPresent()) {
                    status.setStatus("FAILED");
                    status.setEndTime(LocalDateTime.now());
                    status.setTestOutput("Test case not found: " + baseTestCaseId);
                    return createExecutionResult(executionId, status, "Test case not found");
                }
                TestCase testCase = testCaseOpt.get();
                System.out.println("[TestExecution] Step 4 OK: Test case found - " + testCase.getTestCaseId());
                TestResult testResult = new TestResult(testRun.getId(), testCase.getId(), "RUNNING");
                testResult = testResultRepository.save(testResult);
                status.setTestResultId(testResult.getId());
                System.out.println(
                        "[TestExecution] Step 4.1 OK: TestResult created in database with ID = " + testResult.getId());
                Long actualTestCaseDbId = testCase.getId();
                System.out.println(
                        "[TestExecution] Step 4.2: Using actual DB ID = " + actualTestCaseDbId + " for step tracking");
                System.setProperty("webdriver.headless", String.valueOf(isHeadless));
                System.setProperty("webdriver.browser", browser.toLowerCase());
                System.setProperty("test.testCaseId", String.valueOf(testCaseId));
                System.setProperty("test.testCaseDbId", String.valueOf(actualTestCaseDbId));
                System.setProperty("test.projectId", String.valueOf(projectId));
                System.setProperty("test.executionId", executionId);
                System.setProperty("test.userStoryId", testCase.getUserStoryId());
                System.setProperty("test.methodName",
                        mapTestCaseToMethod(testCase) != null ? mapTestCaseToMethod(testCase) : "unknown");
                stepTrackingService.resetStepsForTestCase(actualTestCaseDbId);
                TestNG testng = new TestNG();
                XmlSuite suite = createSingleTestCaseSuite(testCase);
                testng.setXmlSuites(Arrays.asList(suite));
                try {
                    Class<?> listenerClass = Class.forName("com.vizja.testweb.utilities.EnhancedTestListener");
                    Object listener = listenerClass.getDeclaredConstructor().newInstance();
                    testng.addListener((org.testng.ITestNGListener) listener);
                    System.out.println("[TestExecution] ✅ EnhancedTestListener added to TestNG");
                } catch (Exception e) {
                    System.err.println("[TestExecution] ⚠️ Failed to add EnhancedTestListener: " + e.getMessage());
                    e.printStackTrace();
                }
                String outputDir = "TestOutput/execution_" + executionId;
                testng.setOutputDirectory(outputDir);
                System.out.println("\n" + "=".repeat(60));
                System.out.println("[TestExecution] STARTING TEST CASE EXECUTION");
                System.out.println("[TestExecution] Test Case ID: " + testCaseId);
                System.out.println("[TestExecution] Execution ID: " + executionId);
                System.out.println("[TestExecution] Browser: " + browser + " (headless: " + isHeadless + ")");
                System.out.println("[TestExecution] Test case details: " + testCase.getTestCaseId() + " - "
                        + testCase.getObjective());
                System.out.println("[TestExecution] Generated suite: " + suite.getName());
                System.out.println("[TestExecution] Test classes count: "
                        + (suite.getTests().isEmpty() ? 0 : suite.getTests().get(0).getXmlClasses().size()));
                System.out.println("=".repeat(60));
                if (!suite.getTests().isEmpty() && !suite.getTests().get(0).getXmlClasses().isEmpty()) {
                    System.out.println(
                            "[TestExecution] Test class: " + suite.getTests().get(0).getXmlClasses().get(0).getName());
                } else {
                    System.out.println("[TestExecution] WARNING: No test classes found for test case: " + testCaseId);
                    status.setStatus("FAILED");
                    LocalDateTime failEndTime = LocalDateTime.now();
                    status.setEndTime(failEndTime);
                    status.setTestOutput("No test classes found for test case: " + testCaseId);
                    long failDurationMs = java.time.Duration.between(status.getStartTime(), failEndTime).toMillis();
                    status.setDurationMs(failDurationMs);
                    sendEventToClients(projectId, "test_case_completed", Map.of(
                            "testCaseId", testCaseId,
                            "executionId", executionId,
                            "status", "FAILED",
                            "endTime", failEndTime,
                            "duration", failDurationMs,
                            "success", false));
                    try {
                        TestRun tr = testRunRepository.findById(status.getTestRunId()).orElse(null);
                        if (tr != null) {
                            tr.setStatus("FAILED");
                            tr.setEndTime(failEndTime);
                            testRunRepository.save(tr);
                        }
                        TestResult trr = testResultRepository.findById(status.getTestResultId()).orElse(null);
                        if (trr != null) {
                            trr.markAsCompleted(false);
                            trr.setErrorMessage("No test classes mapped for this test case");
                            testResultRepository.save(trr);
                        }
                    } catch (Exception e) {
                        System.err.println("[TestExecution] DB update on no-class failure: " + e.getMessage());
                    }
                    return createExecutionResult(executionId, status, "No test classes mapped for this test case");
                }
                System.out.println("[TestExecution] About to run TestNG...");
                testng.run();
                System.out.println("[TestExecution] TestNG execution completed.");
                boolean testPassed = !testng.hasFailure();
                if (testng.hasFailure()) {
                    System.out.println("[TestExecution] ❌ TestNG reported failures");
                    status.setStatus("FAILED");
                } else {
                    System.out.println("[TestExecution] ✅ TestNG completed successfully");
                    status.setStatus("COMPLETED");
                }
                LocalDateTime endTime = LocalDateTime.now();
                status.setEndTime(endTime);
                status.setTestOutput("Test case execution completed. Output: " + outputDir);
                long durationMs = java.time.Duration.between(status.getStartTime(), endTime).toMillis();
                status.setDurationMs(durationMs);
                sendEventToClients(projectId, "test_case_completed", Map.of(
                        "testCaseId", testCaseId,
                        "executionId", executionId,
                        "status", testPassed ? "COMPLETED" : "FAILED",
                        "endTime", endTime,
                        "duration", durationMs,
                        "success", testPassed));
                try {
                    TestRun updatedTestRun = testRunRepository.findById(status.getTestRunId()).orElse(null);
                    if (updatedTestRun != null) {
                        updatedTestRun.setStatus(testPassed ? "COMPLETED" : "FAILED");
                        updatedTestRun.setEndTime(endTime);
                        testRunRepository.save(updatedTestRun);
                        System.out.println("[TestExecution] ✅ TestRun updated in database");
                    }
                    TestResult updatedTestResult = testResultRepository.findById(status.getTestResultId()).orElse(null);
                    if (updatedTestResult != null) {
                        updatedTestResult.markAsCompleted(testPassed);
                        if (!testPassed) {
                            updatedTestResult.setErrorMessage("Test execution failed - check TestNG output");
                        }
                        testResultRepository.save(updatedTestResult);
                        System.out.println("[TestExecution] ✅ TestResult updated in database - Status: " +
                                (testPassed ? "PASS" : "FAIL"));
                    }
                    stepTrackingService.markRemainingRunningStepsAsCompleted(testCase.getId(),
                            testPassed ? "passed" : "failed");
                } catch (Exception dbError) {
                    System.err.println("[TestExecution] ⚠️ Database update failed: " + dbError.getMessage());
                    dbError.printStackTrace();
                }
                System.out.println("[TestExecution] Completed: " + testCaseId);
                return createExecutionResult(executionId, status, "Test case executed successfully");
            } catch (Exception e) {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("[TestExecution] ❌ CRITICAL ERROR OCCURRED");
                System.out.println("[TestExecution] Test Case ID: " + testCaseId);
                System.out.println("[TestExecution] Execution ID: " + executionId);
                System.out.println("[TestExecution] Error Type: " + e.getClass().getSimpleName());
                System.out.println("[TestExecution] Error Message: " + e.getMessage());
                System.out.println("=".repeat(60));
                System.out.println("[TestExecution] Full Stack Trace:");
                e.printStackTrace();
                System.out.println("=".repeat(60));
                TestExecutionStatus status = runningTests.get(executionId);
                if (status != null) {
                    status.setStatus("FAILED");
                    LocalDateTime errorEndTime = LocalDateTime.now();
                    status.setEndTime(errorEndTime);
                    status.setTestOutput("Execution failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    status.setErrorMessage(e.getMessage());
                    try {
                        if (status.getTestRunId() != null) {
                            TestRun errorTestRun = testRunRepository.findById(status.getTestRunId()).orElse(null);
                            if (errorTestRun != null) {
                                errorTestRun.setStatus("FAILED");
                                errorTestRun.setEndTime(errorEndTime);
                                testRunRepository.save(errorTestRun);
                                System.out.println("[TestExecution] ❌ TestRun marked as FAILED in database");
                            }
                        }
                        if (status.getTestResultId() != null) {
                            TestResult errorTestResult = testResultRepository.findById(status.getTestResultId())
                                    .orElse(null);
                            if (errorTestResult != null) {
                                errorTestResult.markAsCompleted(false, e.getMessage(), e.getStackTrace()[0].toString());
                                testResultRepository.save(errorTestResult);
                                System.out.println("[TestExecution] ❌ TestResult marked as FAILED in database");
                            }
                        }
                    } catch (Exception dbError) {
                        System.err.println("[TestExecution] ⚠️ Error updating database during failure handling: "
                                + dbError.getMessage());
                    }
                    long durationMs = java.time.Duration.between(status.getStartTime(), errorEndTime).toMillis();
                    sendEventToClients(projectId, "test_case_completed", Map.of(
                            "testCaseId", testCaseId,
                            "executionId", executionId,
                            "status", "FAILED",
                            "endTime", errorEndTime,
                            "duration", durationMs,
                            "success", false));
                }
                return createExecutionResult(executionId, status, "Test case execution failed: " + e.getMessage());
            } finally {
                System.out.println("[TestExecution] Execution finished for test case: " + testCaseId);
            }
        });
    }
    public Map<String, Object> getExecutionStatus(String executionId) {
        TestExecutionStatus status = runningTests.get(executionId);
        if (status == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("found", false);
            result.put("message", "Execution not found");
            return result;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("found", true);
        result.put("status", status.getStatus());
        result.put("startTime", status.getStartTime());
        result.put("endTime", status.getEndTime());
        result.put("output", status.getTestOutput());
        result.put("durationMs", status.getDurationMs());
        result.put("errorMessage", status.getErrorMessage());
        result.put("testRunId", status.getTestRunId());
        result.put("testResultId", status.getTestResultId());
        result.put("configuration", Map.of(
                "isHeadless", status.isHeadless(),
                "browser", status.getBrowser()));
        return result;
    }
    public Map<String, Object> getTestRunStatus(Long testRunId) {
        Optional<TestRun> testRunOpt = testRunRepository.findById(testRunId);
        if (!testRunOpt.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("found", false);
            result.put("message", "Test run not found");
            return result;
        }
        TestRun testRun = testRunOpt.get();
        Map<String, Object> result = new HashMap<>();
        result.put("found", true);
        result.put("id", testRun.getId());
        result.put("status", testRun.getStatus());
        result.put("name", testRun.getName());
        result.put("startTime", testRun.getStartTime());
        result.put("endTime", testRun.getEndTime());
        result.put("durationMs", testRun.getDurationMs());
        result.put("triggeredBy", testRun.getTriggeredBy());
        result.put("environment", testRun.getEnvironment());
        result.put("parameters", testRun.getParameters());
        List<TestResult> testResults = testResultRepository.findByTestRunIdOrderByCreatedAtDesc(testRunId);
        result.put("testResults", testResults.stream().map(tr -> {
            Map<String, Object> trMap = new HashMap<>();
            trMap.put("id", tr.getId());
            trMap.put("testCaseId", tr.getTestCaseId());
            trMap.put("status", tr.getStatus());
            trMap.put("startTime", tr.getStartTime());
            trMap.put("endTime", tr.getEndTime());
            trMap.put("durationMs", tr.getDurationMs());
            trMap.put("errorMessage", tr.getErrorMessage());
            return trMap;
        }).collect(java.util.stream.Collectors.toList()));
        return result;
    }
    public List<Map<String, Object>> getLatestTestRuns(Long projectId, int limit) {
        List<TestRun> testRuns = testRunRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        return testRuns.stream()
                .limit(limit)
                .map(testRun -> {
                    Map<String, Object> runMap = new HashMap<>();
                    runMap.put("id", testRun.getId());
                    runMap.put("name", testRun.getName());
                    runMap.put("status", testRun.getStatus());
                    runMap.put("startTime", testRun.getStartTime());
                    runMap.put("endTime", testRun.getEndTime());
                    runMap.put("durationMs", testRun.getDurationMs());
                    runMap.put("triggeredBy", testRun.getTriggeredBy());
                    runMap.put("parameters", testRun.getParameters());
                    runMap.put("createdAt", testRun.getCreatedAt());
                    List<TestResult> testResults = testResultRepository
                            .findByTestRunIdOrderByCreatedAtDesc(testRun.getId());
                    runMap.put("testResults", testResults.stream().map(tr -> {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("id", tr.getId());
                        resultMap.put("testCaseId", tr.getTestCaseId());
                        resultMap.put("status", tr.getStatus());
                        resultMap.put("durationMs", tr.getDurationMs());
                        resultMap.put("errorMessage", tr.getErrorMessage());
                        return resultMap;
                    }).collect(java.util.stream.Collectors.toList()));
                    return runMap;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    private String generateExecutionId(Long projectId, String identifier) {
        return projectId + "_" + identifier + "_" + System.currentTimeMillis();
    }
    private Map<String, Object> createExecutionResult(String executionId, TestExecutionStatus status, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("executionId", executionId);
        result.put("status", status.getStatus());
        result.put("startTime", status.getStartTime());
        result.put("endTime", status.getEndTime());
        result.put("message", message);
        result.put("testRunId", status.getTestRunId()); 
        result.put("testResultId", status.getTestResultId()); 
        result.put("configuration", Map.of(
                "isHeadless", status.isHeadless(),
                "browser", status.getBrowser()));
        return result;
    }
    private XmlSuite createTestSuite(String userStoryId, List<TestCase> testCases) {
        XmlSuite suite = new XmlSuite();
        suite.setName("UserStory_" + userStoryId);
        XmlTest test = new XmlTest(suite);
        test.setName("TestSuite_" + userStoryId);
        Map<String, List<TestCase>> testCasesByClass = new HashMap<>();
        for (TestCase testCase : testCases) {
            String testClassName = mapTestCaseToTestClass(testCase);
            if (testClassName != null) {
                testCasesByClass.computeIfAbsent(testClassName, k -> new ArrayList<>()).add(testCase);
            }
        }
        List<XmlClass> classes = new ArrayList<>();
        for (Map.Entry<String, List<TestCase>> entry : testCasesByClass.entrySet()) {
            String className = entry.getKey();
            List<TestCase> classTestCases = entry.getValue();
            XmlClass xmlClass = new XmlClass(className);
            List<XmlInclude> includeMethods = new ArrayList<>();
            for (TestCase testCase : classTestCases) {
                String methodName = mapTestCaseToMethod(testCase);
                if (methodName != null) {
                    includeMethods.add(new XmlInclude(methodName));
                    System.out.println("[TestExecution] Including method: " + methodName + " for test case: "
                            + testCase.getTestCaseId());
                }
            }
            if (!includeMethods.isEmpty()) {
                xmlClass.setIncludedMethods(includeMethods);
                classes.add(xmlClass);
                System.out.println("[TestExecution] Configured test class: " + className + " with "
                        + includeMethods.size() + " methods");
            }
        }
        test.setXmlClasses(classes);
        System.out.println("[TestExecution] Created test suite with " + classes.size() + " test classes for "
                + testCases.size() + " test cases");
        return suite;
    }
    private XmlSuite createSingleTestCaseSuite(TestCase testCase) {
        XmlSuite suite = new XmlSuite();
        suite.setName("SingleTestCase_" + testCase.getTestCaseId());
        XmlTest test = new XmlTest(suite);
        test.setName("TestCase_" + testCase.getTestCaseId());
        String testClassName = mapTestCaseToTestClass(testCase);
        String testMethodName = mapTestCaseToMethod(testCase);
        if (testClassName != null && testMethodName != null) {
            XmlClass xmlClass = new XmlClass(testClassName);
            List<XmlInclude> includeMethods = Arrays.asList(new XmlInclude(testMethodName));
            xmlClass.setIncludedMethods(includeMethods);
            test.setXmlClasses(Arrays.asList(xmlClass));
            System.out.println("[TestExecution] Configured single test case:");
            System.out.println("  - Class: " + testClassName);
            System.out.println("  - Method: " + testMethodName);
            System.out.println("  - Test Case: " + testCase.getTestCaseId() + " (" + testCase.getObjective() + ")");
        } else {
            System.out.println("[TestExecution] WARNING: Could not map test case to class/method:");
            System.out.println("  - Test Case ID: " + testCase.getTestCaseId());
            System.out.println("  - User Story: " + testCase.getUserStoryId());
            System.out.println("  - Mapped Class: " + testClassName);
            System.out.println("  - Mapped Method: " + testMethodName);
        }
        return suite;
    }
    private String normalizeUserStoryIdForMapping(String userStoryId) {
        if (userStoryId == null)
            return null;
        if (userStoryId.contains("_"))
            return userStoryId.replace("_", "");
        return userStoryId;
    }
    private String normalizeUserStoryIdForLookup(String userStoryId) {
        if (userStoryId == null || userStoryId.trim().isEmpty())
            return userStoryId;
        String id = userStoryId.trim();
        if (id.contains("_"))
            return id;
        if (id.matches("US\\d+"))
            return id.substring(0, 2) + "_" + id.substring(2);
        return id;
    }
    private String normalizeTestCaseId(String testCaseId) {
        if (testCaseId == null || testCaseId.trim().isEmpty())
            return testCaseId;
        return testCaseId.trim().replace("_", "").toUpperCase();
    }
    private String mapTestCaseToTestClass(TestCase testCase) {
        String userStoryId = normalizeUserStoryIdForMapping(testCase.getUserStoryId());
        String testCaseId = testCase.getTestCaseId();
        if ("US01".equals(userStoryId)) {
            return "com.vizja.testweb.tests.us01.Us01_KullaniciKaydiYapilabilmeli";
        }
        if ("US02".equals(userStoryId)) {
            return "com.vizja.testweb.tests.us02.Us02_GecersizYeniKullaniciKayit";
        }
        if ("US03".equals(userStoryId)) {
            return "com.vizja.testweb.tests.us03.Us03_BillingAdressEkle";
        }
        if ("US04".equals(userStoryId)) {
            switch (testCaseId) {
                case "TC01":
                    return "com.vizja.testweb.tests.us04.Tc01";
                case "TC02":
                    return "com.vizja.testweb.tests.us04.Tc02";
                case "TC03":
                    return "com.vizja.testweb.tests.us04.Tc03";
                case "TC04":
                    return "com.vizja.testweb.tests.us04.Tc04";
                case "TC05":
                    return "com.vizja.testweb.tests.us04.Tc05";
                case "TC06":
                    return "com.vizja.testweb.tests.us04.Tc06";
                case "TC07":
                    return "com.vizja.testweb.tests.us04.Tc07";
                case "TC08":
                    return "com.vizja.testweb.tests.us04.Tc08";
                case "TC09":
                    return "com.vizja.testweb.tests.us04.Tc09";
                case "TC10":
                    return "com.vizja.testweb.tests.us04.Tc10";
                default:
                    return null;
            }
        }
        if ("US08".equals(userStoryId)) {
            switch (testCaseId) {
                case "TC01":
                case "TC02":
                case "TC03":
                case "TC04":
                    return "com.vizja.testweb.tests.us08.TC_01_TC02_TC03_TC04";
                case "TC05":
                case "TC06":
                case "TC07":
                case "TC08":
                case "TC09":
                case "TC10":
                    return "com.vizja.testweb.tests.us08.TC05_TC06_TC07_TC08_TC09_TC10";
                default:
                    return null;
            }
        }
        return null;
    }
    private String mapTestCaseToMethod(TestCase testCase) {
        String userStoryId = normalizeUserStoryIdForMapping(testCase.getUserStoryId());
        String testCaseId = testCase.getTestCaseId();
        if ("US01".equals(userStoryId)) {
            switch (testCaseId) {
                case "TC01":
                    return "tc01_KullaniciKayit";
                case "TC02":
                    return "tc02_withoutUsernameNotRegister";
                case "TC03":
                    return "tc03_withoutEmailNotRegister";
                case "TC04":
                    return "tc04_withoutPasswordNotRegister";
                case "TC05":
                    return "tc05_WithoutIagreeClickNotRegister";
                case "TC06":
                    return "tc06_hataliemailileKayitOlma";
                case "TC07":
                    return "tc07_withoutComEmailIleKayitOlma";
                case "TC08":
                    return "tc08_sekizChrctrPasswordkayit";
                case "TC09":
                    return "tc09_dokuzChrctrPasswordkayit";
                case "TC10":
                    return "tc10_yediChrctrPasswordkayitOlma";
                case "TC11":
                    return "tc11_passwordSadeceRakamlaKayitOlma";
                case "TC12":
                    return "tc12_buyukkucukHarfRakamUsernameKayit";
                case "TC13":
                    return "tc13_usernameOzelkarakterKayit";
                default:
                    return null;
            }
        }
        if ("US02".equals(userStoryId)) {
            switch (testCaseId) {
                case "TC01":
                    return "tc01_kayitliBilgiKaydiYapilmamali";
                case "TC02":
                    return "tc02_kayitliUsernameSignUpOlma";
                case "TC03":
                    return "tc03_kayitliEmailSignUpOlma";
                case "TC04":
                    return "tc04_withoutPasswordNotSignup";
                case "TC05":
                    return "tc05_loginWithRegisteredPassword";
                case "TC06":
                    return "tc06_loginWithUnregisteredPassword";
                case "TC07":
                    return "tc07_signInWithoutPasswordNotClickable";
                case "TC08":
                    return "tc08_signInWithoutEmailNotAccepted";
                case "TC09":
                    return "tc09_signUpWithRegisteredEmailShowsError";
                default:
                    return null;
            }
        }
        if ("US03".equals(userStoryId)) {
            switch (testCaseId) {
                case "TC01":
                    return "tc01_billingAddressSavedWhenAllFieldsFilled";
                case "TC02":
                    return "tc02_saveAddressCannotBeDoneWithoutCountryRegion";
                case "TC03":
                    return "tc03_saveAddressCannotBeDoneWithoutStreet";
                case "TC04":
                    return "tc04_saveAddressCannotBeDoneWithoutTownCity";
                case "TC05":
                    return "tc05_saveAddressCannotBeDoneWithoutState";
                case "TC06":
                    return "tc06_saveAddressCannotBeDoneWithoutZIP";
                case "TC07":
                    return "tc07_saveAddressCannotBeDoneWithoutPhone";
                case "TC08":
                    return "tc08_saveAddressCannotBeDoneWhenAllFieldsEmpty";
                case "TC09":
                    return "tc09_firstNameLastNameEmailAutoFilledWhenEditing";
                case "TC10":
                    return "tc10_addressChangedSuccessfullyMessageDisplayed";
                case "TC11":
                    return "tc11_saveAddressWithInvalidZIPRejected";
                case "TC12":
                    return "tc12_saveAddressWithInvalidPhoneRejected";
                case "TC13":
                    return "tc13_billingAddressAddedBySaveWhenFormValid";
                default:
                    return null;
            }
        }
        if ("US04".equals(userStoryId)) {
            return "test01";
        }
        if ("US08".equals(userStoryId)) {
            switch (testCaseId) {
                case "TC01":
                    return "testAddProductsToWishlist";
                case "TC02":
                    return "testQuickViewFunctionality";
                case "TC03":
                    return "testCheckoutProcess";
                case "TC04":
                    return "testCheckoutProcess"; 
                case "TC05":
                    return "testFirstNameRequired";
                case "TC06":
                    return "testLastNameRequired";
                case "TC07":
                    return "testStreetAddressRequired";
                case "TC08":
                    return "testPostCodeRequired";
                case "TC09":
                    return "testTownCityRequired";
                case "TC10":
                    return "testProvinceRequired";
                default:
                    return null;
            }
        }
        return null;
    }
}
