package com.vizja.testweb.repository;

import com.vizja.testweb.model.TestRun;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface TestRunRepository extends JpaRepository<TestRun, Long> {
    List<TestRun> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<TestRun> findByStatus(String status);

    List<TestRun> findByStatusOrderByStartTimeDesc(String status);

    List<TestRun> findByProjectIdAndStatus(Long projectId, String status);

    @Query("SELECT tr FROM TestRun tr WHERE tr.createdAt >= :since ORDER BY tr.createdAt DESC")
    List<TestRun> findRecentTestRuns(@Param("since") LocalDateTime since);

    List<TestRun> findByNameContainingIgnoreCase(String namePattern);

    @Query("SELECT tr FROM TestRun tr WHERE tr.projectId = :projectId " +
            "AND tr.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY tr.createdAt DESC")
    List<TestRun> findByProjectAndDateRange(
            @Param("projectId") Long projectId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(tr) FROM TestRun tr WHERE tr.projectId = :projectId AND tr.status = :status")
    long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") String status);

    @Query("SELECT tr FROM TestRun tr WHERE tr.name LIKE %:testCaseName% AND tr.status = 'RUNNING'")
    Optional<TestRun> findRunningTestByName(@Param("testCaseName") String testCaseName);
}
