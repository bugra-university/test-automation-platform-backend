package com.project_team09.api.service;

import com.project_team09.api.model.dto.TestSuiteDTO;
import com.project_team09.api.model.entity.Project;
import com.project_team09.api.model.entity.TestSuite;
import com.project_team09.api.repository.ProjectRepository;
import com.project_team09.api.repository.TestSuiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestSuiteService {

    private final TestSuiteRepository testSuiteRepository;
    private final ProjectRepository projectRepository;

    public TestSuiteService(TestSuiteRepository testSuiteRepository, ProjectRepository projectRepository) {
        this.testSuiteRepository = testSuiteRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<TestSuiteDTO> getAllTestSuites() {
        return testSuiteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestSuiteDTO getTestSuiteById(Long id) {
        return testSuiteRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("TestSuite not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TestSuiteDTO> getTestSuitesByProjectId(Long projectId) {
        return testSuiteRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TestSuiteDTO createTestSuite(TestSuiteDTO testSuiteDTO) {
        TestSuite testSuite = convertToEntity(testSuiteDTO);
        TestSuite savedTestSuite = testSuiteRepository.save(testSuite);
        return convertToDTO(savedTestSuite);
    }

    @Transactional
    public TestSuiteDTO updateTestSuite(Long id, TestSuiteDTO testSuiteDTO) {
        if (!testSuiteRepository.existsById(id)) {
            throw new EntityNotFoundException("TestSuite not found with id: " + id);
        }

        TestSuite testSuite = convertToEntity(testSuiteDTO);
        testSuite.setId(id);
        TestSuite updatedTestSuite = testSuiteRepository.save(testSuite);
        return convertToDTO(updatedTestSuite);
    }

    @Transactional
    public void deleteTestSuite(Long id) {
        if (!testSuiteRepository.existsById(id)) {
            throw new EntityNotFoundException("TestSuite not found with id: " + id);
        }
        testSuiteRepository.deleteById(id);
    }

    private TestSuiteDTO convertToDTO(TestSuite testSuite) {
        TestSuiteDTO dto = new TestSuiteDTO();
        dto.setId(testSuite.getId());
        dto.setName(testSuite.getName());
        dto.setDescription(testSuite.getDescription());
        dto.setProjectId(testSuite.getProject().getId());
        return dto;
    }

    private TestSuite convertToEntity(TestSuiteDTO testSuiteDTO) {
        TestSuite testSuite = new TestSuite();
        testSuite.setId(testSuiteDTO.getId());
        testSuite.setName(testSuiteDTO.getName());
        testSuite.setDescription(testSuiteDTO.getDescription());

        if (testSuiteDTO.getProjectId() != null) {
            Project project = projectRepository.findById(testSuiteDTO.getProjectId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Project not found with id: " + testSuiteDTO.getProjectId()));
            testSuite.setProject(project);
        }

        return testSuite;
    }
}
