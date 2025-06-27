package com.project_team09.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "test_schedules")
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
    @TypeDef(name = "string-array", typeClass = StringArrayType.class)
})
public class TestSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "title")
    private String title;

    @Column(name = "user_story_id", nullable = false, length = 50)
    private String userStoryId;

    @Type(type = "string-array")
    @Column(name = "test_case_ids", columnDefinition = "text[]")
    private String[] testCaseIds;

    @Column(name = "start_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endTime;

    @Column(name = "schedule_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType = ScheduleType.ONCE;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "next_run_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime nextRunTime;

    @Column(name = "last_run_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime lastRunTime;

    @Column(name = "last_test_run_id")
    private Long lastTestRunId;

    @Type(type = "jsonb")
    @Column(name = "repeat_settings", columnDefinition = "jsonb")
    private Map<String, Object> repeatSettings;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    // Enums
    public enum ScheduleType {
        ONCE, DAILY, WEEKLY, MONTHLY
    }

    public enum ScheduleStatus {
        SCHEDULED, RUNNING, COMPLETED, FAILED, PAUSED, CANCELLED
    }

    // Constructors
    public TestSchedule() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public TestSchedule(Long projectId, String userStoryId, String[] testCaseIds, 
                       LocalDateTime startTime, LocalDateTime endTime, ScheduleType scheduleType) {
        this();
        this.projectId = projectId;
        this.userStoryId = userStoryId;
        this.testCaseIds = testCaseIds;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduleType = scheduleType;
        this.nextRunTime = startTime; // İlk çalışma zamanı
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    public String[] getTestCaseIds() {
        return testCaseIds;
    }

    public void setTestCaseIds(String[] testCaseIds) {
        this.testCaseIds = testCaseIds;
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

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getNextRunTime() {
        return nextRunTime;
    }

    public void setNextRunTime(LocalDateTime nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public LocalDateTime getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(LocalDateTime lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public Long getLastTestRunId() {
        return lastTestRunId;
    }

    public void setLastTestRunId(Long lastTestRunId) {
        this.lastTestRunId = lastTestRunId;
    }

    public Map<String, Object> getRepeatSettings() {
        return repeatSettings;
    }

    public void setRepeatSettings(Map<String, Object> repeatSettings) {
        this.repeatSettings = repeatSettings;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public boolean isRecurring() {
        return scheduleType != ScheduleType.ONCE;
    }

    public boolean isActive() {
        return status == ScheduleStatus.SCHEDULED || status == ScheduleStatus.RUNNING;
    }

    public boolean shouldRun() {
        return status == ScheduleStatus.SCHEDULED && 
               nextRunTime != null && 
               LocalDateTime.now().isAfter(nextRunTime);
    }

    @Override
    public String toString() {
        return "TestSchedule{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", title='" + title + '\'' +
                ", userStoryId='" + userStoryId + '\'' +
                ", scheduleType=" + scheduleType +
                ", status=" + status +
                ", nextRunTime=" + nextRunTime +
                '}';
    }
} 