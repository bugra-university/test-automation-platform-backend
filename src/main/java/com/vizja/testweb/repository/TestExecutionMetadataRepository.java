package com.vizja.testweb.repository;

import com.vizja.testweb.model.TestExecutionMetadata;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface TestExecutionMetadataRepository extends JpaRepository<TestExecutionMetadata, Long> {
    List<TestExecutionMetadata> findByProjectId(Long projectId);

    List<TestExecutionMetadata> findByProjectIdOrderByExecutionTimeDesc(Long projectId);

    Optional<TestExecutionMetadata> findByReportFileName(String reportFileName);

    Optional<TestExecutionMetadata> findByReportFilePath(String reportFilePath);

    @Query("SELECT t FROM TestExecutionMetadata t WHERE t.projectId = :projectId AND t.executionTime >= :startTime AND t.executionTime <= :endTime ORDER BY t.executionTime DESC")
    List<TestExecutionMetadata> findByProjectIdAndExecutionTimeBetween(
            @Param("projectId") Long projectId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT t FROM TestExecutionMetadata t WHERE t.reportFileName LIKE %:fileNamePattern% ORDER BY t.executionTime DESC")
    List<TestExecutionMetadata> findByReportFileNameContaining(@Param("fileNamePattern") String fileNamePattern);
}
