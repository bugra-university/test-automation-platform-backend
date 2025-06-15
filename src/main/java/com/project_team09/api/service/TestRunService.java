package com.project_team09.api.service;

import com.project_team09.api.model.dto.TestRunDTO;
import com.project_team09.api.model.entity.Project;
import com.project_team09.api.model.entity.TestRun;
import com.project_team09.api.model.entity.TestResult;
import com.project_team09.api.model.entity.TestSuite;
import com.project_team09.api.repository.TestRunRepository;
import com.project_team09.api.repository.TestSuiteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestRunService {

    private static final Logger logger = LoggerFactory.getLogger(TestRunService.class);

    private final TestRunRepository testRunRepository;
    private final ProjectService projectService;
    private final TestSuiteRepository testSuiteRepository;

    @Transactional(readOnly = true)
    public List<TestRunDTO> getAllTestRuns() {
        return testRunRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestRunDTO> getActiveTestRuns() {
        return testRunRepository.findByStatus("RUNNING").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestRunDTO> getTestRunsByProject(Long projectId) {
        return testRunRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestRunDTO getTestRunById(Long id) {
        TestRun testRun = testRunRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test run not found: " + id));
        return convertToDTO(testRun);
    }

    @Transactional
    public TestRunDTO createTestRun(TestRunDTO testRunDTO) {
        TestRun testRun = new TestRun();
        testRun.setName(testRunDTO.getName());
        testRun.setStatus("RUNNING");
        testRun.setStartTime(LocalDateTime.now());
        testRun.setTriggeredBy(testRunDTO.getTriggeredBy());
        testRun.setEnvironment(testRunDTO.getEnvironment());
        testRun.setGitCommitHash(testRunDTO.getGitCommitHash());
        testRun.setParameters(testRunDTO.getParameters());

        // "Add project (if projectId is specified)
        if (testRunDTO.getProjectId() != null) {
            Project project = projectService.getProjectEntityById(testRunDTO.getProjectId());
            testRun.setProject(project);
        }

        TestRun savedTestRun = testRunRepository.save(testRun);
        return convertToDTO(savedTestRun);
    }

    /**
     * Creates and executes a test run for the specified Test Suite.
     * 
     * @param testSuiteId Test Suite ID
     * @param testRunDTO  Test run information
     * @return The result of the executed test run
     */
    @Transactional
    public TestRunDTO createAndRunTestSuite(Long testSuiteId, TestRunDTO testRunDTO) {
        // Find the Test Suite
        TestSuite testSuite = testSuiteRepository.findById(testSuiteId)
                .orElseThrow(() -> new EntityNotFoundException("Test Suite not found: " + testSuiteId));

        // Create the Test Run
        TestRun testRun = new TestRun();
        testRun.setName(testRunDTO.getName());
        testRun.setStatus("RUNNING");
        testRun.setStartTime(LocalDateTime.now());
        testRun.setTriggeredBy(testRunDTO.getTriggeredBy() != null ? testRunDTO.getTriggeredBy() : "MANUAL");
        testRun.setEnvironment(testRunDTO.getEnvironment() != null ? testRunDTO.getEnvironment() : "TEST");
        testRun.setGitCommitHash(testRunDTO.getGitCommitHash());
        testRun.setParameters(testRunDTO.getParameters());

        // Set the Project
        testRun.setProject(testSuite.getProject());

        // Save the Test Run
        TestRun savedTestRun = testRunRepository.save(testRun);

        try {
            // Run the test in Docker container
            boolean success = runTestInDocker(testSuite, savedTestRun);

            // Complete the test
            if (success) {
                savedTestRun.setStatus("COMPLETED");
            } else {
                savedTestRun.setStatus("FAILED");
            }
        } catch (Exception e) {
            logger.error("Test execution error: {}", e.getMessage(), e);
            savedTestRun.setStatus("FAILED");
        } finally {
            // Update the test run
            savedTestRun.setEndTime(LocalDateTime.now());
            savedTestRun.setUpdatedAt(LocalDateTime.now());
            savedTestRun = testRunRepository.save(savedTestRun);
        }

        return convertToDTO(savedTestRun);
    }

    /**
     * Executes the test run within a local environment.
     */
    private boolean runTestInDocker(TestSuite testSuite, TestRun testRun) {
        try {
            logger.info("Running test locally. Test Suite: {}, Test Run ID: {}",
                    testSuite.getName(), testRun.getId());

            // Specify TestNG XML file (from test suite's folder path)
            String testngXml = testSuite.getFolderPath() != null ? testSuite.getFolderPath() : "testng.xml";

            // Setup process builder
            ProcessBuilder processBuilder = new ProcessBuilder();

            // Pass the test run ID as an environment variable
            processBuilder.environment().put("TEST_RUN_ID", String.valueOf(testRun.getId()));

            // Add test run parameters
            if (testRun.getParameters() != null && !testRun.getParameters().isEmpty()) {
                for (var entry : testRun.getParameters().entrySet()) {
                    processBuilder.environment().put(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }

            // Create Maven command
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                processBuilder.command("cmd.exe", "/c", "mvn", "test", "-DsuiteXmlFile=" + testngXml);
            } else {
                processBuilder.command("mvn", "test", "-DsuiteXmlFile=" + testngXml);
            }

            // Set working directory to the project root
            processBuilder.directory(new java.io.File("."));
            processBuilder.redirectErrorStream(true);

            // Run the command
            logger.info("Running command: mvn test -DsuiteXmlFile={}", testngXml);
            Process process = processBuilder.start();

            // Read the combined output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("Test Output: {}", line);
                }
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            logger.info("Process exit code: {}", exitCode);

            // 0 exit code is considered successful
            return exitCode == 0;
        } catch (Exception e) {
            logger.error("Test execution error: {}", e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public TestRunDTO updateTestRunStatus(Long id, String status) {
        TestRun testRun = testRunRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test run not found: " + id));

        testRun.setStatus(status);
        if (status.equals("COMPLETED") || status.equals("FAILED") || status.equals("CANCELLED")) {
            testRun.setEndTime(LocalDateTime.now());
        }
        testRun.setUpdatedAt(LocalDateTime.now());

        TestRun updatedTestRun = testRunRepository.save(testRun);
        return convertToDTO(updatedTestRun);
    }

    @Transactional
    public void stopTestRun(Long id) {
        TestRun testRun = testRunRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test run not found: " + id));

        if ("RUNNING".equals(testRun.getStatus())) {
            testRun.setStatus("CANCELLED");
            testRun.setEndTime(LocalDateTime.now());
            testRun.setUpdatedAt(LocalDateTime.now());
            testRunRepository.save(testRun);
        }
    }

    private TestRunDTO convertToDTO(TestRun testRun) {
        TestRunDTO dto = new TestRunDTO();
        dto.setId(testRun.getId());
        dto.setName(testRun.getName());
        dto.setStartTime(testRun.getStartTime());
        dto.setEndTime(testRun.getEndTime());
        dto.setStatus(testRun.getStatus());
        dto.setTriggeredBy(testRun.getTriggeredBy());
        dto.setEnvironment(testRun.getEnvironment());
        dto.setGitCommitHash(testRun.getGitCommitHash());
        dto.setParameters(testRun.getParameters());
        dto.setCreatedAt(testRun.getCreatedAt());
        dto.setUpdatedAt(testRun.getUpdatedAt());

        // Add project details.
        if (testRun.getProject() != null) {
            dto.setProjectId(testRun.getProject().getId());
            dto.setProjectName(testRun.getProject().getName());
        }

        // Calculate test result statistics
        List<TestResult> results = testRun.getTestResults();
        dto.setTotalTestCases(results.size());
        dto.setPassedTests((int) results.stream().filter(r -> "PASS".equals(r.getStatus())).count());
        dto.setFailedTests((int) results.stream().filter(r -> "FAIL".equals(r.getStatus())).count());
        dto.setSkippedTests((int) results.stream().filter(r -> "SKIP".equals(r.getStatus())).count());

        return dto;
    }
}
