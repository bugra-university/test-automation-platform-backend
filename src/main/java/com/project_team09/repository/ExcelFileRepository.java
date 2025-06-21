package com.project_team09.repository;

import com.project_team09.model.ExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ExcelFileRepository extends JpaRepository<ExcelFile, Long> {
    // Temel CRUD işlemleri JpaRepository tarafından sağlanıyor
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM project_excel_files WHERE project_id = :projectId", nativeQuery = true)
    void deleteByProjectId(@Param("projectId") Long projectId);
} 