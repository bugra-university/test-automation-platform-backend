package com.project_team09.api.service;

import com.project_team09.api.model.dto.ReportDTO;
import com.project_team09.api.model.entity.Report;
import com.project_team09.api.model.entity.TestRun;
import com.project_team09.api.repository.ReportRepository;
import com.project_team09.api.repository.TestRunRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final TestRunRepository testRunRepository;

    public ReportService(ReportRepository reportRepository, TestRunRepository testRunRepository) {
        this.reportRepository = reportRepository;
        this.testRunRepository = testRunRepository;
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportDTO getReportById(Long id) {
        return reportRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> getReportsByTestRunId(Long testRunId) {
        return reportRepository.findByTestRunId(testRunId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> getReportsByType(String reportType) {
        return reportRepository.findByReportType(reportType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDTO createReport(ReportDTO reportDTO) {
        Report report = convertToEntity(reportDTO);
        Report savedReport = reportRepository.save(report);
        return convertToDTO(savedReport);
    }

    @Transactional
    public ReportDTO updateReport(Long id, ReportDTO reportDTO) {
        if (!reportRepository.existsById(id)) {
            throw new EntityNotFoundException("Report not found with id: " + id);
        }

        Report report = convertToEntity(reportDTO);
        report.setId(id);
        Report updatedReport = reportRepository.save(report);
        return convertToDTO(updatedReport);
    }

    @Transactional
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new EntityNotFoundException("Report not found with id: " + id);
        }
        reportRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + id));

        File file = new File(report.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("Report file not found: " + report.getFilePath());
        }

        try {
            Path path = Paths.get(report.getFilePath());
            byte[] fileContent = Files.readAllBytes(path);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getMediaTypeForReport(report.getReportType()));
            headers.setContentDispositionFormData("attachment", report.getFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (IOException e) {
            throw new RuntimeException("Error reading report file: " + e.getMessage());
        }
    }

    private MediaType getMediaTypeForReport(String reportType) {
        switch (reportType.toUpperCase()) {
            case "PDF":
                return MediaType.APPLICATION_PDF;
            case "HTML":
                return MediaType.TEXT_HTML;
            case "XML":
                return MediaType.APPLICATION_XML;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private ReportDTO convertToDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setTestRunId(report.getTestRun().getId());
        dto.setReportType(report.getReportType());
        dto.setFilePath(report.getFilePath());
        dto.setFileName(report.getFileName());
        dto.setGeneratedAt(report.getGeneratedAt());

        // Additional fields for front-end display
        dto.setTestRunName(report.getTestRun().getName());
        dto.setProjectId(report.getTestRun().getProject().getId());
        dto.setProjectName(report.getTestRun().getProject().getName());

        return dto;
    }

    private Report convertToEntity(ReportDTO reportDTO) {
        Report report = new Report();
        report.setId(reportDTO.getId());
        report.setReportType(reportDTO.getReportType());
        report.setFilePath(reportDTO.getFilePath());
        report.setFileName(reportDTO.getFileName());

        if (reportDTO.getGeneratedAt() != null) {
            report.setGeneratedAt(reportDTO.getGeneratedAt());
        } else {
            report.setGeneratedAt(LocalDateTime.now());
        }

        if (reportDTO.getTestRunId() != null) {
            TestRun testRun = testRunRepository.findById(reportDTO.getTestRunId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "TestRun not found with id: " + reportDTO.getTestRunId()));
            report.setTestRun(testRun);
        }

        return report;
    }
}
