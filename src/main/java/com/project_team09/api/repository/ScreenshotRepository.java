package com.project_team09.api.repository;

import com.project_team09.api.model.entity.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {
    List<Screenshot> findByTestResultId(Long testResultId);

    List<Screenshot> findByStepId(Long stepId);

    int countByTestResultId(Long testResultId);
}
