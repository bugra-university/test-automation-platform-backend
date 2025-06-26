package com.project_team09.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_reports")
public class TestReport {
    
    @Id
    private String id;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "test_case", length = 1000)
    private String testCase;
    
    @Column(name = "status")
    private String status; // passed, failed, mixed
    
    @Column(name = "passed_count")
    private Integer passedCount;
    
    @Column(name = "total_count")
    private Integer totalCount;
    
    @Column(name = "executed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executedAt;
    
    @Column(name = "file_size")
    private String fileSize;
    
    @Column(name = "duration")
    private String duration;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // Test details as JSON or separate fields
    @Column(name = "test_name")
    private String testName;
    
    @Column(name = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    @Column(name = "steps_count")
    private Integer stepsCount;
    
    // Constructors
    public TestReport() {}
    
    public TestReport(String id, String fileName, String title, String description, 
                     String testCase, String status, Integer passedCount, Integer totalCount,
                     LocalDateTime executedAt, String fileSize, String duration, String filePath) {
        this.id = id;
        this.fileName = fileName;
        this.title = title;
        this.description = description;
        this.testCase = testCase;
        this.status = status;
        this.passedCount = passedCount;
        this.totalCount = totalCount;
        this.executedAt = executedAt;
        this.fileSize = fileSize;
        this.duration = duration;
        this.filePath = filePath;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTestCase() { return testCase; }
    public void setTestCase(String testCase) { this.testCase = testCase; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getPassedCount() { return passedCount; }
    public void setPassedCount(Integer passedCount) { this.passedCount = passedCount; }
    
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
    
    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Integer getStepsCount() { return stepsCount; }
    public void setStepsCount(Integer stepsCount) { this.stepsCount = stepsCount; }
} 