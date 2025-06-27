package com.project_team09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project_team09.model.ProductBacklogItem;
import com.project_team09.model.TestCase;
import com.project_team09.model.TestStep;
import com.project_team09.model.TestResult;
import com.project_team09.repository.ProductBacklogItemRepository;
import com.project_team09.repository.TestCaseRepository;
import com.project_team09.repository.TestStepRepository;
import com.project_team09.repository.TestResultRepository;

import java.time.LocalDateTime;
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

    /**
     * Get all test suites (User Stories with Test Cases) for a project
     */
    public List<Map<String, Object>> getTestSuitesByProject(Long projectId) {
        // Get all User Stories (Product Backlog Items) for the project
        List<ProductBacklogItem> backlogItems = productBacklogItemRepository.findByProjectId(projectId);
        
        // Get all Test Cases for the project
        List<TestCase> testCases = testCaseRepository.findByProjectId(projectId);
        
        // Group test cases by user story ID (handle format differences US_01 vs US01)
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
            
            // Get test cases for this user story
            List<TestCase> userStoryTestCases = testCasesByUserStory.getOrDefault(
                backlogItem.getUserStoryId(), new ArrayList<>());
            
            // Calculate status and progress based on test cases
            Map<String, Object> statusAndProgress = calculateStatusAndProgress(userStoryTestCases);
            testSuite.putAll(statusAndProgress);
            
            // Convert test cases to map format and sort by test case ID
            List<Map<String, Object>> testCaseMaps = userStoryTestCases.stream()
                .sorted((tc1, tc2) -> tc1.getTestCaseId().compareTo(tc2.getTestCaseId()))
                .map(this::convertTestCaseToMap)
                .collect(Collectors.toList());
            
            testSuite.put("testCases", testCaseMaps);
            testSuites.add(testSuite);
        }

        return testSuites;
    }

    /**
     * Get test cases for a specific user story
     */
    public List<Map<String, Object>> getTestCasesByUserStory(Long projectId, String userStoryId) {
        List<TestCase> testCases = testCaseRepository.findByProjectId(projectId).stream()
            .filter(tc -> userStoryId.equals(normalizeUserStoryId(tc.getUserStoryId())))
            .collect(Collectors.toList());

        return testCases.stream()
            .map(this::convertTestCaseToMap)
            .collect(Collectors.toList());
    }

    /**
     * Get test steps for a specific test case
     */
    public List<Map<String, Object>> getTestStepsByTestCase(Long testCaseId) {
        List<TestStep> testSteps = testStepRepository.findByTestCaseIdOrderByStepNumber(testCaseId);
        
        return testSteps.stream()
            .map(this::convertTestStepToMap)
            .collect(Collectors.toList());
    }

    /**
     * Run a test suite (User Story) with configuration - placeholder implementation
     */
    public Map<String, Object> runTestSuite(Long projectId, String userStoryId, boolean isHeadless, String browser) {
        Map<String, Object> result = new HashMap<>();
        result.put("userStoryId", userStoryId);
        result.put("status", "started");
        result.put("startTime", LocalDateTime.now());
        result.put("message", "Test suite execution started for " + userStoryId);
        result.put("configuration", Map.of(
            "isHeadless", isHeadless,
            "browser", browser
        ));
        
        // TODO: Implement actual test execution logic
        // This will integrate with TestNG and WebDriver based on configuration
        return result;
    }

    /**
     * Run a test suite (User Story) - backward compatibility
     */
    public Map<String, Object> runTestSuite(Long projectId, String userStoryId) {
        return runTestSuite(projectId, userStoryId, true, "chrome");
    }

    /**
     * Run a specific test case with configuration - placeholder implementation
     */
    public Map<String, Object> runTestCase(Long projectId, Long testCaseId, boolean isHeadless, String browser) {
        Map<String, Object> result = new HashMap<>();
        result.put("testCaseId", testCaseId);
        result.put("status", "started");
        result.put("startTime", LocalDateTime.now());
        result.put("message", "Test case execution started for ID: " + testCaseId);
        result.put("configuration", Map.of(
            "isHeadless", isHeadless,
            "browser", browser
        ));
        
        // TODO: Implement actual test execution logic
        // This will integrate with TestNG and WebDriver based on configuration
        return result;
    }

    /**
     * Run a specific test case - backward compatibility
     */
    public Map<String, Object> runTestCase(Long projectId, Long testCaseId) {
        return runTestCase(projectId, testCaseId, true, "chrome");
    }

    /**
     * Get test suites statistics for a project
     */
    public Map<String, Object> getTestSuitesStatistics(Long projectId) {
        List<TestCase> allTestCases = testCaseRepository.findByProjectId(projectId);
        List<ProductBacklogItem> backlogItems = productBacklogItemRepository.findByProjectId(projectId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalStories", backlogItems.size());
        statistics.put("totalTestCases", allTestCases.size());
        
        // Create statusCounts object as expected by frontend
        Map<String, Object> statusCounts = new HashMap<>();
        statusCounts.put("passed", 0);
        statusCounts.put("failed", 0);
        statusCounts.put("pending", allTestCases.size()); // All tests are pending initially
        statusCounts.put("not_run", allTestCases.size());
        
        statistics.put("statusCounts", statusCounts);
        
        return statistics;
    }

    // Helper methods

    /**
     * Normalize user story ID format (US01 -> US_01, US_01 -> US_01)
     */
    private String normalizeUserStoryId(String userStoryId) {
        if (userStoryId == null) return null;
        
        // If already has underscore, return as is
        if (userStoryId.contains("_")) {
            return userStoryId;
        }
        
        // If format is US01, convert to US_01
        if (userStoryId.matches("US\\d+")) {
            return userStoryId.substring(0, 2) + "_" + userStoryId.substring(2);
        }
        
        return userStoryId;
    }

    private String extractUserStoryName(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "Unnamed User Story";
        }
        
        // Extract first line or first sentence as name
        String[] lines = description.split("\n");
        String firstLine = lines[0].trim();
        
        // Remove common prefixes and clean up
        firstLine = firstLine.replaceAll("^User registration to the Site \\(Customer\\)", "User Registration");
        firstLine = firstLine.replaceAll("\\(.*?\\)$", "").trim(); // Remove ending parentheses
        
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

        // For now, use simple logic - will be enhanced with actual test results
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
        
        // Get test steps count
        int stepCount = testStepRepository.countByTestCaseId(testCase.getId());
        boolean hasSteps = stepCount > 0;
        
        testCaseMap.put("hasSteps", hasSteps);
        testCaseMap.put("stepCount", stepCount);
        testCaseMap.put("isComplete", hasSteps);
        
        // Get latest test result for this test case
        List<TestResult> testResults = testResultRepository.findByTestCaseIdOrderByCreatedAtDesc(testCase.getId());
        TestResult latestResult = testResults.isEmpty() ? null : testResults.get(0);
        
        if (latestResult != null) {
            // Set status based on latest test result
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
            
            // Set last run time
            testCaseMap.put("lastRun", latestResult.getStartTime());
            
            // Set duration
            if (latestResult.getDurationMs() != null) {
                testCaseMap.put("duration", latestResult.getDurationMs());
            } else {
                testCaseMap.put("duration", null);
            }
        } else {
            // No test results yet
            testCaseMap.put("status", "not_run");
            testCaseMap.put("lastRun", null);
            testCaseMap.put("duration", null);
        }
        
        // Get steps if they exist
        if (hasSteps) {
            List<TestStep> steps = testStepRepository.findByTestCaseIdOrderByStepNumber(testCase.getId());
            
            // Count completed steps (PASS status) - check for different possible status values
            int completedSteps = (int) steps.stream()
                .filter(step -> {
                    String status = step.getStatus();
                    return status != null && (
                        "PASS".equalsIgnoreCase(status) || 
                        "PASSED".equalsIgnoreCase(status) ||
                        "passed".equals(status)
                    );
                })
                .count();
            
            // Step progress calculation completed
            
            testCaseMap.put("progress", Map.of("completed", completedSteps, "total", stepCount));
            
            List<Map<String, Object>> stepMaps = steps.stream()
                .map(step -> convertTestStepToMap(step, steps.size()))
                .collect(Collectors.toList());
            testCaseMap.put("steps", stepMaps);
        } else {
            testCaseMap.put("progress", Map.of("completed", 0, "total", stepCount));
            testCaseMap.put("steps", new ArrayList<>());
        }
        
        return testCaseMap;
    }

    private Map<String, Object> convertTestStepToMap(TestStep testStep) {
        Map<String, Object> stepMap = new HashMap<>();
        stepMap.put("id", testStep.getStepNumber());
        stepMap.put("description", testStep.getDescription());
        stepMap.put("status", testStep.getStatus() != null ? testStep.getStatus().toLowerCase() : "pending");
        stepMap.put("testData", testStep.getTestData());
        stepMap.put("expectedResult", testStep.getExpectedResult());
        stepMap.put("actualResult", testStep.getActualResult());
        stepMap.put("lastRun", null);
        stepMap.put("duration", null);
        return stepMap;
    }

    private Map<String, Object> convertTestStepToMap(TestStep testStep, int totalSteps) {
        Map<String, Object> stepMap = convertTestStepToMap(testStep);
        
        // Add step progress (current step / total steps)
        stepMap.put("stepNumber", testStep.getStepNumber());
        stepMap.put("progress", testStep.getStepNumber() + "/" + totalSteps);
        
        return stepMap;
    }
} 