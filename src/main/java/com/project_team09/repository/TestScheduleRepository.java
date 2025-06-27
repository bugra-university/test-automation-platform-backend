package com.project_team09.repository;

import com.project_team09.model.TestSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestScheduleRepository extends JpaRepository<TestSchedule, Long> {

    // Project bazlı schedule'ları getir
    List<TestSchedule> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    // Project ve status bazlı schedule'ları getir
    List<TestSchedule> findByProjectIdAndStatusOrderByCreatedAtDesc(Long projectId, TestSchedule.ScheduleStatus status);

    // Status bazlı schedule'ları getir (debug için)
    List<TestSchedule> findByStatus(TestSchedule.ScheduleStatus status);

    // User story bazlı schedule'ları getir
    List<TestSchedule> findByProjectIdAndUserStoryIdOrderByCreatedAtDesc(Long projectId, String userStoryId);

    // Çalışması gereken schedule'ları getir (şu an çalışma zamanı geçmiş ve SCHEDULED durumunda olan)
    @Query("SELECT s FROM TestSchedule s WHERE s.status = 'SCHEDULED' AND s.nextRunTime <= :currentTime")
    List<TestSchedule> findSchedulesToRun(@Param("currentTime") LocalDateTime currentTime);

    // Aktif schedule'ları getir (SCHEDULED veya RUNNING durumunda olan)
    @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND s.status IN ('SCHEDULED', 'RUNNING')")
    List<TestSchedule> findActiveSchedulesByProject(@Param("projectId") Long projectId);

    // Belirli zaman aralığındaki schedule'ları getir (Calendar view için)
    @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND " +
           "((s.startTime >= :startDate AND s.startTime <= :endDate) OR " +
           "(s.endTime >= :startDate AND s.endTime <= :endDate) OR " +
           "(s.startTime <= :startDate AND s.endTime >= :endDate))")
    List<TestSchedule> findSchedulesInDateRange(
        @Param("projectId") Long projectId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // Recurring schedule'ları getir (daily, weekly, monthly)
    @Query("SELECT s FROM TestSchedule s WHERE s.scheduleType != 'ONCE' AND s.status IN ('SCHEDULED', 'RUNNING')")
    List<TestSchedule> findRecurringSchedules();

    // Son çalışan schedule'ları getir (status history için)
    @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND s.lastRunTime IS NOT NULL ORDER BY s.lastRunTime DESC")
    List<TestSchedule> findRecentlyRunSchedules(@Param("projectId") Long projectId);

    // Schedule türüne göre getir
    List<TestSchedule> findByProjectIdAndScheduleTypeOrderByCreatedAtDesc(Long projectId, TestSchedule.ScheduleType scheduleType);

    // Başlık ile arama (case insensitive)
    @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND " +
           "(LOWER(s.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(s.userStoryId) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<TestSchedule> searchSchedules(@Param("projectId") Long projectId, @Param("searchText") String searchText);

    // Belirli bir test run'a ait schedule'ı getir
    TestSchedule findByLastTestRunId(Long testRunId);

    // Bugün çalışacak schedule'ları getir
    @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND " +
           "DATE(s.nextRunTime) = DATE(:date) AND s.status = 'SCHEDULED'")
    List<TestSchedule> findSchedulesForToday(@Param("projectId") Long projectId, @Param("date") LocalDateTime date);

    // Gelecekteki schedule'ları getir
    @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND " +
           "s.nextRunTime > :currentTime AND s.status = 'SCHEDULED' ORDER BY s.nextRunTime ASC")
    List<TestSchedule> findUpcomingSchedules(@Param("projectId") Long projectId, @Param("currentTime") LocalDateTime currentTime);

    // Schedule istatistikleri için count'lar
    @Query("SELECT COUNT(s) FROM TestSchedule s WHERE s.projectId = :projectId AND s.status = :status")
    Long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TestSchedule.ScheduleStatus status);

    // Başarısız schedule'ları getir
    @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND s.status = 'FAILED' ORDER BY s.updatedAt DESC")
    List<TestSchedule> findFailedSchedules(@Param("projectId") Long projectId);
} 