package com.nse.stock.pages;

import com.nse.stock.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.concurrent.TimeUnit;

/**
 * Page Object Model for NSE India Home Page
 * Contains locators and methods for interacting with the home page elements
 */
public class NSEHomePage {
    
    private static final Logger logger = LogManager.getLogger(NSEHomePage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private ConfigReader configReader;
    
    // Page Elements
    @FindBy(id = "search-box")
    private WebElement searchBox;
    
    @FindBy(xpath = "//input[@placeholder='Search for stocks, indices, ETFs & more']")
    private WebElement searchInput;
    
    @FindBy(xpath = "//button[@type='submit' or contains(@class,'search-btn')]")
    private WebElement searchButton;
    
    @FindBy(xpath = "//div[contains(@class,'search-suggestions')]")
    private WebElement searchSuggestions;
    
    @FindBy(xpath = "//a[contains(@href,'get-quotes')]")
    private WebElement getQuotesLink;
    
    @FindBy(xpath = "//div[contains(@class,'cookie-banner')]//button[contains(text(),'Accept')]")
    private WebElement acceptCookiesButton;
    
    @FindBy(xpath = "//div[contains(@class,'modal')]//button[contains(@class,'close')]")
    private WebElement closeModalButton;
    
    // Constructor
    public NSEHomePage(WebDriver driver) {
        this.driver = driver;
        this.configReader = ConfigReader.getInstance();
        this.wait = new WebDriverWait(driver, configReader.getExplicitWait());
        PageFactory.initElements(driver, this);
        logger.info("NSE Home Page initialized");
    }
    
    /**
     * Navigate to NSE India website
     */
    public void navigateToNSE() {
        String url = configReader.getNSEBaseUrl();
        logger.info("Navigating to NSE website: {}", url);
        driver.get(url);
        
        // Handle any popups or cookies
        handleInitialPopups();
    }
    
    /**
     * Handle initial popups, cookies, and modals
     */
    private void handleInitialPopups() {
        try {
            // Wait for page to load completely
            Thread.sleep(5000);

            // Handle multiple types of overlays and popups
            handleCookieConsent();
            handleNotificationPopups();
            handleModalDialogs();
            handleAdvertisements();

        } catch (Exception e) {
            logger.warn("Error handling initial popups: {}", e.getMessage());
        }
    }

    private void handleCookieConsent() {
        try {
            WebElement cookieButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Accept') or contains(text(),'OK') or contains(@class,'cookie') or contains(@id,'cookie')]")
                )
            );
            cookieButton.click();
            logger.info("Cookie consent accepted");
        } catch (Exception e) {
            logger.debug("No cookie consent popup found");
        }
    }

    private void handleNotificationPopups() {
        try {
            WebElement notificationClose = driver.findElement(
                By.xpath("//button[contains(@class,'notification') or contains(text(),'No Thanks') or contains(@aria-label,'notification')]")
            );
            if (notificationClose.isDisplayed()) {
                notificationClose.click();
                logger.info("Notification popup closed");
            }
        } catch (Exception e) {
            logger.debug("No notification popup found");
        }
    }

    private void handleModalDialogs() {
        try {
            WebElement closeModal = driver.findElement(
                By.xpath("//button[contains(@class,'close') or contains(@aria-label,'close') or contains(@class,'modal-close')]")
            );
            if (closeModal.isDisplayed()) {
                closeModal.click();
                logger.info("Modal dialog closed");
            }
        } catch (Exception e) {
            logger.debug("No modal dialog found");
        }
    }

    private void handleAdvertisements() {
        try {
            WebElement adClose = driver.findElement(
                By.xpath("//button[contains(@class,'ad-close') or contains(@id,'ad-close') or contains(text(),'Skip Ad')]")
            );
            if (adClose.isDisplayed()) {
                adClose.click();
                logger.info("Advertisement closed");
            }
        } catch (Exception e) {
            logger.debug("No advertisement found");
        }
    }
    
    /**
     * Search for a stock symbol
     * @param stockSymbol Stock symbol to search
     */
    public void searchStock(String stockSymbol) {
        logger.info("Searching for stock: {}", stockSymbol);
        
        try {
            // Try multiple search input locators
            WebElement searchField = findSearchInput();
            
            if (searchField != null) {
                // Clear and enter stock symbol
                searchField.clear();
                searchField.sendKeys(stockSymbol);
                
                // Wait for suggestions to appear
                Thread.sleep(2000);
                
                // Click on the first suggestion or press enter
                clickFirstSuggestion(stockSymbol);
                
                logger.info("Stock search completed for: {}", stockSymbol);
            } else {
                logger.error("Search input field not found");
                throw new RuntimeException("Unable to locate search input field");
            }
            
        } catch (Exception e) {
            logger.error("Error searching for stock {}: {}", stockSymbol, e.getMessage(), e);
            throw new RuntimeException("Stock search failed", e);
        }
    }
    
    /**
     * Find search input field using multiple strategies
     * @return WebElement of search input
     */
    private WebElement findSearchInput() {
        String[] searchLocators = {
            "//input[@placeholder='Search for stocks, indices, ETFs & more']",
            "//input[contains(@placeholder,'Search')]",
            "//input[@id='search-box']",
            "//input[contains(@class,'search')]",
            "//input[@name='search']"
        };
        
        for (String locator : searchLocators) {
            try {
                WebElement element = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath(locator))
                );
                logger.debug("Found search input using locator: {}", locator);
                return element;
            } catch (Exception e) {
                logger.debug("Search input not found with locator: {}", locator);
            }
        }
        
        return null;
    }
    
    /**
     * Click on the first search suggestion
     * @param stockSymbol Stock symbol being searched
     */
    private void clickFirstSuggestion(String stockSymbol) {
        try {
            // Wait for suggestions to appear
            WebElement suggestion = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class,'suggestion') or contains(@class,'dropdown')]" +
                            "//span[contains(text(),'" + stockSymbol + "')]")
                )
            );
            suggestion.click();
            logger.info("Clicked on suggestion for: {}", stockSymbol);
            
        } catch (Exception e) {
            logger.warn("No suggestions found, trying direct navigation");
            // If no suggestions, try direct navigation to get quotes page
            navigateToGetQuotes(stockSymbol);
        }
    }
    
    /**
     * Navigate directly to get quotes page for a stock
     * @param stockSymbol Stock symbol
     */
    public void navigateToGetQuotes(String stockSymbol) {
        String url = configReader.getNSEGetQuoteUrl() + "?symbol=" + stockSymbol;
        logger.info("Navigating directly to get quotes: {}", url);
        driver.get(url);
        
        // Wait for page to load
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Check if the page is loaded successfully
     * @return true if page is loaded
     */
    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.titleContains("NSE"));
            return true;
        } catch (Exception e) {
            logger.error("Page not loaded properly: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get current page title
     * @return Page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    /**
     * Get current page URL
     * @return Current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
