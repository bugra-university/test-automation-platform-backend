package com.project_team09.api.controller;

import com.project_team09.api.model.dto.ReportDTO;
import com.project_team09.api.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping("/test-run/{testRunId}")
    public ResponseEntity<List<ReportDTO>> getReportsByTestRunId(@PathVariable Long testRunId) {
        return ResponseEntity.ok(reportService.getReportsByTestRunId(testRunId));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        return reportService.downloadReport(id);
    }

    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@Valid @RequestBody ReportDTO reportDTO) {
        ReportDTO createdReport = reportService.createReport(reportDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
