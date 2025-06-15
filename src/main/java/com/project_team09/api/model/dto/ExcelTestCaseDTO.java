package com.project_team09.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelTestCaseDTO {
    private String userStoryId; // US01
    private String testCaseId; // TC01
    private String testObjective; // "Sign up when all areas are filled"
    private String preCondition; // "Access to the Site"
    private List<ExcelTestStepDTO> steps;
    private String note; // BUG notları için
}
