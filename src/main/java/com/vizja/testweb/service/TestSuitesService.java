package com.vizja.testweb.service;

import com.vizja.testweb.model.*;
import com.vizja.testweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestSuitesService {
    @Autowired
    private ProductBacklogItemRepository productBacklogItemRepository;
    @Autowired
    private TestCaseRepository testCaseRepository;
    @Autowired
    private TestStepRepository testStepRepository;
    @Autowired
    private TestResultRepository testResultRepository;

    public List<Map<String, Object>> getTestSuitesByProject(Long projectId) {
        List<ProductBacklogItem> backlogItems = productBacklogItemRepository.findByProjectId(projectId);
        List<TestCase> testCases = testCaseRepository.findByProjectId(projectId);
        Map<String, List<TestCase>> testCasesByUserStory = testCases.stream()
                .collect(Collectors.groupingBy(tc -> normalizeUserStoryId(tc.getUserStoryId()),
                        Collectors.toList()));
        List<Map<String, Object>> testSuites = new ArrayList<>();
        for (ProductBacklogItem backlogItem : backlogItems) {
            Map<String, Object> testSuite = new HashMap<>();
            testSuite.put("id", backlogItem.getUserStoryId());
            testSuite.put("name", extractUserStoryName(backlogItem.getDescription()));
            testSuite.put("description", backlogItem.getDescription());
            testSuite.put("type", "user_story");
            testSuite.put("expanded", false);
            String normalizedBacklogId = normalizeUserStoryId(backlogItem.getUserStoryId());
            List<TestCase> userStoryTestCases = testCasesByUserStory.getOrDefault(
                    normalizedBacklogId, new ArrayList<>());
            Map<String, Object> statusAndProgress = calculateStatusAndProgress(userStoryTestCases);
            testSuite.putAll(statusAndProgress);
            List<Map<String, Object>> testCaseMaps = userStoryTestCases.stream()
                    .sorted((tc1, tc2) -> tc1.getTestCaseId().compareTo(tc2.getTestCaseId()))
                    .map(this::convertTestCaseToMap)
                    .collect(Collectors.toList());
            testSuite.put("testCases", testCaseMaps);
            testSuites.add(testSuite);
        }
        return testSuites;
    }

    public List<Map<String, Object>> getTestCasesByUserStory(Long projectId, String userStoryId) {
        String normalizedRequestId = normalizeUserStoryId(userStoryId);
        List<TestCase> testCases = testCaseRepository.findByProjectId(projectId).stream()
                .filter(tc -> normalizedRequestId.equals(normalizeUserStoryId(tc.getUserStoryId())))
                .collect(Collectors.toList());
        return testCases.stream()
                .map(this::convertTestCaseToMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTestStepsByTestCase(Long testCaseId) {
        List<TestStep> testSteps = testStepRepository.findByTestCaseIdOrderByStepNumber(testCaseId);
        return testSteps.stream()
                .map(this::convertTestStepToMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> runTestSuite(Long projectId, String userStoryId, boolean isHeadless, String browser) {
        Map<String, Object> result = new HashMap<>();
        result.put("userStoryId", userStoryId);
        result.put("status", "started");
        result.put("startTime", LocalDateTime.now());
        result.put("message", "Test suite execution started for " + userStoryId);
        result.put("configuration", Map.of(
                "isHeadless", isHeadless,
                "browser", browser));
        return result;
    }

    public Map<String, Object> runTestSuite(Long projectId, String userStoryId) {
        return runTestSuite(projectId, userStoryId, true, "chrome");
    }

    public Map<String, Object> runTestCase(Long projectId, Long testCaseId, boolean isHeadless, String browser) {
        Map<String, Object> result = new HashMap<>();
        result.put("testCaseId", testCaseId);
        result.put("status", "started");
        result.put("startTime", LocalDateTime.now());
        result.put("message", "Test case execution started for ID: " + testCaseId);
        result.put("configuration", Map.of(
                "isHeadless", isHeadless,
                "browser", browser));
        return result;
    }

    public Map<String, Object> runTestCase(Long projectId, Long testCaseId) {
        return runTestCase(projectId, testCaseId, true, "chrome");
    }

    public Map<String, Object> getTestSuitesStatistics(Long projectId) {
        List<TestCase> allTestCases = testCaseRepository.findByProjectId(projectId);
        List<ProductBacklogItem> backlogItems = productBacklogItemRepository.findByProjectId(projectId);
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalStories", backlogItems.size());
        statistics.put("totalTestCases", allTestCases.size());
        Map<String, Object> statusCounts = new HashMap<>();
        statusCounts.put("passed", 0);
        statusCounts.put("failed", 0);
        statusCounts.put("pending", allTestCases.size());
        statusCounts.put("not_run", allTestCases.size());
        statistics.put("statusCounts", statusCounts);
        return statistics;
    }

    private String normalizeUserStoryId(String userStoryId) {
        if (userStoryId == null)
            return null;
        if (userStoryId.contains("_")) {
            return userStoryId;
        }
        if (userStoryId.matches("US\\d+")) {
            return userStoryId.substring(0, 2) + "_" + userStoryId.substring(2);
        }
        return userStoryId;
    }

    private String extractUserStoryName(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "Unnamed User Story";
        }
        String[] lines = description.split("\n");
        String firstLine = lines[0].trim();
        firstLine = firstLine.replaceAll("^User registration to the Site \\(Customer\\)", "User Registration");
        firstLine = firstLine.replaceAll("\\(.*?\\)$", "").trim();
        return firstLine.length() > 50 ? firstLine.substring(0, 47) + "..." : firstLine;
    }

    private Map<String, Object> calculateStatusAndProgress(List<TestCase> testCases) {
        Map<String, Object> result = new HashMap<>();
        if (testCases.isEmpty()) {
            result.put("status", "pending");
            result.put("progress", Map.of("completed", 0, "total", 0));
            result.put("lastRun", null);
            result.put("duration", null);
            return result;
        }
        int totalTestCases = testCases.size();
        int completedTestCases = (int) testCases.stream()
                .filter(tc -> tc.getObjective() != null && !tc.getObjective().trim().isEmpty())
                .count();
        result.put("status", "not_run");
        result.put("progress", Map.of("completed", completedTestCases, "total", totalTestCases));
        result.put("lastRun", null);
        result.put("duration", null);
        return result;
    }

    private Map<String, Object> convertTestCaseToMap(TestCase testCase) {
        Map<String, Object> testCaseMap = new HashMap<>();
        testCaseMap.put("id", testCase.getTestCaseId());
        testCaseMap.put("name", testCase.getObjective() != null ? testCase.getObjective() : "Unnamed Test Case");
        testCaseMap.put("description", testCase.getObjective());
        testCaseMap.put("type", "test_case");
        testCaseMap.put("expanded", false);
        int stepCount = testStepRepository.countByTestCaseId(testCase.getId());
        boolean hasSteps = stepCount > 0;
        testCaseMap.put("hasSteps", hasSteps);
        testCaseMap.put("stepCount", stepCount);
        testCaseMap.put("isComplete", hasSteps);
        List<TestResult> testResults = testResultRepository.findByTestCaseIdOrderByCreatedAtDesc(testCase.getId());
        TestResult latestResult = testResults.isEmpty() ? null : testResults.get(0);
        if (latestResult != null) {
            String resultStatus = latestResult.getStatus();
            if ("PASS".equals(resultStatus)) {
                testCaseMap.put("status", "passed");
            } else if ("FAIL".equals(resultStatus)) {
                testCaseMap.put("status", "failed");
            } else if ("RUNNING".equals(resultStatus)) {
                testCaseMap.put("status", "running");
            } else {
                testCaseMap.put("status", "not_run");
            }
            testCaseMap.put("lastRun", latestResult.getStartTime());
            if (latestResult.getDurationMs() != null) {
                testCaseMap.put("duration", latestResult.getDurationMs());
            } else {
                testCaseMap.put("duration", null);
            }
        } else {
            testCaseMap.put("status", "not_run");
            testCaseMap.put("lastRun", null);
            testCaseMap.put("duration", null);
        }
        if (hasSteps) {
            List<TestStep> steps = testStepRepository.findByTestCaseIdOrderByStepNumber(testCase.getId());
            int completedSteps = (int) steps.stream()
                    .filter(step -> {
                        String status = step.getStatus();
                        return status != null && ("PASS".equalsIgnoreCase(status) ||
                                "PASSED".equalsIgnoreCase(status) ||
                                "passed".equals(status));
                    })
                    .count();
            testCaseMap.put("progress", Map.of("completed", completedSteps, "total", stepCount));
            List<Map<String, Object>> stepMaps = steps.stream()
                    .map(this::convertTestStepToMap)
                    .collect(Collectors.toList());
            testCaseMap.put("steps", stepMaps);
        } else {
            testCaseMap.put("progress", Map.of("completed", 0, "total", stepCount));
            testCaseMap.put("steps", new ArrayList<>());
        }
        return testCaseMap;
    }

    private Map<String, Object> convertTestStepToMap(TestStep step) {
        Map<String, Object> stepMap = new HashMap<>();
        stepMap.put("id", step.getId());
        stepMap.put("stepNumber", step.getStepNumber());
        stepMap.put("description", step.getDescription());
        stepMap.put("testData", step.getTestData());
        stepMap.put("expectedResult", step.getExpectedResult());
        stepMap.put("actualResult", step.getActualResult());
        stepMap.put("isHome", step.getIsHome());
        stepMap.put("url", step.getUrl());
        stepMap.put("screenshotPath", step.getScreenshotPath());
        stepMap.put("status", step.getStatus());
        stepMap.put("rowIndex", step.getRowIndex());
        if (step.getLastRun() != null) {
            stepMap.put("lastRun", step.getLastRun().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            stepMap.put("lastRun", null);
        }
        if (step.getDurationMs() != null) {
            stepMap.put("durationMs", step.getDurationMs());
            stepMap.put("duration", formatDuration(step.getDurationMs()));
        } else {
            stepMap.put("durationMs", null);
            stepMap.put("duration", null);
        }
        return stepMap;
    }

    private String formatDuration(Long durationMs) {
        long durationSeconds = durationMs / 1000;
        long hours = durationSeconds / 3600;
        long minutes = (durationSeconds % 3600) / 60;
        long seconds = durationSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
