package com.project_team09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.project_team09.model.TestStep;
import com.project_team09.repository.TestStepRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.IOException;

@Service
public class StepTrackingService {

    @Autowired
    private TestStepRepository testStepRepository;

    // Track step execution status
    private final Map<String, StepExecutionData> runningSteps = new ConcurrentHashMap<>();
    
    // SSE Event Streams for real-time step updates  
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> stepEventStreams = new ConcurrentHashMap<>();

    public static class StepExecutionData {
        private Long testCaseId;
        private Integer stepNumber;
        private String stepDescription;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String errorMessage;
        private Long durationMs;
        private String executionId;

        // Getters and setters
        public Long getTestCaseId() { return testCaseId; }
        public void setTestCaseId(Long testCaseId) { this.testCaseId = testCaseId; }

        public Integer getStepNumber() { return stepNumber; }
        public void setStepNumber(Integer stepNumber) { this.stepNumber = stepNumber; }

        public String getStepDescription() { return stepDescription; }
        public void setStepDescription(String stepDescription) { this.stepDescription = stepDescription; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public Long getDurationMs() { return durationMs; }
        public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
    }

    /**
     * Register SSE emitter for step events
     */
    public void registerStepEventStream(Long projectId, SseEmitter emitter) {
        stepEventStreams.computeIfAbsent(projectId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        System.out.println("[StepTracking] Registered step event stream for project " + projectId + 
                          ". Total streams: " + stepEventStreams.get(projectId).size());
    }

    /**
     * Unregister SSE emitter for step events
     */
    public void unregisterStepEventStream(Long projectId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = stepEventStreams.get(projectId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                stepEventStreams.remove(projectId);
            }
            System.out.println("[StepTracking] Unregistered step event stream for project " + projectId + 
                              ". Remaining streams: " + emitters.size());
        }
    }

    /**
     * Send step event to all connected clients for a project
     */
    private void sendStepEventToClients(Long projectId, String eventName, Object data) {
        CopyOnWriteArrayList<SseEmitter> emitters = stepEventStreams.get(projectId);
        if (emitters != null && !emitters.isEmpty()) {
            System.out.println("[StepTracking] Sending step event '" + eventName + "' to " + emitters.size() + " clients");
            
            for (SseEmitter emitter : new CopyOnWriteArrayList<>(emitters)) {
                try {
                    emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
                } catch (IOException e) {
                    System.out.println("[StepTracking] Failed to send step event to client, removing emitter: " + e.getMessage());
                    emitters.remove(emitter);
                }
            }
        }
    }

    /**
     * Start tracking a test step
     */
    public void startStep(Long projectId, Long testCaseId, Integer stepNumber, String stepDescription, String executionId) {
        String stepKey = generateStepKey(testCaseId, stepNumber);
        
        System.out.println("[StepTracking] Starting step " + stepNumber + " for test case " + testCaseId);
        
        StepExecutionData stepData = new StepExecutionData();
        stepData.setTestCaseId(testCaseId);
        stepData.setStepNumber(stepNumber);
        stepData.setStepDescription(stepDescription);
        stepData.setStatus("running");
        stepData.setStartTime(LocalDateTime.now());
        stepData.setExecutionId(executionId);
        
        runningSteps.put(stepKey, stepData);

        // Update database
        updateStepInDatabase(testCaseId, stepNumber, "running", null, null);

        // Send SSE event
        sendStepEventToClients(projectId, "step_started", Map.of(
            "testCaseId", testCaseId,
            "stepNumber", stepNumber,
            "stepDescription", stepDescription,
            "status", "running",
            "startTime", stepData.getStartTime(),
            "executionId", executionId
        ));
    }

