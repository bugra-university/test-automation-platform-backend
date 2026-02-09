package com.vizja.testweb.repository;
import com.vizja.testweb.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByTestRunIdOrderByCreatedAtDesc(Long testRunId);
    List<TestResult> findByTestCaseIdOrderByCreatedAtDesc(Long testCaseId);
    List<TestResult> findByTestRunIdAndStatus(Long testRunId, String status);
    Optional<TestResult> findByTestRunIdAndTestCaseId(Long testRunId, Long testCaseId);
    @Query("SELECT COUNT(tr) FROM TestResult tr WHERE tr.testRunId = :testRunId AND tr.status = :status")
    long countByTestRunIdAndStatus(@Param("testRunId") Long testRunId, @Param("status") String status);
    @Query("SELECT tr.status, COUNT(tr) FROM TestResult tr WHERE tr.testRunId = :testRunId GROUP BY tr.status")
    List<Object[]> getTestRunStatistics(@Param("testRunId") Long testRunId);
    @Query("SELECT tr FROM TestResult tr WHERE tr.testRunId = :testRunId AND tr.status = 'FAIL' AND tr.errorMessage IS NOT NULL")
    List<TestResult> findFailedTestsWithErrors(@Param("testRunId") Long testRunId);
    @Query("SELECT tr FROM TestResult tr WHERE tr.testRunId = :testRunId AND tr.durationMs IS NOT NULL ORDER BY tr.durationMs DESC")
    List<TestResult> findSlowestTests(@Param("testRunId") Long testRunId);
    @Query("SELECT AVG(tr.durationMs) FROM TestResult tr WHERE tr.testCaseId = :testCaseId AND tr.durationMs IS NOT NULL")
    Double getAverageDurationForTestCase(@Param("testCaseId") Long testCaseId);
    @Query("SELECT tr FROM TestResult tr WHERE tr.testCaseId = :testCaseId ORDER BY tr.createdAt DESC")
    List<TestResult> findRecentResultsForTestCase(@Param("testCaseId") Long testCaseId);
    void deleteByTestRunId(Long testRunId);
} 
