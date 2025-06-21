package com.project_team09.repository;

import com.project_team09.model.ProductBacklogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductBacklogItemRepository extends JpaRepository<ProductBacklogItem, Long> {

    List<ProductBacklogItem> findByProjectId(Long projectId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductBacklogItem pbi WHERE pbi.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);
} 