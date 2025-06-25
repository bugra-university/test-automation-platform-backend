package com.project_team09.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_results")
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_run_id", nullable = false)
    private Long testRunId;

    @Column(name = "test_case_id", nullable = false)
    private Long testCaseId;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PASS, FAIL, SKIP, ERROR

    @Column(name = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Foreign key relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_run_id", insertable = false, updatable = false)
    private TestRun testRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", insertable = false, updatable = false)
    private TestCase testCase;

    // Constructors
    public TestResult() {
        this.createdAt = LocalDateTime.now();
    }

    public TestResult(Long testRunId, Long testCaseId, String status) {
        this();
        this.testRunId = testRunId;
        this.testCaseId = testCaseId;
        this.status = status;
        this.startTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestRunId() {
        return testRunId;
    }

    public void setTestRunId(Long testRunId) {
        this.testRunId = testRunId;
    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (this.startTime != null && endTime != null) {
            this.durationMs = java.time.Duration.between(this.startTime, endTime).toMillis();
        }
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public TestRun getTestRun() {
        return testRun;
    }

    public void setTestRun(TestRun testRun) {
        this.testRun = testRun;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    // Helper methods
    public boolean isPassed() {
        return "PASS".equals(status);
    }

    public boolean isFailed() {
        return "FAIL".equals(status);
    }

    public boolean isSkipped() {
        return "SKIP".equals(status);
    }

    public boolean hasError() {
        return "ERROR".equals(status);
    }

    public void markAsCompleted(boolean success) {
        this.endTime = LocalDateTime.now();
        this.status = success ? "PASS" : "FAIL";
        if (this.startTime != null) {
            this.durationMs = java.time.Duration.between(this.startTime, this.endTime).toMillis();
        }
    }

    public void markAsCompleted(boolean success, String errorMessage) {
        markAsCompleted(success);
        this.errorMessage = errorMessage;
    }

    public void markAsCompleted(boolean success, String errorMessage, String stackTrace) {
        markAsCompleted(success, errorMessage);
        this.stackTrace = stackTrace;
    }
} 