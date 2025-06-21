package com.project_team09.service;

import com.project_team09.model.Project;
import com.project_team09.model.User;
import com.project_team09.model.ExcelFile;
import com.project_team09.repository.ProjectRepository;
import com.project_team09.repository.UserRepository;
import com.project_team09.repository.ExcelFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ExcelFileRepository excelFileRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ExcelFileRepository excelFileRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.excelFileRepository = excelFileRepository;
    }

    public List<Project> getAllProjects() {
        // For now, return all projects. In production, this should be filtered by user
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByOwnerId(Long ownerId) {
        return projectRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    public Project createProject(String name, String description) {
        // For now, use a default user. In production, get from security context
        User defaultUser = userRepository.findByUsername("testuser");
        if (defaultUser == null) {
            // Create a default user if not exists
            defaultUser = new User();
            defaultUser.setUsername("testuser");
            defaultUser.setEmail("test@example.com");
            defaultUser.setPassword("encoded_password"); // This should be properly encoded
            defaultUser.setFirstName("Test");
            defaultUser.setLastName("User");
            defaultUser = userRepository.save(defaultUser);
        }

        Project project = new Project(name, description, defaultUser.getId(), defaultUser.getUsername());
        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        Optional<Project> project = projectRepository.findById(id);
        return project.orElse(null);
    }

    public Project updateProject(Long id, String name, String description) {
        Optional<Project> existingProject = projectRepository.findById(id);
        if (existingProject.isPresent()) {
            Project project = existingProject.get();
            project.setName(name);
            project.setDescription(description);
            return projectRepository.save(project);
        }
        return null;
    }

    public boolean deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            // First, delete physical Excel files
            deletePhysicalExcelFiles(id);
            
            // Then delete the project (CASCADE DELETE will handle related data)
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void deletePhysicalExcelFiles(Long projectId) {
        try {
            // Get all Excel files for this project
            List<ExcelFile> excelFiles = excelFileRepository.findByProjectId(projectId);
            
            for (ExcelFile excelFile : excelFiles) {
                String filePath = excelFile.getFilePath();
                if (filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        if (file.delete()) {
                            System.out.println("Successfully deleted file: " + filePath);
                        } else {
                            System.err.println("Failed to delete file: " + filePath);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting physical Excel files for project " + projectId + ": " + e.getMessage());
            // Don't throw exception here as we still want to delete the project from database
        }
    }
}
