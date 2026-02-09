package com.vizja.testweb.service;
import com.vizja.testweb.model.Project;
import com.vizja.testweb.model.User;
import com.vizja.testweb.model.ExcelFile;
import com.vizja.testweb.repository.ProjectRepository;
import com.vizja.testweb.repository.UserRepository;
import com.vizja.testweb.repository.ExcelFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
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
        return projectRepository.findAll();
    }
    public List<Project> getProjectsByOwnerId(Long ownerId) {
        return projectRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }
    public Project createProject(String name, String description) {
        User defaultUser = userRepository.findByUsername("testuser");
        if (defaultUser == null) {
            defaultUser = new User();
            defaultUser.setUsername("testuser");
            defaultUser.setEmail("test@example.com");
            defaultUser.setPassword("encoded_password"); 
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
            deletePhysicalExcelFiles(id);
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
    private void deletePhysicalExcelFiles(Long projectId) {
        try {
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
        }
    }
    public Map<String, Object> getProjectDatabaseActivity(Long projectId) {
        Map<String, Object> activity = new HashMap<>();
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (!projectOpt.isPresent()) {
                activity.put("error", "Project not found");
                return activity;
            }
            Project project = projectOpt.get();
            activity.put("projectCreatedAt", project.getCreatedAt());
            activity.put("projectUpdatedAt", project.getUpdatedAt());
            List<ExcelFile> excelFiles = excelFileRepository.findByProjectId(projectId);
            if (!excelFiles.isEmpty()) {
                ExcelFile latestExcel = excelFiles.get(0); 
                activity.put("lastExcelParseDate", latestExcel.getUploadDate());
                activity.put("excelLastModified", latestExcel.getLastModified());
                activity.put("hasExcelFile", true);
            } else {
                activity.put("lastExcelParseDate", null);
                activity.put("excelLastModified", null);
                activity.put("hasExcelFile", false);
            }
        } catch (Exception e) {
            activity.put("error", "Failed to fetch database activity: " + e.getMessage());
        }
        return activity;
    }
}

