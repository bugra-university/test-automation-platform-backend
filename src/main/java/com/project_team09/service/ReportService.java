package com.project_team09.service;

import com.project_team09.model.TestReport;
import com.project_team09.model.TestCase;
import com.project_team09.model.TestExecutionMetadata;
import com.project_team09.repository.TestCaseRepository;
import com.project_team09.repository.TestExecutionMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ReportService {
    
    @Autowired
    private TestCaseRepository testCaseRepository;
    
    @Autowired
    private TestExecutionMetadataRepository testExecutionMetadataRepository;
    
    private static final String REPORTS_DIR = "TestOutput/reports";
    private static final String EXECUTION_DIR = "TestOutput";
    
    /**
     * Get all test reports from TestOutput directory
     */
    public List<TestReport> getAllReports() {
        List<TestReport> reports = new ArrayList<>();
        
        try {
            Path reportsPath = Paths.get(REPORTS_DIR);
            if (!Files.exists(reportsPath)) {
                return reports;
            }
            
            Files.list(reportsPath)
                .filter(path -> path.toString().endsWith(".html"))
                .filter(path -> path.getFileName().toString().startsWith("extentReport__"))
                .forEach(path -> {
                    try {
                        TestReport report = parseReportFile(path);
                        if (report != null) {
                            reports.add(report);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing report: " + path.getFileName() + " - " + e.getMessage());
                    }
                });
                
        } catch (IOException e) {
            System.err.println("Error reading reports directory: " + e.getMessage());
        }
        
        // Sort by execution time (newest first)
        reports.sort((a, b) -> b.getExecutedAt().compareTo(a.getExecutedAt()));
        
        return reports;
    }
    
    /**
     * Parse individual report file to extract metadata
     */
    private TestReport parseReportFile(Path filePath) throws IOException {
        String fileName = filePath.getFileName().toString();
        String content = Files.readString(filePath);
        
        // Extract timestamp from filename: extentReport__01_56_15_25062025.html
        LocalDateTime executedAt = extractTimestampFromFilename(fileName);
        
        // Parse HTML content for test details
        String testName = extractTestName(content);
        String description = extractDescription(content);
        String status = extractStatus(content);
        Map<String, Integer> testCounts = extractTestCounts(content);
        String duration = extractDuration(content);
        
        // Generate ID from filename
        String id = fileName.replace(".html", "").replace("extentReport__", "report_");
        
        // Map to user story format
        String title = mapToUserStory(testName);
        String testCase = extractTestCase(content, testName, filePath);
        
        TestReport report = new TestReport();
        report.setId(id);
        report.setFileName(fileName);
        report.setTitle(title);
        report.setDescription(description);
        report.setTestCase(testCase);
        report.setStatus(status);
        report.setPassedCount(testCounts.get("passed"));
        report.setTotalCount(testCounts.get("total"));
        report.setExecutedAt(executedAt);
        report.setFileSize(formatFileSize(Files.size(filePath)));
        report.setDuration(duration);
        report.setFilePath(filePath.toString());
        report.setTestName(testName);
        report.setCreatedAt(LocalDateTime.now());
        
        return report;
    }
    
    /**
     * Extract timestamp from filename
     */
    private LocalDateTime extractTimestampFromFilename(String fileName) {
        // extentReport__01_56_15_25062025.html -> 01:56:15 25/06/2025
        Pattern pattern = Pattern.compile("extentReport__(\\d{2})_(\\d{2})_(\\d{2})_(\\d{8})\\.html");
        Matcher matcher = pattern.matcher(fileName);
        
        if (matcher.find()) {
            String hour = matcher.group(1);
            String minute = matcher.group(2);
            String second = matcher.group(3);
            String dateStr = matcher.group(4); // 25062025
            
            // Parse date: 25062025 -> 25/06/2025
            String day = dateStr.substring(0, 2);
            String month = dateStr.substring(2, 4);
            String year = dateStr.substring(4, 8);
            
            String dateTimeStr = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        return LocalDateTime.now();
    }
    
    /**
     * Extract test name from HTML content
     */
    private String extractTestName(String content) {
        Pattern pattern = Pattern.compile("<span class='test-name'>([^<]+)</span>");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Unknown Test";
    }
    
    /**
     * Extract description from HTML content
     */
    private String extractDescription(String content) {
        Pattern pattern = Pattern.compile("<div class='test-desc'>([^<]+)</div>");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "No description available";
    }
    
    /**
     * Extract test status and counts
     */
    private String extractStatus(String content) {
        Map<String, Integer> counts = extractTestCounts(content);
        int passed = counts.get("passed");
        int failed = counts.get("failed");
        
        if (passed > 0 && failed == 0) {
            return "passed";
        } else if (passed == 0 && failed > 0) {
            return "failed";
        } else if (passed > 0 && failed > 0) {
            return "mixed";
        } else {
            // No tests found, check content for any indicators
            if (content.contains("test(s) passed") && !content.contains("1</span> test(s) failed") && !content.contains("2</span> test(s) failed")) {
                return "passed";
            }
            return "failed";
        }
    }
    
    /**
     * Extract test counts (passed/total)
     */
    private Map<String, Integer> extractTestCounts(String content) {
        Map<String, Integer> counts = new HashMap<>();
        
        // Extract passed count
        Pattern passedPattern = Pattern.compile("<span class='strong'>(\\d+)</span> test\\(s\\) passed");
        Matcher passedMatcher = passedPattern.matcher(content);
        int passed = 0;
        if (passedMatcher.find()) {
            passed = Integer.parseInt(passedMatcher.group(1));
        }
        
        // Extract failed count
        Pattern failedPattern = Pattern.compile("<span class='strong[^>]*'>(\\d+)</span> test\\(s\\) failed");
        Matcher failedMatcher = failedPattern.matcher(content);
        int failed = 0;
        if (failedMatcher.find()) {
            failed = Integer.parseInt(failedMatcher.group(1));
        }
        
        counts.put("passed", passed);
        counts.put("failed", failed);
        counts.put("total", passed + failed);
        
        return counts;
    }
    
    /**
     * Extract duration from HTML content
     */
    private String extractDuration(String content) {
        Pattern pattern = Pattern.compile("<span class='label time-taken[^>]*'>([^<]+)</span>");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Unknown";
    }
    
    /**
     * Map test name to user story format
     */
    private String mapToUserStory(String testName) {
        if (testName.toLowerCase().contains("kullanıcı kaydı") || testName.toLowerCase().contains("kayıt")) {
            return "US_01 - User Registration";
        } else if (testName.toLowerCase().contains("geçersiz") || testName.toLowerCase().contains("invalid")) {
            return "US_02 - Invalid User Registration";
        } else if (testName.toLowerCase().contains("fatura") || testName.toLowerCase().contains("billing")) {
            return "US_03 - Billing Address Management";
        } else if (testName.toLowerCase().contains("karşılaştır") || testName.toLowerCase().contains("compare")) {
            return "US_06 - Product Comparison";
        } else if (testName.toLowerCase().contains("sepet") || testName.toLowerCase().contains("cart")) {
            return "US_08 - Shopping Cart Operations";
        } else if (testName.toLowerCase().contains("satıcı") || testName.toLowerCase().contains("vendor")) {
            return "US_12 - Vendor Management";
        } else if (testName.toLowerCase().contains("profil") || testName.toLowerCase().contains("profile")) {
            return "US_18 - User Profile Management";
        } else if (testName.toLowerCase().contains("ödeme") || testName.toLowerCase().contains("payment")) {
            return "US_20 - Payment Processing";
        }
        return "US_XX - " + testName;
    }
    
    /**
     * Extract test case description from HTML content using execution metadata
     */
    private String extractTestCase(String content, String testName, Path filePath) {
        // First try to extract test case from execution metadata (NEW APPROACH)
        String testCaseId = extractTestCaseFromMetadata(content, testName, filePath);
        if (testCaseId != null) {
            System.out.println("[ReportService] Found test case from metadata: " + testCaseId);
            return testCaseId + ": " + testName + " test case execution";
        }
        
        // Fallback: try to extract test case ID from test name or content (OLD APPROACH)
        testCaseId = extractTestCaseId(content, testName);
        if (testCaseId != null && !testCaseId.equals("TC01")) {
            System.out.println("[ReportService] Found test case from content: " + testCaseId);
            return testCaseId + ": " + testName + " test case execution";
        }
        
        // Final fallback: try to extract from method names in content (OLD APPROACH)
        testCaseId = extractTestCaseFromMethodName(content);
        if (testCaseId != null) {
            System.out.println("[ReportService] Found test case from method name: " + testCaseId);
            return testCaseId + ": " + testName + " test case execution";
        }
        
        // Default fallback (should rarely happen now)
        System.out.println("[ReportService] Using default fallback: TC01");
        return "TC01: " + testName + " test case execution";
    }
    
    /**
     * Extract test case ID from execution metadata (NEW METADATA-BASED APPROACH)
     */
    private String extractTestCaseFromMetadata(String content, String testName, Path filePath) {
        try {
            // Primary: Try to find metadata by exact report file name
            String reportFileName = filePath.getFileName().toString();
            Optional<TestExecutionMetadata> metadataOpt = testExecutionMetadataRepository.findByReportFileName(reportFileName);
            if (metadataOpt.isPresent()) {
                TestExecutionMetadata metadata = metadataOpt.get();
                System.out.println("[ReportService] ✅ Found metadata by file name: " + reportFileName + " -> " + metadata.getTestCaseId());
                return metadata.getTestCaseId();
            }
            
            // Secondary: Try to find metadata by report file path
            String reportPath = filePath.getParent().toString();
            metadataOpt = testExecutionMetadataRepository.findByReportFilePath(reportPath);
            if (metadataOpt.isPresent()) {
                TestExecutionMetadata metadata = metadataOpt.get();
                System.out.println("[ReportService] ✅ Found metadata by report path: " + reportPath + " -> " + metadata.getTestCaseId());
                return metadata.getTestCaseId();
            }
            
            // Fallback: Try to find by similar report file name pattern
            String reportFilePattern = reportFileName.substring(0, Math.min(15, reportFileName.length())); // Take first 15 chars
            List<TestExecutionMetadata> metadataList = testExecutionMetadataRepository.findByReportFileNameContaining(reportFilePattern);
            if (!metadataList.isEmpty()) {
                TestExecutionMetadata metadata = metadataList.get(0);
                System.out.println("[ReportService] ✅ Found metadata by pattern: " + reportFilePattern + " -> " + metadata.getTestCaseId());
                return metadata.getTestCaseId();
            }
            
            // Alternative: lookup by recent execution time (last 1 hour) for project 2 (testv1)
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            LocalDateTime now = LocalDateTime.now();
            List<TestExecutionMetadata> recentMetadata = testExecutionMetadataRepository.findByProjectIdAndExecutionTimeBetween(
                2L, // testv1 project ID
                oneHourAgo, 
                now
            );
            
            if (!recentMetadata.isEmpty()) {
                TestExecutionMetadata metadata = recentMetadata.get(0);
                System.out.println("[ReportService] ✅ Found recent metadata for project 2: " + metadata.getTestCaseId() + " at " + metadata.getExecutionTime());
                return metadata.getTestCaseId();
            }
            
            System.out.println("[ReportService] ❌ No metadata found for file: " + filePath.getFileName());
            
        } catch (Exception e) {
            System.out.println("[ReportService] Error extracting test case from metadata: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Extract report file name from content
     */
    private String extractReportFileName(String content) {
        // This could be enhanced to extract actual file name from report content
        // For now, return null to use the time-based lookup
        return null;
    }

    /**
     * Extract test case ID from test name or content
     */
    private String extractTestCaseId(String content, String testName) {
        // Check if test name contains TC pattern
        Pattern tcPattern = Pattern.compile("(TC\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher tcMatcher = tcPattern.matcher(testName);
        if (tcMatcher.find()) {
            return tcMatcher.group(1).toUpperCase();
        }
        
        // Check content for TC pattern
        tcMatcher = tcPattern.matcher(content);
        if (tcMatcher.find()) {
            return tcMatcher.group(1).toUpperCase();
        }
        
        return null;
    }
    
    /**
     * Extract test case ID from method names in HTML content using database lookup
     */
    private String extractTestCaseFromMethodName(String content) {
        // First try to extract method name pattern from content
        String methodName = extractMethodNameFromContent(content);
        if (methodName != null) {
            // Query database to find test case with matching method pattern
            String testCaseId = findTestCaseByMethodName(methodName);
            if (testCaseId != null) {
                return testCaseId;
            }
        }
        
        // Fallback: try pattern matching for tc##_ format
        Pattern tcMethodPattern = Pattern.compile("tc(\\d+)_", Pattern.CASE_INSENSITIVE);
        Matcher matcher = tcMethodPattern.matcher(content.toLowerCase());
        if (matcher.find()) {
            String number = matcher.group(1);
            String testCaseId = "TC" + String.format("%02d", Integer.parseInt(number));
            return testCaseId;
        }
        
        return null;
    }
    
    /**
     * Extract method name from HTML content
     */
    private String extractMethodNameFromContent(String content) {
        // Look for TestNG method patterns in HTML content
        String[] patterns = {
            "(?i)(tc\\d+_[a-zA-Z0-9_]{3,})",  // tc01_methodName (minimum 3 chars after _)
            "(?i)class='test-name'>([^<]*tc\\d+_[a-zA-Z0-9_]+)",  // from test-name span
            "(?i)test[\\s\\w]*['\"]([^'\"]*tc\\d+_[a-zA-Z0-9_]+)['\"]"
        };
        
        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String methodName = matcher.group(1);
                // Validate method name (should contain tc and underscore and be reasonable length)
                if (methodName != null && methodName.contains("tc") && methodName.contains("_") && methodName.length() > 5) {
                    return methodName;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Find test case ID by method name in database
     */
    private String findTestCaseByMethodName(String methodName) {
        try {
            // Query all test cases and look for matching method names
            List<TestCase> allTestCases = testCaseRepository.findAll();
            
            for (TestCase testCase : allTestCases) {
                // Check if method name matches the test case
                if (isMethodNameMatchingTestCase(methodName, testCase)) {
                    return testCase.getTestCaseId();
                }
            }
        } catch (Exception e) {
            // Silent error handling - method extraction is not critical
        }
        
        return null;
    }
    
    /**
     * Check if method name matches test case
     */
    private boolean isMethodNameMatchingTestCase(String methodName, TestCase testCase) {
        String lowerMethodName = methodName.toLowerCase();
        String testCaseId = testCase.getTestCaseId().toLowerCase();
        
        // Direct pattern match: tc01_ matches TC01, tc02_ matches TC02, etc.
        if (lowerMethodName.startsWith(testCaseId.replace("tc", "tc"))) {
            return true;
        }
        
        // Extract number from test case ID and check method pattern
        String testCaseNumber = testCaseId.replace("tc", "");
        if (lowerMethodName.startsWith("tc" + testCaseNumber + "_")) {
            return true;
        }
        
        // Check if test case objective contains method keywords
        String objective = testCase.getObjective() != null ? testCase.getObjective().toLowerCase() : "";
        
        // Extract keywords from method name (remove tc##_ prefix)
        String methodKeywords = lowerMethodName.replaceFirst("tc\\d+_", "");
        if (methodKeywords.length() > 3) { // Only check if we have meaningful keywords
            if (objective.contains(methodKeywords)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Format file size
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + "KB";
        return (bytes / (1024 * 1024)) + "MB";
    }
    
    /**
     * Get reports by status
     */
    public List<TestReport> getReportsByStatus(String status) {
        return getAllReports().stream()
                .filter(report -> status.equalsIgnoreCase(report.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get report by ID
     */
    public TestReport getReportById(String reportId) {
        return getAllReports().stream()
                .filter(report -> reportId.equals(report.getId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get report file as Resource
     */
    public Resource getReportFile(String reportId) {
        TestReport report = getReportById(reportId);
        if (report != null) {
            File file = new File(report.getFilePath());
            if (file.exists()) {
                return new FileSystemResource(file);
            }
        }
        return null;
    }
    
    /**
     * Get report content as string
     */
    public String getReportContent(String reportId) {
        TestReport report = getReportById(reportId);
        if (report != null) {
            try {
                return Files.readString(Paths.get(report.getFilePath()));
            } catch (IOException e) {
                System.err.println("Error reading report content: " + e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * Delete report file
     */
    public boolean deleteReport(String reportId) {
        TestReport report = getReportById(reportId);
        if (report != null) {
            try {
                Files.deleteIfExists(Paths.get(report.getFilePath()));
                return true;
            } catch (IOException e) {
                System.err.println("Error deleting report: " + e.getMessage());
            }
        }
        return false;
    }
    
    /**
     * Get report statistics
     */
    public Map<String, Object> getReportStatistics() {
        List<TestReport> reports = getAllReports();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", reports.size());
        stats.put("passed", reports.stream().filter(r -> "passed".equals(r.getStatus())).count());
        stats.put("failed", reports.stream().filter(r -> "failed".equals(r.getStatus())).count());
        stats.put("mixed", reports.stream().filter(r -> "mixed".equals(r.getStatus())).count());
        
        return stats;
    }
} 