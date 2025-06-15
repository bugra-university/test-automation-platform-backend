package com.project_team09.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseDTO {
    private Long id;
    private Long suiteId;
    private String name;
    private String description;
    private String className;
    private String methodName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalRuns;
    private Integer successCount;
    private Integer failureCount;

    // Excel'den gelen ek alanlar
    private String userStoryId;
    private String testCaseId;
    private String testObjective;
    private String preCondition;
    private List<TestStepDTO> steps;
    private String note; // BUG notları için
}
