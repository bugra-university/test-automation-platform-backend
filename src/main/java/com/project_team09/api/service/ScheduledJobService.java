package com.project_team09.api.service;

import com.project_team09.api.model.dto.ScheduledJobDTO;
import com.project_team09.api.model.entity.ScheduledJob;
import com.project_team09.api.model.entity.TestSuite;
import com.project_team09.api.repository.ScheduledJobRepository;
import com.project_team09.api.repository.TestSuiteRepository;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduledJobService {

    private final ScheduledJobRepository scheduledJobRepository;
    private final TestSuiteRepository testSuiteRepository;

    public ScheduledJobService(ScheduledJobRepository scheduledJobRepository, TestSuiteRepository testSuiteRepository) {
        this.scheduledJobRepository = scheduledJobRepository;
        this.testSuiteRepository = testSuiteRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduledJobDTO> getAllScheduledJobs() {
        return scheduledJobRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduledJobDTO getScheduledJobById(Long id) {
        return scheduledJobRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("ScheduledJob not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ScheduledJobDTO> getActiveScheduledJobs() {
        return scheduledJobRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduledJobDTO> getScheduledJobsByTestSuiteId(Long testSuiteId) {
        return scheduledJobRepository.findByTestSuiteId(testSuiteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduledJobDTO createScheduledJob(ScheduledJobDTO scheduledJobDTO) {
        ScheduledJob scheduledJob = convertToEntity(scheduledJobDTO);

        // Set next run time based on cron expression
        CronExpression cronExpression = CronExpression.parse(scheduledJob.getCronExpression());
        LocalDateTime now = LocalDateTime.now();
        scheduledJob.setNextRun(cronExpression.next(now));

        ScheduledJob savedScheduledJob = scheduledJobRepository.save(scheduledJob);
        return convertToDTO(savedScheduledJob);
    }

    @Transactional
    public ScheduledJobDTO updateScheduledJob(Long id, ScheduledJobDTO scheduledJobDTO) {
        if (!scheduledJobRepository.existsById(id)) {
            throw new EntityNotFoundException("ScheduledJob not found with id: " + id);
        }

        ScheduledJob scheduledJob = convertToEntity(scheduledJobDTO);
        scheduledJob.setId(id);

        // Update next run time if cron expression changed
        CronExpression cronExpression = CronExpression.parse(scheduledJob.getCronExpression());
        LocalDateTime now = LocalDateTime.now();
        scheduledJob.setNextRun(cronExpression.next(now));

        scheduledJob.setUpdatedAt(LocalDateTime.now());
        ScheduledJob updatedScheduledJob = scheduledJobRepository.save(scheduledJob);
        return convertToDTO(updatedScheduledJob);
    }

    @Transactional
    public void deleteScheduledJob(Long id) {
        if (!scheduledJobRepository.existsById(id)) {
            throw new EntityNotFoundException("ScheduledJob not found with id: " + id);
        }
        scheduledJobRepository.deleteById(id);
    }

    @Transactional
    public ScheduledJobDTO toggleJobStatus(Long id, boolean isActive) {
        ScheduledJob scheduledJob = scheduledJobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ScheduledJob not found with id: " + id));

        scheduledJob.setIsActive(isActive);
        scheduledJob.setUpdatedAt(LocalDateTime.now());

        // If activating, update next run time
        if (isActive) {
            CronExpression cronExpression = CronExpression.parse(scheduledJob.getCronExpression());
            LocalDateTime now = LocalDateTime.now();
            scheduledJob.setNextRun(cronExpression.next(now));
        }

        ScheduledJob updatedJob = scheduledJobRepository.save(scheduledJob);
        return convertToDTO(updatedJob);
    }

    @Transactional(readOnly = true)
    public List<ScheduledJobDTO> getJobsDueForExecution() {
        LocalDateTime now = LocalDateTime.now();
        return scheduledJobRepository.findByNextRunBeforeAndIsActiveTrue(now).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateJobExecutionTime(Long id, LocalDateTime executionTime) {
        ScheduledJob scheduledJob = scheduledJobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ScheduledJob not found with id: " + id));

        scheduledJob.setLastRun(executionTime);

        // Calculate next run time
        CronExpression cronExpression = CronExpression.parse(scheduledJob.getCronExpression());
        scheduledJob.setNextRun(cronExpression.next(executionTime));

        scheduledJobRepository.save(scheduledJob);
    }

    private ScheduledJobDTO convertToDTO(ScheduledJob scheduledJob) {
        ScheduledJobDTO dto = new ScheduledJobDTO();
        dto.setId(scheduledJob.getId());
        dto.setName(scheduledJob.getName());
        dto.setDescription(scheduledJob.getDescription());
        dto.setCronExpression(scheduledJob.getCronExpression());
        dto.setParameters(scheduledJob.getParameters());
        dto.setIsActive(scheduledJob.getIsActive());
        dto.setCreatedAt(scheduledJob.getCreatedAt());
        dto.setUpdatedAt(scheduledJob.getUpdatedAt());
        dto.setLastRun(scheduledJob.getLastRun());
        dto.setNextRun(scheduledJob.getNextRun());

        if (scheduledJob.getTestSuite() != null) {
            dto.setTestSuiteId(scheduledJob.getTestSuite().getId());
            dto.setTestSuiteName(scheduledJob.getTestSuite().getName());

            if (scheduledJob.getTestSuite().getProject() != null) {
                dto.setProjectId(scheduledJob.getTestSuite().getProject().getId());
                dto.setProjectName(scheduledJob.getTestSuite().getProject().getName());
            }
        }

        return dto;
    }

    private ScheduledJob convertToEntity(ScheduledJobDTO dto) {
        ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setId(dto.getId());
        scheduledJob.setName(dto.getName());
        scheduledJob.setDescription(dto.getDescription());
        scheduledJob.setCronExpression(dto.getCronExpression());
        scheduledJob.setParameters(dto.getParameters());
        scheduledJob.setIsActive(dto.getIsActive());

        if (dto.getCreatedAt() != null) {
            scheduledJob.setCreatedAt(dto.getCreatedAt());
        } else if (scheduledJob.getId() == null) {
            scheduledJob.setCreatedAt(LocalDateTime.now());
        }

        scheduledJob.setUpdatedAt(LocalDateTime.now());
        scheduledJob.setLastRun(dto.getLastRun());
        scheduledJob.setNextRun(dto.getNextRun());

        if (dto.getTestSuiteId() != null) {
            TestSuite testSuite = testSuiteRepository.findById(dto.getTestSuiteId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("TestSuite not found with id: " + dto.getTestSuiteId()));
            scheduledJob.setTestSuite(testSuite);
        }

        return scheduledJob;
    }
}
