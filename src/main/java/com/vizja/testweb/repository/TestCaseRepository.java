package com.vizja.testweb.repository;
import com.vizja.testweb.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProjectId(Long projectId);
    List<TestCase> findByProjectIdAndUserStoryId(Long projectId, String userStoryId);
    @Modifying
    @Transactional
    @Query("DELETE FROM TestCase tc WHERE tc.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);
} 
