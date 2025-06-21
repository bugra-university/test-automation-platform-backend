package com.project_team09.repository;

import com.project_team09.model.TestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TestStepRepository extends JpaRepository<TestStep, Long> {
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM test_steps WHERE test_case_id IN (SELECT id FROM test_cases WHERE project_id = :projectId)", nativeQuery = true)
    void deleteByProjectId(@Param("projectId") Long projectId);
} 