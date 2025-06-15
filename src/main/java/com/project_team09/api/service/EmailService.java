package com.project_team09.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

import com.project_team09.api.model.dto.TestResultDTO;
import com.project_team09.api.model.dto.TestRunDTO;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a simple email containing the test run results.
     */
    public void sendTestRunReport(String to, TestRunDTO testRun) {
        if (!StringUtils.hasLength(to)) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@testautomation.com");
        message.setTo(to);
        message.setSubject("Test Run Report: " + testRun.getName());

        StringBuilder content = new StringBuilder();
        content.append("Test Run: ").append(testRun.getName()).append("\n");
        content.append("Project: ").append(testRun.getProjectName()).append("\n");
        content.append("Status: ").append(testRun.getStatus()).append("\n");
        content.append("Start: ").append(testRun.getStartTime().format(DATE_FORMATTER)).append("\n");

        if (testRun.getEndTime() != null) {
            content.append("End: ").append(testRun.getEndTime().format(DATE_FORMATTER)).append("\n");
            content.append("Duration: ").append((testRun.getEndTime().toEpochSecond(java.time.ZoneOffset.UTC) -
                    testRun.getStartTime().toEpochSecond(java.time.ZoneOffset.UTC))).append(" seconds\n");
        }

        content.append("\nResults:\n");
        content.append("Total Tests: ").append(testRun.getTotalTestCases()).append("\n");
        content.append("Passed: ").append(testRun.getPassedTests()).append("\n");
        content.append("Failed: ").append(testRun.getFailedTests()).append("\n");
        content.append("Skipped: ").append(testRun.getSkippedTests()).append("\n");
        content.append("\nTo view the detailed report, please log in to the test automation platform.");

        message.setText(content.toString());
        mailSender.send(message);
    }

    /**
     * Sends an email containing the result of a single test in HTML format.
     */
    public void sendTestResultEmail(String to, TestResultDTO testResult) throws MessagingException {
        if (!StringUtils.hasLength(to)) {
            return;
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom("noreply@testautomation.com");
        helper.setTo(to);
        helper.setSubject("Test Result: " + testResult.getTestCaseName());

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html><body>");
        htmlContent.append("<h2>Test Result: ").append(testResult.getTestCaseName()).append("</h2>");

        // Status information and color
        String statusColor = "green";
        if ("FAILED".equals(testResult.getStatus())) {
            statusColor = "red";
        } else if ("SKIPPED".equals(testResult.getStatus())) {
            statusColor = "orange";
        }

        htmlContent.append("<p><strong>Status:</strong> <span style='color:").append(statusColor).append(";'>")
                .append(testResult.getStatus()).append("</span></p>");

        htmlContent.append("<p><strong>Sınıf:</strong> ").append(testResult.getClassName()).append("</p>");
        htmlContent.append("<p><strong>Metod:</strong> ").append(testResult.getMethodName()).append("</p>");

        if (testResult.getStartTime() != null) {
            htmlContent.append("<p><strong>Başlangıç:</strong> ")
                    .append(testResult.getStartTime().format(DATE_FORMATTER)).append("</p>");
        }

        if (testResult.getEndTime() != null) {
            htmlContent.append("<p><strong>Bitiş:</strong> ").append(testResult.getEndTime().format(DATE_FORMATTER))
                    .append("</p>");
        }

        if (testResult.getDurationMs() != null) {
            htmlContent.append("<p><strong>Süre:</strong> ").append(testResult.getDurationMs()).append(" ms</p>");
        }

        // Error information
        if (StringUtils.hasLength(testResult.getErrorMessage())) {
            htmlContent.append("<h3>Error message:</h3>");
            htmlContent.append("<div style='background-color: #ffeeee; padding: 10px; border: 1px solid #ffcccc;'>");
            htmlContent.append("<pre>").append(testResult.getErrorMessage()).append("</pre>");
            htmlContent.append("</div>");
        }

        htmlContent.append("<p><a href='http://testplatform.example.com/results/").append(testResult.getId())
                .append("'>View detailed report</a></p>");

        htmlContent.append("</body></html>");

        helper.setText(htmlContent.toString(), true);
        mailSender.send(mimeMessage);
    }

    /**
     * Sends a notification about the execution of a scheduled job.
     */
    public void sendScheduledJobNotification(String to, String jobName, String testSuiteName, boolean success,
            String errorMessage) {
        if (!StringUtils.hasLength(to)) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@testautomation.com");
        message.setTo(to);

        String subject = success
                ? "Scheduled job completed successfully: " + jobName
                : "Scheduled job failed: " + jobName;

        message.setSubject(subject);

        StringBuilder content = new StringBuilder();
        content.append("Scheduled Job: ").append(jobName).append("\n");
        content.append("Test Suite: ").append(testSuiteName).append("\n");
        content.append("Status: ").append(success ? "Successful" : "Failed").append("\n");
        content.append("Time: ").append(java.time.LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");

        if (!success && StringUtils.hasLength(errorMessage)) {
            content.append("Error message:\n").append(errorMessage).append("\n\n");
        }

        content.append("To view the detailed report, please log in to the test automation platform.");

        message.setText(content.toString());
        mailSender.send(message);
    }
}