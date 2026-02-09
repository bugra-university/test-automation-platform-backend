package com.vizja.testweb.repository;

import com.vizja.testweb.model.TestSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestScheduleRepository extends JpaRepository<TestSchedule, Long> {

       List<TestSchedule> findByProjectIdOrderByCreatedAtDesc(Long projectId);

       List<TestSchedule> findByProjectIdAndStatusOrderByCreatedAtDesc(Long projectId,
                     TestSchedule.ScheduleStatus status);

       List<TestSchedule> findByStatus(TestSchedule.ScheduleStatus status);

       List<TestSchedule> findByProjectIdAndUserStoryIdOrderByCreatedAtDesc(Long projectId, String userStoryId);

       @Query("SELECT s FROM TestSchedule s WHERE s.status = 'SCHEDULED' AND s.nextRunTime <= :currentTime")
       List<TestSchedule> findSchedulesToRun(@Param("currentTime") LocalDateTime currentTime);

       @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND s.status IN ('SCHEDULED', 'RUNNING')")
       List<TestSchedule> findActiveSchedulesByProject(@Param("projectId") Long projectId);

       @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND " +
                     "((s.startTime >= :startDate AND s.startTime <= :endDate) OR " +
                     "(s.endTime >= :startDate AND s.endTime <= :endDate) OR " +
                     "(s.startTime <= :startDate AND s.endTime >= :endDate))")
       List<TestSchedule> findSchedulesInDateRange(
                     @Param("projectId") Long projectId,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       @Query("SELECT s FROM TestSchedule s WHERE s.scheduleType != 'ONCE' AND s.status IN ('SCHEDULED', 'RUNNING')")
       List<TestSchedule> findRecurringSchedules();

       @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND s.lastRunTime IS NOT NULL ORDER BY s.lastRunTime DESC")
       List<TestSchedule> findRecentlyRunSchedules(@Param("projectId") Long projectId);

       List<TestSchedule> findByProjectIdAndScheduleTypeOrderByCreatedAtDesc(Long projectId,
                     TestSchedule.ScheduleType scheduleType);

       @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND " +
                     "(LOWER(s.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                     "LOWER(s.userStoryId) LIKE LOWER(CONCAT('%', :searchText, '%')))")
       List<TestSchedule> searchSchedules(@Param("projectId") Long projectId, @Param("searchText") String searchText);

       TestSchedule findByLastTestRunId(Long testRunId);

       @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND " +
                     "DATE(s.nextRunTime) = DATE(:date) AND s.status = 'SCHEDULED'")
       List<TestSchedule> findSchedulesForToday(@Param("projectId") Long projectId, @Param("date") LocalDateTime date);

       @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND " +
                     "s.nextRunTime > :currentTime AND s.status = 'SCHEDULED' ORDER BY s.nextRunTime ASC")
       List<TestSchedule> findUpcomingSchedules(@Param("projectId") Long projectId,
                     @Param("currentTime") LocalDateTime currentTime);

       @Query("SELECT COUNT(s) FROM TestSchedule s WHERE s.projectId = :projectId AND s.status = :status")
       Long countByProjectIdAndStatus(@Param("projectId") Long projectId,
                     @Param("status") TestSchedule.ScheduleStatus status);

       @Query("SELECT s FROM TestSchedule s WHERE s.projectId = :projectId AND s.status = 'FAILED' ORDER BY s.updatedAt DESC")
       List<TestSchedule> findFailedSchedules(@Param("projectId") Long projectId);
}
