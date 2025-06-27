package com.project_team09.controller;

import com.project_team09.model.TestSchedule;
import com.project_team09.service.TestScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects/{projectId}/schedules")
@CrossOrigin(origins = "*")
public class TestScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(TestScheduleController.class);

    @Autowired
    private TestScheduleService testScheduleService;

    /**
     * Create a new test schedule
     */
    @PostMapping
    public ResponseEntity<?> createSchedule(
            @PathVariable Long projectId,
            @RequestBody TestSchedule schedule) {
        try {
            logger.info("Creating schedule for project: {}", projectId);
            
            schedule.setProjectId(projectId);
            TestSchedule createdSchedule = testScheduleService.createSchedule(schedule);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Schedule created successfully",
                "schedule", createdSchedule
            ));
        } catch (Exception e) {
            logger.error("Error creating schedule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to create schedule: " + e.getMessage()
                ));
        }
    }

    /**
     * Get all schedules for a project
     */
    @GetMapping
    public ResponseEntity<?> getSchedulesByProject(@PathVariable Long projectId) {
        try {
            logger.info("Getting schedules for project: {}", projectId);
            
            List<TestSchedule> schedules = testScheduleService.getSchedulesByProject(projectId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "schedules", schedules
            ));
        } catch (Exception e) {
            logger.error("Error getting schedules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to get schedules: " + e.getMessage()
                ));
        }
    }

    /**
     * Get schedules in date range (for calendar view)
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> getSchedulesInDateRange(
            @PathVariable Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            logger.info("Getting schedules for project: {} between {} and {}", projectId, startDate, endDate);
            
            List<TestSchedule> schedules = testScheduleService.getSchedulesInDateRange(projectId, startDate, endDate);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "schedules", schedules
            ));
        } catch (Exception e) {
            logger.error("Error getting schedules in date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to get schedules: " + e.getMessage()
                ));
        }
    }

    /**
     * Get a specific schedule by ID
     */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getScheduleById(
            @PathVariable Long projectId,
            @PathVariable Long scheduleId) {
        try {
            logger.info("Getting schedule {} for project: {}", scheduleId, projectId);
            
            TestSchedule schedule = testScheduleService.getScheduleById(scheduleId);
            
            // Verify the schedule belongs to the project
            if (!schedule.getProjectId().equals(projectId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "success", false,
                        "message", "Schedule does not belong to this project"
                    ));
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "schedule", schedule
            ));
        } catch (RuntimeException e) {
            logger.error("Schedule not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error getting schedule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to get schedule: " + e.getMessage()
                ));
        }
    }

    /**
     * Update a schedule
     */
    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long projectId,
            @PathVariable Long scheduleId,
            @RequestBody TestSchedule updatedSchedule) {
        try {
            logger.info("Updating schedule {} for project: {}", scheduleId, projectId);
            
            updatedSchedule.setProjectId(projectId);
            TestSchedule schedule = testScheduleService.updateSchedule(scheduleId, updatedSchedule);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Schedule updated successfully",
                "schedule", schedule
            ));
        } catch (RuntimeException e) {
            logger.error("Schedule not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error updating schedule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to update schedule: " + e.getMessage()
                ));
        }
    }

    /**
     * Delete a schedule
     */
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long projectId,
            @PathVariable Long scheduleId) {
        try {
            logger.info("Deleting schedule {} for project: {}", scheduleId, projectId);
            
            testScheduleService.deleteSchedule(scheduleId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Schedule deleted successfully"
            ));
        } catch (RuntimeException e) {
            logger.error("Schedule not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error deleting schedule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to delete schedule: " + e.getMessage()
                ));
        }
    }

    /**
     * Run a schedule manually
     */
    @PostMapping("/{scheduleId}/run")
    public ResponseEntity<?> runScheduleNow(
            @PathVariable Long projectId,
            @PathVariable Long scheduleId) {
        try {
            logger.info("Running schedule {} manually for project: {}", scheduleId, projectId);
            
            testScheduleService.runScheduleNow(scheduleId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Schedule execution started"
            ));
        } catch (RuntimeException e) {
            logger.error("Error running schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error running schedule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to run schedule: " + e.getMessage()
                ));
        }
    }

    /**
     * Toggle schedule status (pause/resume)
     */
    @PostMapping("/{scheduleId}/toggle")
    public ResponseEntity<?> toggleScheduleStatus(
            @PathVariable Long projectId,
            @PathVariable Long scheduleId) {
        try {
            logger.info("Toggling schedule {} status for project: {}", scheduleId, projectId);
            
            TestSchedule schedule = testScheduleService.toggleScheduleStatus(scheduleId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Schedule status updated",
                "schedule", schedule
            ));
        } catch (RuntimeException e) {
            logger.error("Schedule not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error toggling schedule status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to toggle schedule status: " + e.getMessage()
                ));
        }
    }

    /**
     * Get active schedules
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveSchedules(@PathVariable Long projectId) {
        try {
            logger.info("Getting active schedules for project: {}", projectId);
            
            List<TestSchedule> schedules = testScheduleService.getActiveSchedules(projectId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "schedules", schedules
            ));
        } catch (Exception e) {
            logger.error("Error getting active schedules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to get active schedules: " + e.getMessage()
                ));
        }
    }

    /**
     * Get schedule statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getScheduleStats(@PathVariable Long projectId) {
        try {
            logger.info("Getting schedule statistics for project: {}", projectId);
            
            Map<String, Object> stats = testScheduleService.getScheduleStats(projectId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", stats
            ));
        } catch (Exception e) {
            logger.error("Error getting schedule statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to get schedule statistics: " + e.getMessage()
                ));
        }
    }

    /**
     * Get upcoming schedules
     */
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingSchedules(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Getting upcoming schedules for project: {}", projectId);
            
            List<TestSchedule> schedules = testScheduleService.getUpcomingSchedules(projectId, limit);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "schedules", schedules
            ));
        } catch (Exception e) {
            logger.error("Error getting upcoming schedules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to get upcoming schedules: " + e.getMessage()
                ));
        }
    }

    /**
     * Search schedules
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchSchedules(
            @PathVariable Long projectId,
            @RequestParam String query) {
        try {
            logger.info("Searching schedules for project: {} with query: {}", projectId, query);
            
            List<TestSchedule> schedules = testScheduleService.searchSchedules(projectId, query);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "schedules", schedules
            ));
        } catch (Exception e) {
            logger.error("Error searching schedules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to search schedules: " + e.getMessage()
                ));
        }
    }
} 