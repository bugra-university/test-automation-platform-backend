package com.project_team09.api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_steps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_result_id")
    @JsonBackReference("testResult-steps")
    private TestResult testResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id")
    @JsonBackReference("testCase-steps")
    private TestCase testCase;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    private String status; // PASS, FAIL, SKIP, INFO

    // Excel'den gelen yeni alanlar
    @Column(name = "step_number")
    private Integer stepNumber;

    @Column(name = "step_description")
    private String stepDescription;

    @Column(name = "test_data")
    private String testData;

    @Column(name = "expected_result")
    private String expectedResult;

    @Column(name = "actual_result")
    private String actualResult;

    @Column(name = "is_highlighted")
    private Boolean isHighlighted;

    @Column(name = "is_home")
    private Boolean isHome;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "order_number")
    private Integer orderNumber;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    private List<Screenshot> screenshots = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
