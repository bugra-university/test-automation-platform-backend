package com.project_team09.api.controller;

import com.project_team09.api.model.dto.TestRunDTO;
import com.project_team09.api.service.TestRunService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/test-runs")
public class TestRunController {

    private final TestRunService testRunService;

    public TestRunController(TestRunService testRunService) {
        this.testRunService = testRunService;
    }

    @GetMapping
    public ResponseEntity<List<TestRunDTO>> getAllTestRuns() {
        return ResponseEntity.ok(testRunService.getAllTestRuns());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TestRunDTO>> getActiveTestRuns() {
        return ResponseEntity.ok(testRunService.getActiveTestRuns());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestRunDTO> getTestRunById(@PathVariable Long id) {
        return ResponseEntity.ok(testRunService.getTestRunById(id));
    }

    @PostMapping
    public ResponseEntity<TestRunDTO> createTestRun(@Valid @RequestBody TestRunDTO testRunDTO) {
        TestRunDTO createdTestRun = testRunService.createTestRun(testRunDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTestRun);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TestRunDTO> updateTestRunStatus(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(testRunService.updateTestRunStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> stopTestRun(@PathVariable Long id) {
        testRunService.stopTestRun(id);
        return ResponseEntity.noContent().build();
    }
}
