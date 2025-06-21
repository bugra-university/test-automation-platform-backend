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
    private String sheetType;

    @Column(name = "sheet_index")
    private Integer sheetIndex;

    @Column(name = "row_count")
    private Integer rowCount = 0;

    @Column(name = "column_count")
    private Integer columnCount = 0;

    @Column(name = "parsed")
    private Boolean parsed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

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

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }

    public Boolean getParsed() {
        return parsed;
    }

    public void setParsed(Boolean parsed) {
        this.parsed = parsed;
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