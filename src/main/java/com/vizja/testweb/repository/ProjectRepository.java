package com.vizja.testweb.repository;
import com.vizja.testweb.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    List<Project> findByOwnerUsernameOrderByCreatedAtDesc(String ownerUsername);
}

