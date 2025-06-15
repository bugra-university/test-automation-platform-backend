package com.project_team09.api.repository;

import com.project_team09.api.model.entity.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRunRepository extends JpaRepository<TestRun, Long> {
    List<TestRun> findByStatus(String status);

    List<TestRun> findByProjectId(Long projectId);

    List<TestRun> findByGitCommitHash(String gitCommitHash);

    List<TestRun> findByTriggeredBy(String triggeredBy);
}
