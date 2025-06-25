package project_team09.utilities;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Step Tracker utility for TestNG tests to report step execution progress
 * Integrates with backend StepTrackingService via HTTP API
 */
public class StepTracker {

    private static final String BASE_URL = "http://localhost:8080/api/projects";
    private static final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Thread-local storage for current test context
    private static final ThreadLocal<Long> currentProjectId = new ThreadLocal<>();
    private static final ThreadLocal<Long> currentTestCaseId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentExecutionId = new ThreadLocal<>();
    private static final ThreadLocal<Integer> currentStepNumber = new ThreadLocal<>();

    /**
     * Initialize step tracking for a test case
     */
    public static void initializeTracking(Long projectId, Long testCaseId, String executionId) {
        currentProjectId.set(projectId);
        currentTestCaseId.set(testCaseId);
        currentExecutionId.set(executionId);
        currentStepNumber.set(0);
        
        System.out.println("[StepTracker] Initialized tracking for test case " + testCaseId + 
                          " in project " + projectId + " (execution: " + executionId + ")");
    }

    /**
     * Start executing a test step
     */
    public static void startStep(String stepDescription) {
        try {
            Long projectId = currentProjectId.get();
            Long testCaseId = currentTestCaseId.get();
            String executionId = currentExecutionId.get();
            
            if (projectId == null || testCaseId == null || executionId == null) {
                System.err.println("[StepTracker] ⚠️ Warning: Step tracking not initialized. Call initializeTracking() first.");
                return;
            }

            // Increment step number
            Integer stepNumber = currentStepNumber.get();
            stepNumber = stepNumber != null ? stepNumber + 1 : 1;
            currentStepNumber.set(stepNumber);

            System.out.println("[StepTracker] 🏃 Starting Step " + stepNumber + ": " + stepDescription);

            // Send HTTP request to start step
            Map<String, Object> payload = new HashMap<>();
            payload.put("stepDescription", stepDescription);
            payload.put("executionId", executionId);

            String url = BASE_URL + "/" + projectId + "/test-suites/" + testCaseId + "/steps/" + stepNumber + "/start";
            sendHttpRequest("POST", url, payload);

        } catch (Exception e) {
            System.err.println("[StepTracker] ❌ Failed to start step: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Complete a test step successfully
     */
    public static void completeStep() {
        try {
            Long projectId = currentProjectId.get();
            Long testCaseId = currentTestCaseId.get();
            String executionId = currentExecutionId.get();
            Integer stepNumber = currentStepNumber.get();
            
            if (projectId == null || testCaseId == null || executionId == null || stepNumber == null) {
                System.err.println("[StepTracker] ⚠️ Warning: No active step to complete.");
                return;
            }

            System.out.println("[StepTracker] ✅ Completing Step " + stepNumber);

            // Send HTTP request to complete step
            Map<String, Object> payload = new HashMap<>();
            payload.put("executionId", executionId);

            String url = BASE_URL + "/" + projectId + "/test-suites/" + testCaseId + "/steps/" + stepNumber + "/complete";
            sendHttpRequest("POST", url, payload);

        } catch (Exception e) {
            System.err.println("[StepTracker] ❌ Failed to complete step: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fail a test step with error message
     */
    public static void failStep(String errorMessage) {
        try {
            Long projectId = currentProjectId.get();
            Long testCaseId = currentTestCaseId.get();
            String executionId = currentExecutionId.get();
            Integer stepNumber = currentStepNumber.get();
            
            if (projectId == null || testCaseId == null || executionId == null || stepNumber == null) {
                System.err.println("[StepTracker] ⚠️ Warning: No active step to fail.");
                return;
            }

            System.out.println("[StepTracker] ❌ Failing Step " + stepNumber + ": " + errorMessage);

            // Send HTTP request to fail step
            Map<String, Object> payload = new HashMap<>();
            payload.put("errorMessage", errorMessage);
            payload.put("executionId", executionId);

            String url = BASE_URL + "/" + projectId + "/test-suites/" + testCaseId + "/steps/" + stepNumber + "/fail";
            sendHttpRequest("POST", url, payload);

        } catch (Exception e) {
            System.err.println("[StepTracker] ❌ Failed to fail step: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Execute a step with automatic completion/failure handling
     */
    public static void executeStep(String stepDescription, StepAction action) {
        startStep(stepDescription);
        
        try {
            action.execute();
            completeStep();
        } catch (Exception e) {
            failStep("Step failed: " + e.getMessage());
            throw new RuntimeException("Step execution failed: " + stepDescription, e);
        }
    }

    /**
     * Execute a step that returns a value
     */
    public static <T> T executeStepWithResult(String stepDescription, StepFunction<T> function) {
        startStep(stepDescription);
        
        try {
            T result = function.execute();
            completeStep();
            return result;
        } catch (Exception e) {
            failStep("Step failed: " + e.getMessage());
            throw new RuntimeException("Step execution failed: " + stepDescription, e);
        }
    }

    /**
     * Clean up tracking context (call at end of test)
     */
    public static void cleanup() {
        System.out.println("[StepTracker] 🧹 Cleaning up tracking context");
        currentProjectId.remove();
        currentTestCaseId.remove();
        currentExecutionId.remove();
        currentStepNumber.remove();
    }

    /**
     * Get current step information (for debugging)
     */
    public static Map<String, Object> getCurrentStepInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("projectId", currentProjectId.get());
        info.put("testCaseId", currentTestCaseId.get());
        info.put("executionId", currentExecutionId.get());
        info.put("stepNumber", currentStepNumber.get());
        return info;
    }

    // Helper method to send HTTP requests
    private static void sendHttpRequest(String method, String url, Map<String, Object> payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json");

            if ("POST".equals(method)) {
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonPayload));
            } else if ("PUT".equals(method)) {
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonPayload));
            }

            HttpRequest request = requestBuilder.build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("[StepTracker] ✅ HTTP request successful: " + method + " " + url);
            } else {
                System.err.println("[StepTracker] ⚠️ HTTP request failed: " + response.statusCode() + " - " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("[StepTracker] ❌ HTTP request error: " + e.getMessage());
            // Don't throw exception to avoid breaking the test
        }
    }

    // Functional interfaces for step execution
    @FunctionalInterface
    public interface StepAction {
        void execute() throws Exception;
    }

    @FunctionalInterface
    public interface StepFunction<T> {
        T execute() throws Exception;
    }
} 