package com.project_team09.api.repository;

import com.project_team09.api.model.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findBySuiteId(Long suiteId);

    Optional<TestCase> findByClassNameAndMethodName(String className, String methodName);

    Optional<TestCase> findByUserStoryIdAndTestCaseId(String userStoryId, String testCaseId);
}
