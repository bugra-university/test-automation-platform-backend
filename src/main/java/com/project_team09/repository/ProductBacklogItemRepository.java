package com.project_team09.repository;

import com.project_team09.model.ProductBacklogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBacklogItemRepository extends JpaRepository<ProductBacklogItem, Long> {

    List<ProductBacklogItem> findByProjectId(Long projectId);
    
} 