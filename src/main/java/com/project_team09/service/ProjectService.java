package com.project_team09.service;

import com.project_team09.model.Project;
import com.project_team09.model.User;
import com.project_team09.repository.ProjectRepository;
import com.project_team09.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
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
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
