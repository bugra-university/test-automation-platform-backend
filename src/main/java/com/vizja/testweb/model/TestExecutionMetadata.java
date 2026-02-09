package com.vizja.testweb.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_execution_metadata")
public class TestExecutionMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    @Column(name = "user_story_id", nullable = false, length = 10)
    private String userStoryId;
    @Column(name = "test_case_id", nullable = false, length = 10)
    private String testCaseId;
    @Column(name = "execution_time", nullable = false)
    private LocalDateTime executionTime;
    @Column(name = "report_file_name", length = 255)
    private String reportFileName;
    @Column(name = "report_file_path", length = 500)
    private String reportFilePath;
    @Column(name = "status", length = 20)
    private String status = "RUNNING";
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public TestExecutionMetadata() {
        this.createdAt = LocalDateTime.now();
    }

    public TestExecutionMetadata(Long projectId, String userStoryId, String testCaseId, LocalDateTime executionTime) {
        this();
        this.projectId = projectId;
        this.userStoryId = userStoryId;
        this.testCaseId = testCaseId;
        this.executionTime = executionTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public String getReportFileName() {
        return reportFileName;
    }

    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
