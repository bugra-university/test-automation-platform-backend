package com.project_team09.service;

import com.project_team09.model.TestSchedule;
import com.project_team09.repository.TestScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class TestScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(TestScheduleService.class);

    @Autowired
    private TestScheduleRepository testScheduleRepository;

    @Autowired
    private TestSuitesService testSuitesService;

    @Autowired
    private TestExecutionService testExecutionService;

    // Schedule oluştur
    public TestSchedule createSchedule(TestSchedule schedule) {
        logger.info("Creating new test schedule for project: {}, userStory: {}", 
                   schedule.getProjectId(), schedule.getUserStoryId());
        
        // İlk çalışma zamanını ayarla
        if (schedule.getNextRunTime() == null) {
            schedule.setNextRunTime(schedule.getStartTime());
        }
        
        // Başlık yoksa user story'den oluştur
        if (schedule.getTitle() == null || schedule.getTitle().trim().isEmpty()) {
            schedule.setTitle(schedule.getUserStoryId() + " - Test Schedule");
        }
        
        logger.info("Schedule details before save - StartTime: {}, NextRunTime: {}, Status: {}", 
                   schedule.getStartTime(), schedule.getNextRunTime(), schedule.getStatus());
        
        TestSchedule savedSchedule = testScheduleRepository.save(schedule);
        logger.info("Test schedule created with ID: {}, NextRunTime: {}, Status: {}", 
                   savedSchedule.getId(), savedSchedule.getNextRunTime(), savedSchedule.getStatus());
        
        return savedSchedule;
    }

    // Schedule güncelle
    public TestSchedule updateSchedule(Long scheduleId, TestSchedule updatedSchedule) {
        logger.info("Updating test schedule with ID: {}", scheduleId);
        
        TestSchedule existingSchedule = testScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Test schedule not found with ID: " + scheduleId));
        
        // Güncellenebilir alanları ayarla
        existingSchedule.setTitle(updatedSchedule.getTitle());
        existingSchedule.setUserStoryId(updatedSchedule.getUserStoryId());
        existingSchedule.setTestCaseIds(updatedSchedule.getTestCaseIds());
        existingSchedule.setStartTime(updatedSchedule.getStartTime());
        existingSchedule.setEndTime(updatedSchedule.getEndTime());
        existingSchedule.setScheduleType(updatedSchedule.getScheduleType());
        existingSchedule.setStatus(updatedSchedule.getStatus());
        existingSchedule.setDescription(updatedSchedule.getDescription());
        existingSchedule.setRepeatSettings(updatedSchedule.getRepeatSettings());
        
        // Eğer zamanlama değiştiyse next run time'ı güncelle
        if (!existingSchedule.getStartTime().equals(updatedSchedule.getStartTime()) ||
            !existingSchedule.getScheduleType().equals(updatedSchedule.getScheduleType())) {
            calculateNextRunTime(existingSchedule);
        }
        
        TestSchedule savedSchedule = testScheduleRepository.save(existingSchedule);
        logger.info("Test schedule updated with ID: {}", savedSchedule.getId());
        
        return savedSchedule;
    }

    // Schedule sil
    public void deleteSchedule(Long scheduleId) {
        logger.info("Deleting test schedule with ID: {}", scheduleId);
        
        TestSchedule schedule = testScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Test schedule not found with ID: " + scheduleId));
        
        // Eğer çalışıyorsa iptal et
        if (schedule.getStatus() == TestSchedule.ScheduleStatus.RUNNING) {
            schedule.setStatus(TestSchedule.ScheduleStatus.CANCELLED);
            testScheduleRepository.save(schedule);
        } else {
            testScheduleRepository.delete(schedule);
        }
        
        logger.info("Test schedule deleted with ID: {}", scheduleId);
    }

    // Project'e ait tüm schedule'ları getir
    public List<TestSchedule> getSchedulesByProject(Long projectId) {
        return testScheduleRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    // Schedule'ı ID ile getir
    public TestSchedule getScheduleById(Long scheduleId) {
        return testScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Test schedule not found with ID: " + scheduleId));
    }

    // Belirli tarih aralığındaki schedule'ları getir (Calendar için)
    public List<TestSchedule> getSchedulesInDateRange(Long projectId, LocalDateTime startDate, LocalDateTime endDate) {
        return testScheduleRepository.findSchedulesInDateRange(projectId, startDate, endDate);
    }

    // Aktif schedule'ları getir
    public List<TestSchedule> getActiveSchedules(Long projectId) {
        return testScheduleRepository.findActiveSchedulesByProject(projectId);
    }

    // Schedule'ı manuel çalıştır
    @Transactional
    public void runScheduleNow(Long scheduleId) {
        logger.info("Running schedule manually: {}", scheduleId);
        
        TestSchedule schedule = getScheduleById(scheduleId);
        
        if (schedule.getStatus() == TestSchedule.ScheduleStatus.RUNNING) {
            throw new RuntimeException("Schedule is already running");
        }
        
        executeSchedule(schedule);
    }

    // Schedule'ı duraklat/devam ettir
    @Transactional
    public TestSchedule toggleScheduleStatus(Long scheduleId) {
        TestSchedule schedule = getScheduleById(scheduleId);
        
        if (schedule.getStatus() == TestSchedule.ScheduleStatus.SCHEDULED) {
            schedule.setStatus(TestSchedule.ScheduleStatus.PAUSED);
            logger.info("Schedule paused: {}", scheduleId);
        } else if (schedule.getStatus() == TestSchedule.ScheduleStatus.PAUSED) {
            schedule.setStatus(TestSchedule.ScheduleStatus.SCHEDULED);
            calculateNextRunTime(schedule);
            logger.info("Schedule resumed: {}", scheduleId);
        }
        
        return testScheduleRepository.save(schedule);
    }

    // Çalışması gereken schedule'ları kontrol et (Scheduled task - her dakika)
    @Scheduled(fixedRate = 60000) // Her 60 saniyede bir kontrol et
    public void checkAndRunSchedules() {
        LocalDateTime currentTime = LocalDateTime.now();
        logger.info("Checking schedules at: {}", currentTime);
        
        // Debug: Tüm SCHEDULED durumundaki schedule'ları listele
        List<TestSchedule> allScheduledSchedules = testScheduleRepository.findByStatus(TestSchedule.ScheduleStatus.SCHEDULED);
        logger.info("Total SCHEDULED schedules in database: {}", allScheduledSchedules.size());
        
        for (TestSchedule schedule : allScheduledSchedules) {
            logger.info("Schedule ID: {}, NextRunTime: {}, Status: {}, ShouldRun: {}", 
                       schedule.getId(), 
                       schedule.getNextRunTime(), 
                       schedule.getStatus(),
                       schedule.getNextRunTime() != null && currentTime.isAfter(schedule.getNextRunTime()));
        }
        
        List<TestSchedule> schedulesToRun = testScheduleRepository.findSchedulesToRun(currentTime);
        
        logger.info("Found {} schedules to run", schedulesToRun.size());
        
        for (TestSchedule schedule : schedulesToRun) {
            try {
                executeSchedule(schedule);
            } catch (Exception e) {
                logger.error("Error executing schedule {}: {}", schedule.getId(), e.getMessage(), e);
                // Schedule'ı failed durumuna getir
                schedule.setStatus(TestSchedule.ScheduleStatus.FAILED);
                testScheduleRepository.save(schedule);
            }
        }
    }

    // Schedule'ı çalıştır
    @Transactional
    private void executeSchedule(TestSchedule schedule) {
        logger.info("Executing schedule: {} for user story: {}", schedule.getId(), schedule.getUserStoryId());
        
        try {
            // Status'u RUNNING yap
            schedule.setStatus(TestSchedule.ScheduleStatus.RUNNING);
            schedule.setLastRunTime(LocalDateTime.now());
            testScheduleRepository.save(schedule);
            
            // Test suite'ı gerçek TestExecutionService ile çalıştır (headless mode)
            logger.info("Starting real test execution for schedule: {}", schedule.getId());
            logger.info("Calling TestExecutionService.executeTestSuiteAsync with projectId={}, userStoryId={}", 
                       schedule.getProjectId(), schedule.getUserStoryId());
            
            if (testExecutionService == null) {
                logger.error("❌ testExecutionService is NULL! Autowiring failed!");
                throw new RuntimeException("TestExecutionService not available");
            }
            
            logger.info("✅ testExecutionService is not null, calling async method...");
            testExecutionService.executeTestSuiteAsync(
                schedule.getProjectId(), 
                schedule.getUserStoryId(),
                false, // headless = false for debugging (Chrome will open)
                "chrome" // default browser
            ).thenAccept(result -> {
                logger.info("Test execution async callback received for schedule: {}", schedule.getId());
                // Async completion callback
                try {
                    TestSchedule updatedSchedule = testScheduleRepository.findById(schedule.getId()).orElse(null);
                    if (updatedSchedule != null) {
                        String status = (String) result.get("status");
                        if ("COMPLETED".equals(status) || "PASSED".equals(status)) {
                            updatedSchedule.setStatus(TestSchedule.ScheduleStatus.COMPLETED);
                            logger.info("Schedule {} completed successfully (async)", schedule.getId());
                        } else {
                            updatedSchedule.setStatus(TestSchedule.ScheduleStatus.FAILED);
                            logger.warn("Schedule {} failed (async) with result: {}", schedule.getId(), result);
                        }
                        
                        // Recurring schedule handling
                        if (updatedSchedule.isRecurring()) {
                            calculateNextRunTime(updatedSchedule);
                            updatedSchedule.setStatus(TestSchedule.ScheduleStatus.SCHEDULED);
                        }
                        
                        testScheduleRepository.save(updatedSchedule);
                    }
                } catch (Exception e) {
                    logger.error("Error in async completion callback for schedule {}: {}", schedule.getId(), e.getMessage());
                }
            }).exceptionally(throwable -> {
                // Async error callback
                try {
                    TestSchedule updatedSchedule = testScheduleRepository.findById(schedule.getId()).orElse(null);
                    if (updatedSchedule != null) {
                        updatedSchedule.setStatus(TestSchedule.ScheduleStatus.FAILED);
                        testScheduleRepository.save(updatedSchedule);
                    }
                    logger.error("Schedule {} failed with exception: {}", schedule.getId(), throwable.getMessage());
                } catch (Exception e) {
                    logger.error("Error in async error callback for schedule {}: {}", schedule.getId(), e.getMessage());
                }
                return null;
            });
            
            // For immediate status, mark as RUNNING and let async callbacks handle completion
            schedule.setStatus(TestSchedule.ScheduleStatus.RUNNING);
            logger.info("Schedule {} started successfully, execution in progress", schedule.getId());
            
            // Note: Recurring schedule logic is now handled in async callbacks
            
        } catch (Exception e) {
            schedule.setStatus(TestSchedule.ScheduleStatus.FAILED);
            logger.error("Error executing schedule {}: {}", schedule.getId(), e.getMessage(), e);
            throw e;
        } finally {
            testScheduleRepository.save(schedule);
        }
    }

    // Bir sonraki çalışma zamanını hesapla
    private void calculateNextRunTime(TestSchedule schedule) {
        if (!schedule.isRecurring()) {
            schedule.setNextRunTime(null);
            return;
        }
        
        LocalDateTime baseTime = schedule.getLastRunTime() != null ? 
                                schedule.getLastRunTime() : schedule.getStartTime();
        
        LocalDateTime nextRun;
        
        switch (schedule.getScheduleType()) {
            case DAILY:
                nextRun = baseTime.plusDays(1);
                break;
            case WEEKLY:
                nextRun = baseTime.plusWeeks(1);
                break;
            case MONTHLY:
                nextRun = baseTime.plusMonths(1);
                break;
            default:
                nextRun = null;
                break;
        }
        
        schedule.setNextRunTime(nextRun);
        logger.info("Next run time for schedule {} calculated as: {}", schedule.getId(), nextRun);
    }

    // Schedule istatistikleri
    public Map<String, Object> getScheduleStats(Long projectId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total", testScheduleRepository.countByProjectIdAndStatus(projectId, null));
        stats.put("scheduled", testScheduleRepository.countByProjectIdAndStatus(projectId, TestSchedule.ScheduleStatus.SCHEDULED));
        stats.put("running", testScheduleRepository.countByProjectIdAndStatus(projectId, TestSchedule.ScheduleStatus.RUNNING));
        stats.put("completed", testScheduleRepository.countByProjectIdAndStatus(projectId, TestSchedule.ScheduleStatus.COMPLETED));
        stats.put("failed", testScheduleRepository.countByProjectIdAndStatus(projectId, TestSchedule.ScheduleStatus.FAILED));
        stats.put("paused", testScheduleRepository.countByProjectIdAndStatus(projectId, TestSchedule.ScheduleStatus.PAUSED));
        
        return stats;
    }

    // Upcoming schedules
    public List<TestSchedule> getUpcomingSchedules(Long projectId, int limit) {
        List<TestSchedule> upcoming = testScheduleRepository.findUpcomingSchedules(projectId, LocalDateTime.now());
        return upcoming.size() > limit ? upcoming.subList(0, limit) : upcoming;
    }

    // Search schedules
    public List<TestSchedule> searchSchedules(Long projectId, String searchText) {
        return testScheduleRepository.searchSchedules(projectId, searchText);
    }
} 