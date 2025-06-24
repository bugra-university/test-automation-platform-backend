package project_team09.utilities;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Enhanced TestNG Listener for screenshot capture and real-time reporting
 * Integrates with WebDriverManager and database persistence
 */
public class EnhancedTestListener implements ITestListener, ISuiteListener {
    
    // Track test execution metrics
    private static final Map<String, TestExecutionData> executionData = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> currentTestName = new ThreadLocal<>();
    private static final ThreadLocal<LocalDateTime> testStartTime = new ThreadLocal<>();
    
    // Execution data container
    public static class TestExecutionData {
        private String testName;
        private String className;
        private String methodName;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String errorMessage;
        private String stackTrace;
        private String screenshotPath;
        private String browserInfo;
        private long durationMs;
        
        // Getters and setters
        public String getTestName() { return testName; }
        public void setTestName(String testName) { this.testName = testName; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getMethodName() { return methodName; }
        public void setMethodName(String methodName) { this.methodName = methodName; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public String getStackTrace() { return stackTrace; }
        public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
        
        public String getScreenshotPath() { return screenshotPath; }
        public void setScreenshotPath(String screenshotPath) { this.screenshotPath = screenshotPath; }
        
        public String getBrowserInfo() { return browserInfo; }
        public void setBrowserInfo(String browserInfo) { this.browserInfo = browserInfo; }
        
        public long getDurationMs() { return durationMs; }
        public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
    }
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🚀 STARTING TEST CONTEXT: " + context.getName());
        System.out.println("Suite: " + context.getSuite().getName());
        System.out.println("Total Tests: " + context.getAllTestMethods().length);
        System.out.println("Browser: " + WebDriverManager.getBrowserInfo());
        System.out.println("Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(80));
        
        // Initialize WebDriver for this test context
        try {
            WebDriverManager.initializeDriver();
            System.out.println("✅ WebDriver initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize WebDriver: " + e.getMessage());
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestIdentifier(result);
        LocalDateTime startTime = LocalDateTime.now();
        
        currentTestName.set(testName);
        testStartTime.set(startTime);
        
        // Create execution data
        TestExecutionData data = new TestExecutionData();
        data.setTestName(testName);
        data.setClassName(result.getTestClass().getName());
        data.setMethodName(result.getMethod().getMethodName());
        data.setStatus("RUNNING");
        data.setStartTime(startTime);
        data.setBrowserInfo(WebDriverManager.getBrowserInfo());
        
        executionData.put(testName, data);
        
        System.out.println("\n" + "-".repeat(60));
        System.out.println("🧪 STARTING TEST: " + testName);
        System.out.println("Class: " + result.getTestClass().getName());
        System.out.println("Method: " + result.getMethod().getMethodName());
        System.out.println("Start Time: " + startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
        System.out.println("-".repeat(60));
        
        // Take initial screenshot if driver is ready
        if (WebDriverManager.isDriverReady()) {
            String screenshotPath = WebDriverManager.takeScreenshot(testName, "TEST_START");
            if (screenshotPath != null) {
                data.setScreenshotPath(screenshotPath);
            }
        }
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestIdentifier(result);
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = testStartTime.get();
        
        TestExecutionData data = executionData.get(testName);
        if (data != null) {
            data.setStatus("PASSED");
            data.setEndTime(endTime);
            
            if (startTime != null) {
                data.setDurationMs(java.time.Duration.between(startTime, endTime).toMillis());
            }
        }
        
        System.out.println("✅ TEST PASSED: " + testName);
        System.out.println("Duration: " + formatDuration(data != null ? data.getDurationMs() : 0));
        
        // Take success screenshot
        if (WebDriverManager.isDriverReady()) {
            WebDriverManager.takeScreenshot(testName, "TEST_SUCCESS");
        }
        
        cleanupThreadLocals();
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestIdentifier(result);
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = testStartTime.get();
        
        TestExecutionData data = executionData.get(testName);
        if (data != null) {
            data.setStatus("FAILED");
            data.setEndTime(endTime);
            
            if (startTime != null) {
                data.setDurationMs(java.time.Duration.between(startTime, endTime).toMillis());
            }
            
            // Capture error details
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                data.setErrorMessage(throwable.getMessage());
                data.setStackTrace(getStackTrace(throwable));
            }
        }
        
        System.err.println("❌ TEST FAILED: " + testName);
        System.err.println("Duration: " + formatDuration(data != null ? data.getDurationMs() : 0));
        
        if (result.getThrowable() != null) {
            System.err.println("Error: " + result.getThrowable().getMessage());
        }
        
        // Take failure screenshot
        if (WebDriverManager.isDriverReady()) {
            String errorMsg = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error";
            String screenshotPath = WebDriverManager.takeFailureScreenshot(testName, errorMsg);
            if (data != null && screenshotPath != null) {
                data.setScreenshotPath(screenshotPath);
            }
        }
        
        cleanupThreadLocals();
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestIdentifier(result);
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = testStartTime.get();
        
        TestExecutionData data = executionData.get(testName);
        if (data != null) {
            data.setStatus("SKIPPED");
            data.setEndTime(endTime);
            
            if (startTime != null) {
                data.setDurationMs(java.time.Duration.between(startTime, endTime).toMillis());
            }
            
            // Capture skip reason
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                data.setErrorMessage("Skipped: " + throwable.getMessage());
            }
        }
        
        System.out.println("⏭️ TEST SKIPPED: " + testName);
        
        if (result.getThrowable() != null) {
            System.out.println("Reason: " + result.getThrowable().getMessage());
        }
        
        cleanupThreadLocals();
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🏁 TEST CONTEXT FINISHED: " + context.getName());
        
        // Calculate summary statistics
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;
        
        System.out.println("📊 EXECUTION SUMMARY:");
        System.out.println("Total Tests: " + total);
        System.out.println("Passed: " + passed + " ✅");
        System.out.println("Failed: " + failed + " ❌");
        System.out.println("Skipped: " + skipped + " ⏭️");
        System.out.println("Success Rate: " + (total > 0 ? String.format("%.1f%%", (passed * 100.0 / total)) : "0%"));
        System.out.println("End Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(80));
        
        // Cleanup WebDriver
        try {
            WebDriverManager.quitDriver();
            System.out.println("✅ WebDriver cleanup completed");
        } catch (Exception e) {
            System.err.println("❌ WebDriver cleanup failed: " + e.getMessage());
        }
    }
    
    @Override
    public void onStart(ISuite suite) {
        System.out.println("\n" + "🎭 STARTING TEST SUITE: " + suite.getName());
        System.out.println("XML File: " + suite.getXmlSuite().getFileName());
        System.out.println("Parallel Mode: " + suite.getXmlSuite().getParallel());
        System.out.println("Thread Count: " + suite.getXmlSuite().getThreadCount());
    }
    
    @Override
    public void onFinish(ISuite suite) {
        System.out.println("🎭 FINISHED TEST SUITE: " + suite.getName());
        
        // Emergency cleanup for any remaining drivers
        WebDriverManager.quitAllDrivers();
        
        // Print detailed execution data
        printExecutionSummary();
    }
    
    // Helper methods
    private String getTestIdentifier(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }
    
    private void cleanupThreadLocals() {
        currentTestName.remove();
        testStartTime.remove();
    }
    
    private String formatDuration(long durationMs) {
        if (durationMs < 1000) {
            return durationMs + "ms";
        } else if (durationMs < 60000) {
            return String.format("%.2fs", durationMs / 1000.0);
        } else {
            long minutes = durationMs / 60000;
            long seconds = (durationMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
    
    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
    
    private void printExecutionSummary() {
        if (executionData.isEmpty()) {
            return;
        }
        
        System.out.println("\n" + "📋 DETAILED EXECUTION REPORT");
        System.out.println("=".repeat(100));
        
        for (TestExecutionData data : executionData.values()) {
            System.out.printf("%-50s | %-8s | %-10s | %s%n",
                data.getTestName(),
                data.getStatus(),
                formatDuration(data.getDurationMs()),
                data.getBrowserInfo()
            );
            
            if (data.getScreenshotPath() != null) {
                System.out.println("    📸 Screenshot: " + data.getScreenshotPath());
            }
            
            if (data.getErrorMessage() != null) {
                System.out.println("    ❌ Error: " + data.getErrorMessage());
            }
        }
        
        System.out.println("=".repeat(100));
    }
    
    /**
     * Get execution data for external reporting
     */
    public static Map<String, TestExecutionData> getExecutionData() {
        return new ConcurrentHashMap<>(executionData);
    }
    
    /**
     * Clear execution data (useful for cleanup)
     */
    public static void clearExecutionData() {
        executionData.clear();
    }
    
    /**
     * Take screenshot for current test
     */
    public static String takeTestScreenshot(String stepDescription) {
        String testName = currentTestName.get();
        if (testName == null) {
            testName = "UnknownTest_" + Thread.currentThread().getName();
        }
        return WebDriverManager.takeScreenshot(testName, stepDescription);
    }
} 