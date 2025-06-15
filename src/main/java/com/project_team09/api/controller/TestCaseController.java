package com.project_team09.api.controller;

import com.project_team09.api.model.dto.TestCaseDTO;
import com.project_team09.api.model.dto.TestStepDTO;
import com.project_team09.api.model.entity.TestCase;
import com.project_team09.api.service.TestCaseService;
import com.project_team09.api.service.ExcelParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test-cases")
public class TestCaseController {

    private final TestCaseService testCaseService;
    private final ExcelParserService excelParserService;

    public TestCaseController(TestCaseService testCaseService, ExcelParserService excelParserService) {
        this.testCaseService = testCaseService;
        this.excelParserService = excelParserService;
    }

    @GetMapping
    public ResponseEntity<List<TestCaseDTO>> getAllTestCases() {
        return ResponseEntity.ok(testCaseService.getAllTestCases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestCaseDTO> getTestCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(testCaseService.getTestCaseById(id));
    }

    @GetMapping("/suite/{suiteId}")
    public ResponseEntity<List<TestCaseDTO>> getTestCasesBySuiteId(@PathVariable Long suiteId) {
        return ResponseEntity.ok(testCaseService.getTestCasesBySuiteId(suiteId));
    }

    @PostMapping
    public ResponseEntity<TestCaseDTO> createTestCase(@Valid @RequestBody TestCaseDTO testCaseDTO) {
        TestCaseDTO createdTestCase = testCaseService.createTestCase(testCaseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTestCase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestCaseDTO> updateTestCase(@PathVariable Long id,
            @Valid @RequestBody TestCaseDTO testCaseDTO) {
        return ResponseEntity.ok(testCaseService.updateTestCase(id, testCaseDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-excel")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Lütfen geçerli bir Excel dosyası yükleyin.");
            }

            String contentType = file.getContentType();
            if (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                    !contentType.equals("application/vnd.ms-excel")) {
                return ResponseEntity.badRequest().body("Lütfen bir Excel dosyası yükleyin (.xlsx veya .xls)");
            }

            List<TestCase> savedTestCases = excelParserService.parseAndSaveExcelFile(file); // TestCase entity'lerini
                                                                                            // DTO'ya dönüştür
            List<TestCaseDTO> testCaseDTOs = savedTestCases.stream()
                    .map(tc -> {
                        TestCaseDTO dto = new TestCaseDTO();
                        // Temel bilgiler
                        dto.setId(tc.getId());
                        dto.setName(tc.getName());
                        dto.setDescription(tc.getDescription());
                        dto.setClassName(tc.getClassName());
                        dto.setMethodName(tc.getMethodName());
                        dto.setCreatedAt(tc.getCreatedAt());
                        dto.setUpdatedAt(tc.getUpdatedAt());

                        // Excel'den gelen detaylı bilgiler
                        dto.setUserStoryId(tc.getUserStoryId());
                        dto.setTestCaseId(tc.getTestCaseId());
                        dto.setTestObjective(tc.getTestObjective());
                        dto.setPreCondition(tc.getPreCondition());
                        dto.setNote(tc.getNote());

                        // Test adımlarını dönüştür
                        if (tc.getSteps() != null) {
                            dto.setSteps(tc.getSteps().stream()
                                    .map(step -> {
                                        TestStepDTO stepDto = new TestStepDTO();
                                        stepDto.setId(step.getId());
                                        stepDto.setStepNumber(step.getStepNumber());
                                        stepDto.setStepDescription(step.getStepDescription());
                                        stepDto.setTestData(step.getTestData());
                                        stepDto.setExpectedResult(step.getExpectedResult());
                                        stepDto.setActualResult(step.getActualResult());
                                        stepDto.setIsHighlighted(step.getIsHighlighted());
                                        stepDto.setIsHome(step.getIsHome());
                                        stepDto.setStatus(step.getStatus());
                                        return stepDto;
                                    })
                                    .collect(Collectors.toList()));
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(testCaseDTOs);
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body("Excel dosyası işlenirken hata oluştu: " + e.getMessage());
        }
    }
}
