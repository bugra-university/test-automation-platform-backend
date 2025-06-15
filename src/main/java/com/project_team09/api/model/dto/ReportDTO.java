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
public class ReportDTO {
    private Long id;

    @NotNull(message = "The test run ID cannot be empty.")
    private Long testRunId;

    @NotBlank(message = "The report type cannot be empty.")
    private String reportType;

    private String filePath;

    @NotBlank(message = "The file name cannot be empty.")
    private String fileName;

    private LocalDateTime generatedAt;

    // Additional fields for front-end display
    private String testRunName;
    private Long projectId;
    private String projectName;
}
