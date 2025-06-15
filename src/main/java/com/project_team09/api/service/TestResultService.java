package com.project_team09.api.service;

import com.project_team09.api.model.dto.TestResultDTO;
import com.project_team09.api.model.entity.TestCase;
import com.project_team09.api.model.entity.TestResult;
import com.project_team09.api.model.entity.TestRun;
import com.project_team09.api.repository.TestCaseRepository;
import com.project_team09.api.repository.TestResultRepository;
import com.project_team09.api.repository.TestRunRepository;
import com.project_team09.api.repository.TestStepRepository;
import com.project_team09.api.repository.ScreenshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestResultService {

    private final TestResultRepository testResultRepository;
    private final TestRunRepository testRunRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestStepRepository testStepRepository;
    private final ScreenshotRepository screenshotRepository;

    public TestResultService(
            TestResultRepository testResultRepository,
            TestRunRepository testRunRepository,
            TestCaseRepository testCaseRepository,
            TestStepRepository testStepRepository,
            ScreenshotRepository screenshotRepository) {
        this.testResultRepository = testResultRepository;
        this.testRunRepository = testRunRepository;
        this.testCaseRepository = testCaseRepository;
        this.testStepRepository = testStepRepository;
        this.screenshotRepository = screenshotRepository;
    }

    @Transactional(readOnly = true)
    public List<TestResultDTO> getAllTestResults() {
        return testResultRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestResultDTO getTestResultById(Long id) {
        return testResultRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("TestResult not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TestResultDTO> getTestResultsByTestRunId(Long testRunId) {
        return testResultRepository.findByTestRunId(testRunId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestResultDTO> getTestResultsByTestCaseId(Long testCaseId) {
        return testResultRepository.findByTestCaseId(testCaseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestResultDTO> getTestResultsByStatus(String status) {
        return testResultRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TestResultDTO createTestResult(TestResultDTO testResultDTO) {
        TestResult testResult = convertToEntity(testResultDTO);
        TestResult savedTestResult = testResultRepository.save(testResult);
        return convertToDTO(savedTestResult);
    }

    @Transactional
    public TestResultDTO updateTestResult(Long id, TestResultDTO testResultDTO) {
        if (!testResultRepository.existsById(id)) {
            throw new EntityNotFoundException("TestResult not found with id: " + id);
        }

        TestResult testResult = convertToEntity(testResultDTO);
        testResult.setId(id);
        TestResult updatedTestResult = testResultRepository.save(testResult);
        return convertToDTO(updatedTestResult);
    }

    @Transactional
    public void deleteTestResult(Long id) {
        if (!testResultRepository.existsById(id)) {
            throw new EntityNotFoundException("TestResult not found with id: " + id);
        }
        testResultRepository.deleteById(id);
    }

    @Transactional
    public TestResultDTO recordTestExecution(Long testCaseId, Long testRunId, boolean success,
            LocalDateTime startTime, LocalDateTime endTime,
            String errorMessage, String stackTrace) {
        TestResultDTO dto = new TestResultDTO();
        dto.setTestCaseId(testCaseId);
        dto.setTestRunId(testRunId);
        dto.setStatus(success ? "PASS" : "FAIL");
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setDurationMs(
                endTime != null && startTime != null ? java.time.Duration.between(startTime, endTime).toMillis()
                        : null);
        dto.setErrorMessage(errorMessage);
        dto.setStackTrace(stackTrace);
        dto.setCreatedAt(LocalDateTime.now());

        return createTestResult(dto);
    }

    private TestResultDTO convertToDTO(TestResult testResult) {
        TestResultDTO dto = new TestResultDTO();
        dto.setId(testResult.getId());
        dto.setStatus(testResult.getStatus());
        dto.setStartTime(testResult.getStartTime());
        dto.setEndTime(testResult.getEndTime());
        dto.setDurationMs(testResult.getDurationMs());
        dto.setErrorMessage(testResult.getErrorMessage());
        dto.setStackTrace(testResult.getStackTrace());
        dto.setCreatedAt(testResult.getCreatedAt());

        if (testResult.getTestRun() != null) {
            dto.setTestRunId(testResult.getTestRun().getId());
        }

        if (testResult.getTestCase() != null) {
            TestCase testCase = testResult.getTestCase();
            dto.setTestCaseId(testCase.getId());
            dto.setTestCaseName(testCase.getName());
            dto.setClassName(testCase.getClassName());
            dto.setMethodName(testCase.getMethodName());
        }

        // Count related entities
        dto.setStepCount(testStepRepository.countByTestResultId(testResult.getId()));
        dto.setScreenshotCount(screenshotRepository.countByTestResultId(testResult.getId()));

        return dto;
    }

    private TestResult convertToEntity(TestResultDTO testResultDTO) {
        TestResult testResult = new TestResult();
        testResult.setId(testResultDTO.getId());
        testResult.setStatus(testResultDTO.getStatus());
        testResult.setStartTime(testResultDTO.getStartTime());
        testResult.setEndTime(testResultDTO.getEndTime());
        testResult.setDurationMs(testResultDTO.getDurationMs());
        testResult.setErrorMessage(testResultDTO.getErrorMessage());
        testResult.setStackTrace(testResultDTO.getStackTrace());

        if (testResultDTO.getCreatedAt() == null) {
            testResult.setCreatedAt(LocalDateTime.now());
        } else {
            testResult.setCreatedAt(testResultDTO.getCreatedAt());
        }

        if (testResultDTO.getTestRunId() != null) {
            TestRun testRun = testRunRepository.findById(testResultDTO.getTestRunId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "TestRun not found with id: " + testResultDTO.getTestRunId()));
            testResult.setTestRun(testRun);
        }

        if (testResultDTO.getTestCaseId() != null) {
            TestCase testCase = testCaseRepository.findById(testResultDTO.getTestCaseId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "TestCase not found with id: " + testResultDTO.getTestCaseId()));
            testResult.setTestCase(testCase);
        }

        return testResult;
    }

    public boolean isSuccess(TestResult testResult) {
        return "PASS".equals(testResult.getStatus());
    }
}
