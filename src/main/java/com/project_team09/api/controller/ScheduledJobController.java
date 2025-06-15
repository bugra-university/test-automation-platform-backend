package com.project_team09.api.controller;

import com.project_team09.api.model.dto.ScheduledJobDTO;
import com.project_team09.api.service.ScheduledJobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/scheduled-jobs")
public class ScheduledJobController {

    private final ScheduledJobService scheduledJobService;

    public ScheduledJobController(ScheduledJobService scheduledJobService) {
        this.scheduledJobService = scheduledJobService;
    }

    @GetMapping
    public ResponseEntity<List<ScheduledJobDTO>> getAllScheduledJobs() {
        return ResponseEntity.ok(scheduledJobService.getAllScheduledJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduledJobDTO> getScheduledJobById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduledJobService.getScheduledJobById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ScheduledJobDTO>> getActiveScheduledJobs() {
        return ResponseEntity.ok(scheduledJobService.getActiveScheduledJobs());
    }

    @PostMapping
    public ResponseEntity<ScheduledJobDTO> createScheduledJob(@Valid @RequestBody ScheduledJobDTO scheduledJobDTO) {
        ScheduledJobDTO createdScheduledJob = scheduledJobService.createScheduledJob(scheduledJobDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdScheduledJob);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduledJobDTO> updateScheduledJob(@PathVariable Long id,
            @Valid @RequestBody ScheduledJobDTO scheduledJobDTO) {
        return ResponseEntity.ok(scheduledJobService.updateScheduledJob(id, scheduledJobDTO));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ScheduledJobDTO> activateScheduledJob(@PathVariable Long id) {
        return ResponseEntity.ok(scheduledJobService.toggleJobStatus(id, true));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ScheduledJobDTO> deactivateScheduledJob(@PathVariable Long id) {
        return ResponseEntity.ok(scheduledJobService.toggleJobStatus(id, false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduledJob(@PathVariable Long id) {
        scheduledJobService.deleteScheduledJob(id);
        return ResponseEntity.noContent().build();
    }
}
