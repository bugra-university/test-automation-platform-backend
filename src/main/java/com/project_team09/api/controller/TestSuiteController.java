package com.project_team09.api.controller;

import com.project_team09.api.model.dto.TestSuiteDTO;
import com.project_team09.api.service.TestSuiteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/test-suites")
public class TestSuiteController {

    private final TestSuiteService testSuiteService;

    public TestSuiteController(TestSuiteService testSuiteService) {
        this.testSuiteService = testSuiteService;
    }

    @GetMapping
    public ResponseEntity<List<TestSuiteDTO>> getAllTestSuites() {
        return ResponseEntity.ok(testSuiteService.getAllTestSuites());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestSuiteDTO> getTestSuiteById(@PathVariable Long id) {
        return ResponseEntity.ok(testSuiteService.getTestSuiteById(id));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TestSuiteDTO>> getTestSuitesByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(testSuiteService.getTestSuitesByProjectId(projectId));
    }

    @PostMapping
    public ResponseEntity<TestSuiteDTO> createTestSuite(@Valid @RequestBody TestSuiteDTO testSuiteDTO) {
        TestSuiteDTO createdTestSuite = testSuiteService.createTestSuite(testSuiteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTestSuite);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestSuiteDTO> updateTestSuite(@PathVariable Long id,
            @Valid @RequestBody TestSuiteDTO testSuiteDTO) {
        return ResponseEntity.ok(testSuiteService.updateTestSuite(id, testSuiteDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestSuite(@PathVariable Long id) {
        testSuiteService.deleteTestSuite(id);
        return ResponseEntity.noContent().build();
    }
}
