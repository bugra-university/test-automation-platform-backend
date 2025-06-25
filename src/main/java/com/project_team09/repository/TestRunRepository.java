package com.project_team09.repository;

import com.project_team09.model.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestRunRepository extends JpaRepository<TestRun, Long> {

    // Find by project
    List<TestRun> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    // Find by status
    List<TestRun> findByStatus(String status);

    // Find running tests
    List<TestRun> findByStatusOrderByStartTimeDesc(String status);

    // Find by project and status
    List<TestRun> findByProjectIdAndStatus(Long projectId, String status);

    // Find recent test runs
    @Query("SELECT tr FROM TestRun tr WHERE tr.createdAt >= :since ORDER BY tr.createdAt DESC")
    List<TestRun> findRecentTestRuns(@Param("since") LocalDateTime since);

    // Find test runs by name pattern
    List<TestRun> findByNameContainingIgnoreCase(String namePattern);

    // Find test runs for a specific project in date range
    @Query("SELECT tr FROM TestRun tr WHERE tr.projectId = :projectId " +
           "AND tr.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY tr.createdAt DESC")
    List<TestRun> findByProjectAndDateRange(
        @Param("projectId") Long projectId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // Count test runs by status for a project
    @Query("SELECT COUNT(tr) FROM TestRun tr WHERE tr.projectId = :projectId AND tr.status = :status")
    long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") String status);

    // Find currently running test for specific test case
    @Query("SELECT tr FROM TestRun tr WHERE tr.name LIKE %:testCaseName% AND tr.status = 'RUNNING'")
    Optional<TestRun> findRunningTestByName(@Param("testCaseName") String testCaseName);
} 