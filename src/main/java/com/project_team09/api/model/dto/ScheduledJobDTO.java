package com.project_team09.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledJobDTO {
    private Long id;

    @NotBlank(message = "The job name cannot be empty.")
    private String name;

    private String description;

    @NotBlank(message = "The cron expression cannot be empty.")
    private String cronExpression;

    private Long testSuiteId;

    private Map<String, Object> parameters = new HashMap<>();

    private Boolean isActive = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastRun;

    private LocalDateTime nextRun;

    // Additional fields for front-end display
    private String testSuiteName;
    private Long projectId;
    private String projectName;
}
