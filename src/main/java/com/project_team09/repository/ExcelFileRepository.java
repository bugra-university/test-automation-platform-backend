package com.project_team09.repository;

import com.project_team09.model.ExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelFileRepository extends JpaRepository<ExcelFile, Long> {
    // Temel CRUD işlemleri JpaRepository tarafından sağlanıyor
} 