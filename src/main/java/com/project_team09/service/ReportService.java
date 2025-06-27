package com.project_team09.service;

import com.project_team09.model.TestReport;
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
        String testCase = extractTestCase(content, testName);
        
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
     * Extract test case description
     */
    private String extractTestCase(String content, String testName) {
        // This is a simplified version - you could enhance this to extract actual test case descriptions
        return "TC01: " + testName + " test case execution";
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