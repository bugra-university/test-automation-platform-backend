package com.vizja.testweb.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import java.time.Duration;

public class Driver {
    private Driver() {
    }

    static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            String seleniumHub = System.getenv("SELENIUM_HUB_URL");
            if (seleniumHub != null && !seleniumHub.isEmpty()) {
                try {
                    org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    System.out.println("Selenium Grid'e bağlanılıyor: " + seleniumHub);
                    driver = new org.openqa.selenium.remote.RemoteWebDriver(
                            new java.net.URL(seleniumHub),
                            options);
                } catch (java.net.MalformedURLException e) {
                    e.printStackTrace();
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver();
                }
            } else {
                switch (ConfigReader.getProperty("browser")) {
                    case "chrome":
                        WebDriverManager.chromedriver().setup();
                        driver = new ChromeDriver();
                        break;
                    case "edge":
                        WebDriverManager.edgedriver().setup();
                        driver = new EdgeDriver();
                        break;
                    case "safari":
                        WebDriverManager.safaridriver().setup();
                        driver = new SafariDriver();
                        break;
                    case "firefox":
                        WebDriverManager.firefoxdriver().setup();
                        driver = new FirefoxDriver();
                        break;
                    default:
                        WebDriverManager.chromedriver().setup();
                        driver = new ChromeDriver();
                }
            }
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        }
        return driver;
    }

    public static void closeDriver() {
        if (driver != null) {
            driver = null;
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
