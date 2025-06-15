package com.project_team09.api.repository;

import com.project_team09.api.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByTestRunId(Long testRunId);

    List<Report> findByReportType(String reportType);
}
