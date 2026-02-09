package com.vizja.testweb.service;

import com.vizja.testweb.model.TestStep;
import com.vizja.testweb.repository.TestCaseRepository;
import com.vizja.testweb.repository.TestStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
public class StepTrackingService {
    @Autowired
    private TestStepRepository testStepRepository;
    @Autowired
    private TestCaseRepository testCaseRepository;
    private final Map<String, StepExecutionData> runningSteps = new ConcurrentHashMap<>();
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

        public Long getTestCaseId() {
            return testCaseId;
        }

        public void setTestCaseId(Long testCaseId) {
            this.testCaseId = testCaseId;
        }

        public Integer getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(Integer stepNumber) {
            this.stepNumber = stepNumber;
        }

        public String getStepDescription() {
            return stepDescription;
        }

        public void setStepDescription(String stepDescription) {
            this.stepDescription = stepDescription;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Long getDurationMs() {
            return durationMs;
        }

        public void setDurationMs(Long durationMs) {
            this.durationMs = durationMs;
        }

        public String getExecutionId() {
            return executionId;
        }

        public void setExecutionId(String executionId) {
            this.executionId = executionId;
        }
    }

    public void registerStepEventStream(Long projectId, SseEmitter emitter) {
        stepEventStreams.computeIfAbsent(projectId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        System.out.println("[StepTracking] Registered step event stream for project " + projectId +
                ". Total streams: " + stepEventStreams.get(projectId).size());
    }

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

    private void sendStepEventToClients(Long projectId, String eventName, Object data) {
        CopyOnWriteArrayList<SseEmitter> emitters = stepEventStreams.get(projectId);
        if (emitters != null && !emitters.isEmpty()) {
            System.out.println(
                    "[StepTracking] Sending step event '" + eventName + "' to " + emitters.size() + " clients");
            for (SseEmitter emitter : new CopyOnWriteArrayList<>(emitters)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(eventName)
                            .data(data));
                } catch (IOException e) {
                    System.out.println(
                            "[StepTracking] Failed to send step event to client, removing emitter: " + e.getMessage());
                    emitters.remove(emitter);
                }
            }
        }
    }

    public void startStep(Long projectId, Long testCaseId, Integer stepNumber, String stepDescription,
            String executionId) {
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
        updateStepInDatabase(testCaseId, stepNumber, "running", null, null);
        Map<String, Object> payload = new HashMap<>(Map.of(
                "testCaseId", testCaseId,
                "stepNumber", stepNumber,
                "stepDescription", stepDescription,
                "status", "running",
                "startTime", stepData.getStartTime(),
                "executionId", executionId));
        testCaseRepository.findById(testCaseId).ifPresent(tc -> payload.put("testCaseIdStr", tc.getTestCaseId()));
        sendStepEventToClients(projectId, "step_started", payload);
    }

    public void completeStep(Long projectId, Long testCaseId, Integer stepNumber, String executionId) {
        String stepKey = generateStepKey(testCaseId, stepNumber);
        StepExecutionData stepData = runningSteps.get(stepKey);
        if (stepData != null) {
            LocalDateTime endTime = LocalDateTime.now();
            long durationMs = java.time.Duration.between(stepData.getStartTime(), endTime).toMillis();
            stepData.setStatus("passed");
            stepData.setEndTime(endTime);
            stepData.setDurationMs(durationMs);
            System.out
                    .println("[StepTracking] ✅ Step " + stepNumber + " completed successfully (" + durationMs + "ms)");
            updateStepInDatabase(testCaseId, stepNumber, "passed", endTime, durationMs);
            Map<String, Object> payload = new HashMap<>(Map.of(
                    "testCaseId", testCaseId,
                    "stepNumber", stepNumber,
                    "stepDescription", stepData.getStepDescription(),
                    "status", "passed",
                    "endTime", endTime,
                    "duration", durationMs,
                    "executionId", executionId));
            testCaseRepository.findById(testCaseId).ifPresent(tc -> payload.put("testCaseIdStr", tc.getTestCaseId()));
            sendStepEventToClients(projectId, "step_completed", payload);
            runningSteps.remove(stepKey);
        }
    }

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
            updateStepInDatabase(testCaseId, stepNumber, "failed", endTime, durationMs);
            Map<String, Object> payload = new HashMap<>(Map.of(
                    "testCaseId", testCaseId,
                    "stepNumber", stepNumber,
                    "stepDescription", stepData.getStepDescription(),
                    "status", "failed",
                    "endTime", endTime,
                    "duration", durationMs,
                    "errorMessage", errorMessage,
                    "executionId", executionId));
            testCaseRepository.findById(testCaseId).ifPresent(tc -> payload.put("testCaseIdStr", tc.getTestCaseId()));
            sendStepEventToClients(projectId, "step_failed", payload);
            runningSteps.remove(stepKey);
        }
    }

    public void resetStepsForTestCase(Long testCaseId) {
        System.out.println("[StepTracking] Resetting all steps for test case " + testCaseId + " to pending");
        List<TestStep> steps = testStepRepository.findByTestCaseIdOrderByStepNumber(testCaseId);
        for (TestStep step : steps) {
            step.setStatus("pending");
            testStepRepository.save(step);
        }
    }

    public void markRemainingRunningStepsAsCompleted(Long testCaseId, String finalStatus) {
        if (!"passed".equals(finalStatus) && !"failed".equals(finalStatus)) {
            finalStatus = "passed";
        }
        List<TestStep> steps = testStepRepository.findByTestCaseIdOrderByStepNumber(testCaseId);
        LocalDateTime endTime = LocalDateTime.now();
        int updated = 0;
        for (TestStep step : steps) {
            if ("running".equals(step.getStatus())) {
                step.setStatus(finalStatus);
                step.setEndTime(endTime);
                step.setDurationMs(0L);
                step.setLastRun(endTime);
                testStepRepository.save(step);
                updated++;
            }
        }
        if (updated > 0) {
            System.out.println("[StepTracking] Marked " + updated + " remaining running step(s) as " + finalStatus
                    + " for test case " + testCaseId);
        }
    }

    public StepExecutionData getStepExecution(Long testCaseId, Integer stepNumber) {
        String stepKey = generateStepKey(testCaseId, stepNumber);
        return runningSteps.get(stepKey);
    }

    public List<StepExecutionData> getRunningStepsForTestCase(Long testCaseId) {
        return runningSteps.values().stream()
                .filter(step -> step.getTestCaseId().equals(testCaseId))
                .collect(java.util.stream.Collectors.toList());
    }

    private String generateStepKey(Long testCaseId, Integer stepNumber) {
        return testCaseId + "_step_" + stepNumber;
    }

    private void updateStepInDatabase(Long testCaseId, Integer stepNumber, String status, LocalDateTime endTime,
            Long durationMs) {
        try {
            List<TestStep> steps = testStepRepository.findByTestCaseIdOrderByStepNumber(testCaseId);
            for (TestStep step : steps) {
                if (step.getStepNumber().equals(stepNumber)) {
                    step.setStatus(status);
                    if ("running".equals(status)) {
                        step.setStartTime(LocalDateTime.now());
                        step.setLastRun(LocalDateTime.now());
                        step.setEndTime(null);
                        step.setDurationMs(null);
                    } else if ("passed".equals(status) || "failed".equals(status)) {
                        step.setEndTime(endTime);
                        step.setDurationMs(durationMs);
                        step.setLastRun(endTime);
                    }
                    testStepRepository.save(step);
                    System.out.println(
                            "[StepTracking] Updated step " + stepNumber + " in database with status: " + status +
                                    (durationMs != null ? ", duration: " + durationMs + "ms" : ""));
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("[StepTracking] Failed to update step in database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
