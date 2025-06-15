package com.project_team09.api.service;

import com.project_team09.api.model.dto.TestCaseDTO;
import com.project_team09.api.model.dto.TestStepDTO;
import com.project_team09.api.model.entity.TestCase;
import com.project_team09.api.model.entity.TestResult;
import com.project_team09.api.model.entity.TestStep;
import com.project_team09.api.model.entity.TestSuite;
import com.project_team09.api.repository.TestCaseRepository;
import com.project_team09.api.repository.TestSuiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final TestSuiteRepository testSuiteRepository;

    public TestCaseService(TestCaseRepository testCaseRepository, TestSuiteRepository testSuiteRepository) {
        this.testCaseRepository = testCaseRepository;
        this.testSuiteRepository = testSuiteRepository;
    }

    @Transactional(readOnly = true)
    public List<TestCaseDTO> getAllTestCases() {
        return testCaseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestCaseDTO getTestCaseById(Long id) {
        return testCaseRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("TestCase not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TestCaseDTO> getTestCasesBySuiteId(Long suiteId) {
        return testCaseRepository.findBySuiteId(suiteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TestCaseDTO createTestCase(TestCaseDTO testCaseDTO) {
        TestCase testCase = convertToEntity(testCaseDTO);
        TestCase savedTestCase = testCaseRepository.save(testCase);
        return convertToDTO(savedTestCase);
    }

    @Transactional
    public TestCaseDTO updateTestCase(Long id, TestCaseDTO testCaseDTO) {
        if (!testCaseRepository.existsById(id)) {
            throw new EntityNotFoundException("TestCase not found with id: " + id);
        }

        TestCase testCase = convertToEntity(testCaseDTO);
        testCase.setId(id);
        TestCase updatedTestCase = testCaseRepository.save(testCase);
        return convertToDTO(updatedTestCase);
    }

    @Transactional
    public void deleteTestCase(Long id) {
        if (!testCaseRepository.existsById(id)) {
            throw new EntityNotFoundException("TestCase not found with id: " + id);
        }
        testCaseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TestCaseDTO findByClassNameAndMethodName(String className, String methodName) {
        return testCaseRepository.findByClassNameAndMethodName(className, methodName)
                .map(this::convertToDTO)
                .orElse(null);
    }

    private TestCaseDTO convertToDTO(TestCase testCase) {
        TestCaseDTO dto = new TestCaseDTO();
        dto.setId(testCase.getId());
        dto.setSuiteId(testCase.getSuite().getId());
        dto.setName(testCase.getName());
        dto.setDescription(testCase.getDescription());
        dto.setClassName(testCase.getClassName());
        dto.setMethodName(testCase.getMethodName());
        dto.setCreatedAt(testCase.getCreatedAt());
        dto.setUpdatedAt(testCase.getUpdatedAt());

        // Set Excel-specific fields
        dto.setUserStoryId(testCase.getUserStoryId());
        dto.setTestCaseId(testCase.getTestCaseId());
        dto.setTestObjective(testCase.getTestObjective());
        dto.setPreCondition(testCase.getPreCondition());
        dto.setNote(testCase.getNote());

        // Calculate test statistics from results
        if (testCase.getTestResults() != null && !testCase.getTestResults().isEmpty()) {
            dto.setTotalRuns(testCase.getTestResults().size());
            int successCount = 0;
            for (TestResult result : testCase.getTestResults()) {
                if ("PASS".equals(result.getStatus())) {
                    successCount++;
                }
            }
            dto.setSuccessCount(successCount);
            dto.setFailureCount(testCase.getTestResults().size() - successCount);
        } else {
            dto.setTotalRuns(0);
            dto.setSuccessCount(0);
            dto.setFailureCount(0);
        }

        // Convert test steps without circular reference
        if (testCase.getSteps() != null) {
            List<TestStepDTO> stepDTOs = new ArrayList<>();
            for (TestStep step : testCase.getSteps()) {
                TestStepDTO stepDTO = new TestStepDTO();
                stepDTO.setId(step.getId());
                stepDTO.setStepNumber(step.getStepNumber());
                stepDTO.setStepDescription(step.getStepDescription());
                stepDTO.setTestData(step.getTestData());
                stepDTO.setExpectedResult(step.getExpectedResult());
                stepDTO.setActualResult(step.getActualResult());
                stepDTO.setLastRun(step.getTimestamp());
                stepDTO.setIsHighlighted(step.getIsHighlighted());
                stepDTO.setIsHome(step.getIsHome());
                stepDTO.setStatus(step.getStatus());
                stepDTOs.add(stepDTO);
            }
            dto.setSteps(stepDTOs);
        }

        return dto;
    }

    private TestCase convertToEntity(TestCaseDTO testCaseDTO) {
        TestCase testCase = new TestCase();
        testCase.setId(testCaseDTO.getId());
        testCase.setName(testCaseDTO.getName());
        testCase.setDescription(testCaseDTO.getDescription());
        testCase.setClassName(testCaseDTO.getClassName());
        testCase.setMethodName(testCaseDTO.getMethodName());

        // Set Excel-specific fields
        testCase.setUserStoryId(testCaseDTO.getUserStoryId());
        testCase.setTestCaseId(testCaseDTO.getTestCaseId());
        testCase.setTestObjective(testCaseDTO.getTestObjective());
        testCase.setPreCondition(testCaseDTO.getPreCondition());
        testCase.setNote(testCaseDTO.getNote());

        if (testCaseDTO.getCreatedAt() != null) {
            testCase.setCreatedAt(testCaseDTO.getCreatedAt());
        }

        testCase.setUpdatedAt(LocalDateTime.now());

        if (testCaseDTO.getSuiteId() != null) {
            TestSuite testSuite = testSuiteRepository.findById(testCaseDTO.getSuiteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "TestSuite not found with id: " + testCaseDTO.getSuiteId()));
            testCase.setSuite(testSuite);
        }

        // Convert test steps
        if (testCaseDTO.getSteps() != null) {
            List<TestStep> steps = new ArrayList<>();
            for (TestStepDTO stepDTO : testCaseDTO.getSteps()) {
                TestStep step = new TestStep();
                step.setId(stepDTO.getId());
                step.setStepNumber(stepDTO.getStepNumber());
                step.setStepDescription(stepDTO.getStepDescription());
                step.setTestData(stepDTO.getTestData());
                step.setExpectedResult(stepDTO.getExpectedResult());
                step.setActualResult(stepDTO.getActualResult());
                step.setIsHighlighted(stepDTO.getIsHighlighted());
                step.setIsHome(stepDTO.getIsHome());
                step.setStatus(stepDTO.getStatus());
                step.setTimestamp(stepDTO.getLastRun());
                step.setTestCase(testCase);
                steps.add(step);
            }
            testCase.setSteps(steps);
        }

        return testCase;
    }
}