    /**
     * Complete a test step (success)
     */
    public void completeStep(Long projectId, Long testCaseId, Integer stepNumber, String executionId) {
        String stepKey = generateStepKey(testCaseId, stepNumber);
        StepExecutionData stepData = runningSteps.get(stepKey);
        
        if (stepData != null) {
            LocalDateTime endTime = LocalDateTime.now();
            long durationMs = java.time.Duration.between(stepData.getStartTime(), endTime).toMillis();
            
            stepData.setStatus("passed");
            stepData.setEndTime(endTime);
            stepData.setDurationMs(durationMs);

            System.out.println("[StepTracking] ✅ Step " + stepNumber + " completed successfully (" + durationMs + "ms)");

            // Update database
            updateStepInDatabase(testCaseId, stepNumber, "passed", endTime, durationMs);

            // Send SSE event
            sendStepEventToClients(projectId, "step_completed", Map.of(
                "testCaseId", testCaseId,
                "stepNumber", stepNumber,
                "stepDescription", stepData.getStepDescription(),
                "status", "passed",
                "endTime", endTime,
                "duration", durationMs,
                "executionId", executionId
            ));

            // Clean up tracking data after a delay
            runningSteps.remove(stepKey);
        }
    }

    /**
     * Fail a test step
     */
    public void failStep(Long projectId, Long testCaseId, Integer stepNumber, String errorMessage, String executionId) {
        String stepKey = generateStepKey(testCaseId, stepNumber);
        StepExecutionData stepData = runningSteps.get(stepKey);
        
        if (stepData != null) {
            LocalDateTime endTime = LocalDateTime.now();
            long durationMs = java.time.Duration.between(stepData.getStartTime(), endTime).toMillis();
            
            stepData.setStatus("failed");
            stepData.setEndTime(endTime);
            stepData.setDurationMs(durationMs);
            stepData.setErrorMessage(errorMessage);

            System.out.println("[StepTracking] ❌ Step " + stepNumber + " failed: " + errorMessage);

            // Update database
            updateStepInDatabase(testCaseId, stepNumber, "failed", endTime, durationMs);

            // Send SSE event
            sendStepEventToClients(projectId, "step_failed", Map.of(
                "testCaseId", testCaseId,
                "stepNumber", stepNumber,
                "stepDescription", stepData.getStepDescription(),
                "status", "failed",
                "endTime", endTime,
                "duration", durationMs,
                "errorMessage", errorMessage,
                "executionId", executionId
            ));

            // Clean up tracking data
            runningSteps.remove(stepKey);
        }
    }

    /**
     * Reset all steps for a test case to pending status
     */
    public void resetStepsForTestCase(Long testCaseId) {
        System.out.println("[StepTracking] Resetting all steps for test case " + testCaseId + " to pending");
        
        List<TestStep> steps = testStepRepository.findByTestCaseIdOrderByStepNumber(testCaseId);
        for (TestStep step : steps) {
            step.setStatus("pending");
            testStepRepository.save(step);
        }
    }

    /**
     * Get execution data for a specific step
     */
    public StepExecutionData getStepExecution(Long testCaseId, Integer stepNumber) {
        String stepKey = generateStepKey(testCaseId, stepNumber);
        return runningSteps.get(stepKey);
    }

    /**
     * Get all running steps for a test case
     */
    public List<StepExecutionData> getRunningStepsForTestCase(Long testCaseId) {
        return runningSteps.values().stream()
            .filter(step -> step.getTestCaseId().equals(testCaseId))
            .collect(java.util.stream.Collectors.toList());
    }

    // Helper methods

    private String generateStepKey(Long testCaseId, Integer stepNumber) {
        return testCaseId + "_step_" + stepNumber;
    }

    private void updateStepInDatabase(Long testCaseId, Integer stepNumber, String status, LocalDateTime endTime, Long durationMs) {
        try {
            List<TestStep> steps = testStepRepository.findByTestCaseIdOrderByStepNumber(testCaseId);
            
            for (TestStep step : steps) {
                if (step.getStepNumber().equals(stepNumber)) {
                    step.setStatus(status);
                    
                    // Note: TestStep model might need additional fields for timing
                    // For now, we're just updating the status
                    testStepRepository.save(step);
                    
                    System.out.println("[StepTracking] Updated step " + stepNumber + " in database with status: " + status);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("[StepTracking] Failed to update step in database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 