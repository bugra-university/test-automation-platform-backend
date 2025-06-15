package com.project_team09.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "The project name cannot be empty.")
    private String name;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer testSuiteCount;
    private Integer testCaseCount;
}
