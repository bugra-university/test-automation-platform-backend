package com.project_team09.api.scheduler;

import com.project_team09.api.model.dto.ScheduledJobDTO;
import com.project_team09.api.model.dto.TestRunDTO;
import com.project_team09.api.service.EmailService;
import com.project_team09.api.service.ScheduledJobService;
import com.project_team09.api.service.TestRunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Spring Scheduler class that runs scheduled tests.
 */
@Component
public class TestScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TestScheduler.class);

    private final ScheduledJobService scheduledJobService;
    private final TestRunService testRunService;
    private final EmailService emailService;

    @Autowired
    public TestScheduler(ScheduledJobService scheduledJobService, TestRunService testRunService,
            EmailService emailService) {
        this.scheduledJobService = scheduledJobService;
        this.testRunService = testRunService;
        this.emailService = emailService;
    }

    /**
     * Method that checks and runs scheduled jobs every minute
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void checkAndRunScheduledJobs() {
        logger.info("Checking scheduled jobs...");

        // Get jobs that are due for execution and are active
        List<ScheduledJobDTO> jobsToRun = scheduledJobService.getJobsDueForExecution();
        logger.info("Found {} jobs to run", jobsToRun.size());

        for (ScheduledJobDTO job : jobsToRun) {
            try {
                executeJob(job);
            } catch (Exception e) {
                logger.error("Error executing job - {}: {}", job.getName(), e.getMessage(), e);

                // Send email notification (if specified in parameters)
                String notificationEmail = getNotificationEmail(job);
                if (StringUtils.hasLength(notificationEmail)) {
                    try {
                        emailService.sendScheduledJobNotification(notificationEmail,
                                job.getName(),
                                job.getTestSuiteName(),
                                false,
                                e.getMessage());
                    } catch (Exception emailEx) {
                        logger.error("Error sending email: {}", emailEx.getMessage());
                    }
                }
            } finally {
                // Update last execution time
                scheduledJobService.updateJobExecutionTime(job.getId(), LocalDateTime.now());
            }
        }
    }

    /**
     * Executes a specific scheduled job
     */
    private void executeJob(ScheduledJobDTO job) {
        logger.info("Executing scheduled job: {}", job.getName());

        // Create DTO for test run
        TestRunDTO testRunDTO = new TestRunDTO();
        testRunDTO.setName("Scheduled: " + job.getName());
        testRunDTO.setStatus("RUNNING");
        testRunDTO.setTriggeredBy("SCHEDULED");

        // Set the Test Suite ID
        if (job.getTestSuiteId() != null) {
            // Add parameters
            Map<String, Object> parameters = new HashMap<>();
            if (job.getParameters() != null) {
                parameters.putAll(job.getParameters());
            }

            // Add parameter for notification email
            String notificationEmail = getNotificationEmail(job);
            if (StringUtils.hasLength(notificationEmail)) {
                parameters.put("notification_email", notificationEmail);
            }

            testRunDTO.setParameters(parameters);

            // Run the test
            TestRunDTO result = testRunService.createAndRunTestSuite(job.getTestSuiteId(), testRunDTO);

            // Send notification email
            if (StringUtils.hasLength(notificationEmail)) {
                try {
                    emailService.sendTestRunReport(notificationEmail, result);
                } catch (Exception e) {
                    logger.error("Error sending email: {}", e.getMessage());
                }
            }

            logger.info("Scheduled job completed: {} - Test run status: {}",
                    job.getName(), result.getStatus());
        } else {
            logger.warn("TestSuite ID not found for scheduled job: {}", job.getName());
            throw new IllegalStateException("Test Suite ID not specified");
        }
    }

    /**
     * Retrieves the notification email address from the job parameters.
     */
    private String getNotificationEmail(ScheduledJobDTO job) {
        if (job.getParameters() != null && job.getParameters().containsKey("notification_email")) {
            Object email = job.getParameters().get("notification_email");
            if (email != null) {
                return email.toString();
            }
        }
        return null;
    }
}