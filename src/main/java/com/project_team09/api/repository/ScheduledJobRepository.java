package com.project_team09.api.repository;

import com.project_team09.api.model.entity.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {
    List<ScheduledJob> findByIsActiveTrue();

    List<ScheduledJob> findByTestSuiteId(Long testSuiteId);

    List<ScheduledJob> findByNextRunBeforeAndIsActiveTrue(LocalDateTime time);
}
