package com.project_team09.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {
    private Long id;

    @NotNull(message = "The test run ID cannot be empty.")
    private Long testRunId;

    @NotNull(message = "The test case ID cannot be empty.")
    private Long testCaseId;

    @NotBlank(message = "The status field cannot be empty.")
    private String status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
    private String errorMessage;
    private String stackTrace;
    private LocalDateTime createdAt;

    // Related data
    private String testCaseName;
    private String className;
    private String methodName;
    private Integer stepCount;
    private Integer screenshotCount;
}
