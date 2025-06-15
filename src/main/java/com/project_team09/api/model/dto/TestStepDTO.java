package com.project_team09.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestStepDTO {
    private Long id;
    private Integer stepNumber;
    private String stepDescription;
    private String testData;
    private String expectedResult;
    private String actualResult;
    private LocalDateTime lastRun;
    private Boolean isHighlighted;
    private Boolean isHome;
    private String status;
}
