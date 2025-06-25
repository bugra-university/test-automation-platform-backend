package com.project_team09.repository;

import com.project_team09.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    // Find by test run
    List<TestResult> findByTestRunIdOrderByCreatedAtDesc(Long testRunId);

    // Find by test case
    List<TestResult> findByTestCaseIdOrderByCreatedAtDesc(Long testCaseId);

    // Find by test run and status
    List<TestResult> findByTestRunIdAndStatus(Long testRunId, String status);

    // Find specific test result
    Optional<TestResult> findByTestRunIdAndTestCaseId(Long testRunId, Long testCaseId);

    // Count results by status for a test run
    @Query("SELECT COUNT(tr) FROM TestResult tr WHERE tr.testRunId = :testRunId AND tr.status = :status")
    long countByTestRunIdAndStatus(@Param("testRunId") Long testRunId, @Param("status") String status);

    // Get test result statistics for a test run
    @Query("SELECT tr.status, COUNT(tr) FROM TestResult tr WHERE tr.testRunId = :testRunId GROUP BY tr.status")
    List<Object[]> getTestRunStatistics(@Param("testRunId") Long testRunId);

    // Find failed test results with error messages
    @Query("SELECT tr FROM TestResult tr WHERE tr.testRunId = :testRunId AND tr.status = 'FAIL' AND tr.errorMessage IS NOT NULL")
    List<TestResult> findFailedTestsWithErrors(@Param("testRunId") Long testRunId);

    // Find slowest tests in a test run
    @Query("SELECT tr FROM TestResult tr WHERE tr.testRunId = :testRunId AND tr.durationMs IS NOT NULL ORDER BY tr.durationMs DESC")
    List<TestResult> findSlowestTests(@Param("testRunId") Long testRunId);

    // Get average duration for a test case
    @Query("SELECT AVG(tr.durationMs) FROM TestResult tr WHERE tr.testCaseId = :testCaseId AND tr.durationMs IS NOT NULL")
    Double getAverageDurationForTestCase(@Param("testCaseId") Long testCaseId);

    // Find recent results for a test case
    @Query("SELECT tr FROM TestResult tr WHERE tr.testCaseId = :testCaseId ORDER BY tr.createdAt DESC")
    List<TestResult> findRecentResultsForTestCase(@Param("testCaseId") Long testCaseId);

    // Delete results for a test run (cleanup)
    void deleteByTestRunId(Long testRunId);
} 