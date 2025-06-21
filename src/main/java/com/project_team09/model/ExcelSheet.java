package com.project_team09.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "excel_sheets")
public class ExcelSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "excel_file_id", nullable = false)
    private ExcelFile excelFile;

    @Column(name = "sheet_name", nullable = false)
    private String sheetName;

    @Column(name = "sheet_type", nullable = false)
    private String sheetType; // BACKLOG or TEST_CASES

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "excelSheet", cascade = CascadeType.ALL)
    private List<ProductBacklogItem> backlogItems = new ArrayList<>();

    @OneToMany(mappedBy = "excelSheet", cascade = CascadeType.ALL)
    private List<TestCase> testCases = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExcelFile getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(ExcelFile excelFile) {
        this.excelFile = excelFile;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getSheetType() {
        return sheetType;
    }

    public void setSheetType(String sheetType) {
        this.sheetType = sheetType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProductBacklogItem> getBacklogItems() {
        return backlogItems;
    }

    public void setBacklogItems(List<ProductBacklogItem> backlogItems) {
        this.backlogItems = backlogItems;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
} 