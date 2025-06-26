package com.project_team09.controller;

import com.project_team09.model.TestReport;
import com.project_team09.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Get all test reports from TestOutput directory
     */
    @GetMapping
    public ResponseEntity<List<TestReport>> getAllReports() {
        try {
            List<TestReport> reports = reportService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get reports filtered by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TestReport>> getReportsByStatus(@PathVariable String status) {
        try {
            List<TestReport> reports = reportService.getReportsByStatus(status);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get a specific report by ID
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<TestReport> getReportById(@PathVariable String reportId) {
        try {
            TestReport report = reportService.getReportById(reportId);
            if (report != null) {
                return ResponseEntity.ok(report);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Download a report file
     */
    @GetMapping("/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable String reportId) {
        try {
            Resource resource = reportService.getReportFile(reportId);
            if (resource != null && resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * View report content (HTML)
     */
    @GetMapping("/{reportId}/view")
    public ResponseEntity<String> viewReport(@PathVariable String reportId) {
        try {
            String content = reportService.getReportContent(reportId);
            if (content != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(content);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete a report file
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable String reportId) {
        try {
            boolean deleted = reportService.deleteReport(reportId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get report statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Object> getReportStats() {
        try {
            Object stats = reportService.getReportStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 