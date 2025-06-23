package com.project_team09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project_team09.service.TestSuitesService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/projects/{projectId}/test-suites")
@CrossOrigin(origins = "http://localhost:3000")
public class TestSuitesController {

    @Autowired
    private TestSuitesService testSuitesService;

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
     * Run a test suite (User Story)
     */
    @PostMapping("/{userStoryId}/run")
    public ResponseEntity<Map<String, Object>> runTestSuite(
            @PathVariable Long projectId, 
            @PathVariable String userStoryId) {
        try {
            Map<String, Object> result = testSuitesService.runTestSuite(projectId, userStoryId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test suite execution started");
            response.put("runResult", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to run test suite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Run a specific test case
     */
    @PostMapping("/test-cases/{testCaseId}/run")
    public ResponseEntity<Map<String, Object>> runTestCase(
            @PathVariable Long projectId, 
            @PathVariable Long testCaseId) {
        try {
            Map<String, Object> result = testSuitesService.runTestCase(projectId, testCaseId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test case execution started");
            response.put("runResult", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to run test case: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
} 