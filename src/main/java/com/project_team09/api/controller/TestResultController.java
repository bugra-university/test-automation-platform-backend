package com.project_team09.api.controller;

import com.project_team09.api.model.dto.TestResultDTO;
import com.project_team09.api.service.TestResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/test-results")
public class TestResultController {

    private final TestResultService testResultService;

    public TestResultController(TestResultService testResultService) {
        this.testResultService = testResultService;
    }

    @GetMapping
    public ResponseEntity<List<TestResultDTO>> getAllTestResults() {
        return ResponseEntity.ok(testResultService.getAllTestResults());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResultDTO> getTestResultById(@PathVariable Long id) {
        return ResponseEntity.ok(testResultService.getTestResultById(id));
    }

    @GetMapping("/test-run/{testRunId}")
    public ResponseEntity<List<TestResultDTO>> getTestResultsByTestRunId(@PathVariable Long testRunId) {
        return ResponseEntity.ok(testResultService.getTestResultsByTestRunId(testRunId));
    }

    @GetMapping("/test-case/{testCaseId}")
    public ResponseEntity<List<TestResultDTO>> getTestResultsByTestCaseId(@PathVariable Long testCaseId) {
        return ResponseEntity.ok(testResultService.getTestResultsByTestCaseId(testCaseId));
    }

    @PostMapping
    public ResponseEntity<TestResultDTO> createTestResult(@Valid @RequestBody TestResultDTO testResultDTO) {
        TestResultDTO createdTestResult = testResultService.createTestResult(testResultDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTestResult);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestResultDTO> updateTestResult(@PathVariable Long id,
            @Valid @RequestBody TestResultDTO testResultDTO) {
        return ResponseEntity.ok(testResultService.updateTestResult(id, testResultDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestResult(@PathVariable Long id) {
        testResultService.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }
}
