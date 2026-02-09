package com.vizja.testweb.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class WebDriverManager {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<TestConfiguration> configThreadLocal = new ThreadLocal<>();
    private static final Map<String, WebDriver> activeDrivers = new ConcurrentHashMap<>();

    public static class TestConfiguration {
        private final boolean isHeadless;
        private final String browser;
        private final int timeoutSeconds;
        private final String downloadPath;
        private final boolean enableScreenshots;
        private final String projectId;
        private final String executionId;

        public TestConfiguration(boolean isHeadless, String browser, String projectId, String executionId) {
            this.isHeadless = isHeadless;
            this.browser = browser.toLowerCase();
            this.timeoutSeconds = 30;
            this.downloadPath = "TestOutput/downloads";
            this.enableScreenshots = true;
            this.projectId = projectId;
            this.executionId = executionId;
        }

        public boolean isHeadless() {
            return isHeadless;
        }

        public String getBrowser() {
            return browser;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public String getDownloadPath() {
            return downloadPath;
        }

        public boolean isEnableScreenshots() {
            return enableScreenshots;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getExecutionId() {
            return executionId;
        }
    }

    public static void initializeDriver() {
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("webdriver.headless", "false"));
        String browser = System.getProperty("webdriver.browser", "chrome");
        String projectId = System.getProperty("test.projectId", "default");
        String executionId = System.getProperty("test.executionId", "default");
        TestConfiguration config = new TestConfiguration(isHeadless, browser, projectId, executionId);
        initializeDriver(config);
    }

    public static void initializeDriver(TestConfiguration config) {
        quitDriver();
        WebDriver driver = createWebDriver(config);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(config.getTimeoutSeconds()));
        driverThreadLocal.set(driver);
        waitThreadLocal.set(wait);
        configThreadLocal.set(config);
        String driverKey = Thread.currentThread().getName() + "_" + System.currentTimeMillis();
        activeDrivers.put(driverKey, driver);
        if (!config.isHeadless()) {
            driver.manage().window().maximize();
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getTimeoutSeconds()));
        System.out.println("[WebDriverManager] Initialized " + config.getBrowser() +
                " driver (headless: " + config.isHeadless() + ")");
    }

    private static WebDriver createWebDriver(TestConfiguration config) {
        WebDriver driver;
        switch (config.getBrowser()) {
            case "firefox":
                driver = createFirefoxDriver(config);
                break;
            case "edge":
                driver = createEdgeDriver(config);
                break;
            case "chrome":
            default:
                driver = createChromeDriver(config);
                break;
        }
        return driver;
    }

    private static WebDriver createChromeDriver(TestConfiguration config) {
        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        System.out.println("[WebDriverManager] ChromeDriver setup completed");
        ChromeOptions options = new ChromeOptions();
        if (config.isHeadless()) {
            options.addArguments("--headless");
            System.out.println("[WebDriverManager] Running in headless mode");
        } else {
            options.addArguments("--start-maximized");
            System.out.println("[WebDriverManager] Running in visible mode");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        Map<String, Object> prefs = new ConcurrentHashMap<>();
        prefs.put("download.default_directory", new File(config.getDownloadPath()).getAbsolutePath());
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        options.setExperimentalOption("prefs", prefs);
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(TestConfiguration config) {
        FirefoxOptions options = new FirefoxOptions();
        if (config.isHeadless()) {
            options.addArguments("--headless");
        }
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.download.dir", new File(config.getDownloadPath()).getAbsolutePath());
        options.addPreference("browser.download.useDownloadDir", true);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk",
                "application/pdf,application/octet-stream,application/x-winexe,application/x-exe,application/x-msdownload");
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(TestConfiguration config) {
        EdgeOptions options = new EdgeOptions();
        if (config.isHeadless()) {
            options.addArguments("--headless");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        Map<String, Object> prefs = new ConcurrentHashMap<>();
        prefs.put("download.default_directory", new File(config.getDownloadPath()).getAbsolutePath());
        prefs.put("download.prompt_for_download", false);
        options.setExperimentalOption("prefs", prefs);
        return new EdgeDriver(options);
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialized. Call initializeDriver() first.");
        }
        return driver;
    }

    public static WebDriverWait getWait() {
        WebDriverWait wait = waitThreadLocal.get();
        if (wait == null) {
            throw new IllegalStateException("WebDriverWait not initialized. Call initializeDriver() first.");
        }
        return wait;
    }

    public static TestConfiguration getConfig() {
        return configThreadLocal.get();
    }

    public static String takeScreenshot(String testName, String stepDescription) {
        WebDriver driver = driverThreadLocal.get();
        TestConfiguration config = configThreadLocal.get();
        if (driver == null || config == null || !config.isEnableScreenshots()) {
            return null;
        }
        try {
            String screenshotDir = "TestOutput/screenshots/" + config.getProjectId() + "/" + config.getExecutionId();
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String sanitizedStep = stepDescription.replaceAll("[^a-zA-Z0-9_-]", "_");
            String filename = String.format("%s_%s_%s_%s.png",
                    sanitizedTestName, sanitizedStep, timestamp, Thread.currentThread().getName());
            File screenshotFile = new File(directory, filename);
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(sourceFile, screenshotFile);
            String relativePath = screenshotFile.getAbsolutePath().replace(System.getProperty("user.dir"), ".");
            System.out.println("[Screenshot] Saved: " + relativePath);
            return relativePath;
        } catch (IOException e) {
            System.err.println("[Screenshot] Failed to save screenshot: " + e.getMessage());
            return null;
        }
    }

    public static String takeFailureScreenshot(String testName, String errorMessage) {
        return takeScreenshot(testName, "FAILURE_" + errorMessage.replaceAll("[^a-zA-Z0-9_-]", "_"));
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("[WebDriverManager] Driver quit successfully");
            } catch (Exception e) {
                System.err.println("[WebDriverManager] Error quitting driver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
                waitThreadLocal.remove();
                configThreadLocal.remove();
            }
        }
    }

    public static void quitAllDrivers() {
        System.out.println("[WebDriverManager] Emergency cleanup - quitting all drivers");
        for (Map.Entry<String, WebDriver> entry : activeDrivers.entrySet()) {
            try {
                entry.getValue().quit();
                System.out.println("[WebDriverManager] Quit driver: " + entry.getKey());
            } catch (Exception e) {
                System.err
                        .println("[WebDriverManager] Error quitting driver " + entry.getKey() + ": " + e.getMessage());
            }
        }
        activeDrivers.clear();
    }

    public static String getBrowserInfo() {
        TestConfiguration config = configThreadLocal.get();
        if (config == null) {
            return "Unknown browser";
        }
        return String.format("%s (%s)",
                config.getBrowser().toUpperCase(),
                config.isHeadless() ? "headless" : "GUI");
    }

    public static boolean isDriverReady() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            return false;
        }
        try {
            driver.getCurrentUrl();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
