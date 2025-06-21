package com.project_team09.repository;

import com.project_team09.model.ExcelSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelSheetRepository extends JpaRepository<ExcelSheet, Long> {
    // Temel CRUD işlemleri JpaRepository tarafından sağlanıyor
} 