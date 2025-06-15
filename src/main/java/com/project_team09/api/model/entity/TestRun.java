package com.project_team09.api.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "test_runs")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class TestRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status")
    private String status; // RUNNING, COMPLETED, FAILED, CANCELLED

    @Column(name = "triggered_by")
    private String triggeredBy; // scheduled, manual, git_push @Column(name = "environment")
    private String environment; // dev, test, prod

    @Column(name = "git_commit_hash")
    private String gitCommitHash;
    @Type(type = "jsonb")
    @Column(name = "parameters", columnDefinition = "jsonb")
    private Map<String, Object> parameters = new HashMap<>();
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "testRun", cascade = CascadeType.ALL)
    private List<TestResult> testResults = new ArrayList<>();

    @OneToMany(mappedBy = "testRun", cascade = CascadeType.ALL)
    private List<Report> reports = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getGitCommitHash() {
        return gitCommitHash;
    }

    public void setGitCommitHash(String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
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

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TestRun testRun = (TestRun) o;
        return Objects.equals(id, testRun.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    } // toString method

    @Override
    public String toString() {
        return "TestRun{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", project=" + (project != null ? project.getId() : null) +
                '}';
    }

    // Constructors
    public TestRun() {
    }

    public TestRun(Long id, String name, LocalDateTime startTime, LocalDateTime endTime, String status,
            String triggeredBy, String environment, String gitCommitHash, Map<String, Object> parameters,
            LocalDateTime createdAt, LocalDateTime updatedAt, Project project, List<TestResult> testResults,
            List<Report> reports) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.triggeredBy = triggeredBy;
        this.environment = environment;
        this.gitCommitHash = gitCommitHash;
        this.parameters = parameters;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.project = project;
        this.testResults = testResults;
        this.reports = reports;
    }
}
