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
public class TestSuiteDTO {
    private Long id;

    @NotNull(message = "The project ID cannot be empty.")
    private Long projectId;

    @NotBlank(message = "The test suite name cannot be empty.")
    private String name;

    private String description;
    private String folderPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer testCaseCount;
}
