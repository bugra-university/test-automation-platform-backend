package com.project_team09.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelTestStepDTO {
    private Integer stepNumber; // No
    private String stepDescription; // Steps
    private String testData; // Test Data
    private String expectedResult; // Expected Result
    private String actualResult; // Actual Result - testin gerçek sonucu
    private LocalDateTime lastRun; // Son çalıştırılma zamanı
    private Boolean isHighlighted; // Sarı renkli satırlar için
    private Boolean isHome; // Home (yeşil hücre)
}
