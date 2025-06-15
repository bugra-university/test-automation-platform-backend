package com.project_team09.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestRunDTO {
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @NotBlank(message = "The status field cannot be empty.")
    private String status;

    private String triggeredBy;
    private String environment;
    private String gitCommitHash;
    private Map<String, Object> parameters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
    private String projectName;
    private Integer totalTestCases;
    private Integer passedTests;
    private Integer failedTests;
    private Integer skippedTests;
}
