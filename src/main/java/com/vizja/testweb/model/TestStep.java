package com.vizja.testweb.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "test_steps")
public class TestStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", nullable = false)
    @JsonIgnore
    private TestCase testCase;
    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;
    @Column(name = "step_description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "test_data", columnDefinition = "TEXT")
    private String testData;
    @Column(name = "expected_result", columnDefinition = "TEXT")
    private String expectedResult;
    @Column(name = "actual_result", columnDefinition = "TEXT")
    private String actualResult;
    @Column(name = "is_home")
    private Boolean isHome = false;
    @Column(name = "url", columnDefinition = "TEXT")
    private String url;
    @Column(name = "screenshot_path", columnDefinition = "TEXT")
    private String screenshotPath;
    @Column(name = "status")
    private String status;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "duration_ms")
    private Long durationMs;
    @Column(name = "last_run")
    private LocalDateTime lastRun;
    @Column(name = "row_index")
    private Integer rowIndex;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public TestCase getTestCase() {
        return testCase;
    }
    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }
    public Integer getStepNumber() {
        return stepNumber;
    }
    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getTestData() {
        return testData;
    }
    public void setTestData(String testData) {
        this.testData = testData;
    }
    public String getExpectedResult() {
        return expectedResult;
    }
    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }
    public String getActualResult() {
        return actualResult;
    }
    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }
    public Boolean getIsHome() {
        return isHome;
    }
    public void setIsHome(Boolean isHome) {
        this.isHome = isHome;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getScreenshotPath() {
        return screenshotPath;
    }
    public void setScreenshotPath(String screenshotPath) {
        this.screenshotPath = screenshotPath;
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
    }
    public Long getDurationMs() {
        return durationMs;
    }
    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
    public LocalDateTime getLastRun() {
        return lastRun;
    }
    public void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }
    public Integer getRowIndex() {
        return rowIndex;
    }
    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 
