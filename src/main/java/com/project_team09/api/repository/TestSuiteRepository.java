package com.project_team09.api.repository;

import com.project_team09.api.model.entity.TestSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSuiteRepository extends JpaRepository<TestSuite, Long> {
    List<TestSuite> findByProjectId(Long projectId);

    int countByProject(com.project_team09.api.model.entity.Project project);
}
