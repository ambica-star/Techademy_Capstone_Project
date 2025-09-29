package com.nse.stock.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Arrays;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Driver Manager class to handle WebDriver initialization and management
 * Supports Chrome, Firefox, and Edge browsers with parallel execution
 */
public class DriverManager {
    
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigReader configReader = ConfigReader.getInstance();
    
    /**
     * Get WebDriver instance for the specified browser
     * @param browserName Browser name (chrome, firefox, edge)
     * @return WebDriver instance
     */
    public static WebDriver getDriver(String browserName) {
        if (driverThreadLocal.get() == null) {
            driverThreadLocal.set(createDriver(browserName));
        }
        return driverThreadLocal.get();
    }
    
    /**
     * Get current WebDriver instance
     * @return Current WebDriver instance
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    
    /**
     * Create WebDriver instance based on browser name
     * @param browserName Browser name
     * @return WebDriver instance
     */
    private static WebDriver createDriver(String browserName) {
        WebDriver driver;
        String browser = browserName != null ? browserName.toLowerCase() : 
                        configReader.getBrowser().toLowerCase();
        
        logger.info("Creating WebDriver for browser: {}", browser);
        
        switch (browser) {
            case "chrome":
                driver = createChromeDriver();
                break;
            case "firefox":
                driver = createFirefoxDriver();
                break;
            case "edge":
                driver = createEdgeDriver();
                break;
            default:
                logger.warn("Unknown browser: {}. Defaulting to Chrome", browser);
                driver = createChromeDriver();
        }
        
        logger.info("WebDriver created successfully for browser: {}", browser);
        return driver;
    }
    
    /**
     * Create Chrome WebDriver with options
     * @return Chrome WebDriver instance
     */
    private static WebDriver createChromeDriver() {
        try {
            // Use latest WebDriverManager to automatically download compatible ChromeDriver
            WebDriverManager.chromedriver().clearDriverCache().setup();

            ChromeOptions options = new ChromeOptions();

            // Add Chrome options for stability and compatibility
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-extensions");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-running-insecure-content");
            options.addArguments("--disable-features=VizDisplayCompositor");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--start-maximized");

            if (configReader.isHeadless()) {
                options.addArguments("--headless");  // Use standard headless mode for Selenium 3
            }

            // Exclude automation switches for better compatibility
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            logger.info("Creating Chrome WebDriver with latest WebDriverManager 5.6.2 and Chrome support");
            return new ChromeDriver(options);
        } catch (Exception e) {
            logger.error("Failed to create Chrome driver: {}", e.getMessage());
            throw new RuntimeException("Chrome driver setup failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create Firefox WebDriver with options
     * @return Firefox WebDriver instance
     */
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();

        if (configReader.isHeadless()) {
            options.addArguments("--headless");
        }

        // Firefox specific options for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        // Disable automation indicators
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);

        // Disable notifications and popups
        options.addPreference("dom.push.enabled", false);
        options.addPreference("dom.webnotifications.enabled", false);

        // Set window size
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        logger.info("Creating Firefox WebDriver");
        return new FirefoxDriver(options);
    }
    
    /**
     * Create Edge WebDriver with options
     * @return Edge WebDriver instance
     */
    private static WebDriver createEdgeDriver() {
        try {
            WebDriverManager.edgedriver().clearDriverCache().setup();

            logger.info("Creating Edge WebDriver with basic configuration for Selenium 3.x");
            return new EdgeDriver();
        } catch (Exception e) {
            logger.error("Failed to create Edge driver: {}", e.getMessage());
            throw new RuntimeException("Edge driver setup failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Quit the current WebDriver instance with proper cleanup
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                logger.info("Quitting WebDriver");
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.warn("Error while quitting WebDriver: {}", e.getMessage());
                // Force kill browser processes if needed
                killBrowserProcesses();
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Force kill browser processes if they don't close properly
     */
    private static void killBrowserProcesses() {
        try {
            // Kill Chrome processes
            Runtime.getRuntime().exec("taskkill /F /IM chrome.exe /T");
            Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");

            // Kill Firefox processes
            Runtime.getRuntime().exec("taskkill /F /IM firefox.exe /T");
            Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe /T");

            // Kill Edge processes
            Runtime.getRuntime().exec("taskkill /F /IM msedge.exe /T");
            Runtime.getRuntime().exec("taskkill /F /IM msedgedriver.exe /T");

            logger.info("Browser processes killed forcefully");
        } catch (Exception e) {
            logger.warn("Error killing browser processes: {}", e.getMessage());
        }
    }
}
