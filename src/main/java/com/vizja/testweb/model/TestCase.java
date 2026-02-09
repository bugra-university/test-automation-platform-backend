package com.vizja.testweb.model;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "test_cases")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @ManyToOne
    @JoinColumn(name = "excel_sheet_id", nullable = false)
    private ExcelSheet excelSheet;
    @Column(name = "user_story_id", length = 50)
    private String userStoryId;
    @Column(name = "test_case_id", nullable = false, length = 50)
    private String testCaseId;
    @Column(name = "test_objective")
    private String objective;
    @Column(name = "pre_condition")
    private String preCondition;
    @Column(name = "test_data")
    private String testData;
    @Column(name = "expected_result")
    private String expectedResult;
    @Column(name = "row_index")
    private Integer rowIndex;
    @OneToMany(
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        mappedBy = "testCase"
    )
    private List<TestStep> testSteps = new ArrayList<>();
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public void addTestStep(TestStep testStep) {
        testSteps.add(testStep);
        testStep.setTestCase(this);
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }
    public ExcelSheet getExcelSheet() {
        return excelSheet;
    }
    public void setExcelSheet(ExcelSheet excelSheet) {
        this.excelSheet = excelSheet;
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
    public String getObjective() {
        return objective;
    }
    public void setObjective(String objective) {
        this.objective = objective;
    }
    public String getPreCondition() {
        return preCondition;
    }
    public void setPreCondition(String preCondition) {
        this.preCondition = preCondition;
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
    public Integer getRowIndex() {
        return rowIndex;
    }
    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }
    public List<TestStep> getTestSteps() {
        return testSteps;
    }
    public void setTestSteps(List<TestStep> testSteps) {
        this.testSteps = testSteps;
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
} 
