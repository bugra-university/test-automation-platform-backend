package com.project_team09.api.repository;

import com.project_team09.api.model.entity.TestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestStepRepository extends JpaRepository<TestStep, Long> {
    List<TestStep> findByTestResultId(Long testResultId);

    List<TestStep> findByTestResultIdOrderByOrderNumberAsc(Long testResultId);

    int countByTestResultId(Long testResultId);
}
