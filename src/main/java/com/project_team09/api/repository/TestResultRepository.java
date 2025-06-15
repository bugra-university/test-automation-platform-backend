package com.project_team09.api.repository;

import com.project_team09.api.model.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByTestRunId(Long testRunId);

    List<TestResult> findByTestCaseId(Long testCaseId);

    List<TestResult> findByStatus(String status);
}
