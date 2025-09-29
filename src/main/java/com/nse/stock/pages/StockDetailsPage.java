package com.nse.stock.pages;

import com.nse.stock.models.StockInfo;
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
import java.util.List;

/**
 * Page Object Model for NSE Stock Details Page
 * Contains locators and methods for extracting stock information
 */
public class StockDetailsPage {
    
    private static final Logger logger = LogManager.getLogger(StockDetailsPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private ConfigReader configReader;
    
    // Stock Price Elements
    @FindBy(xpath = "//span[contains(@class,'price') or @id='lastPrice']")
    private WebElement currentPrice;
    
    @FindBy(xpath = "//span[contains(text(),'₹') or contains(@class,'price-value')]")
    private List<WebElement> priceElements;
    
    @FindBy(xpath = "//div[contains(@class,'stock-info')]//h1 | //h1[contains(@class,'symbol')]")
    private WebElement stockSymbol;
    
    @FindBy(xpath = "//div[contains(@class,'company-name')] | //span[contains(@class,'company')]")
    private WebElement companyName;
    
    // 52 Week High/Low Elements
    @FindBy(xpath = "//span[contains(text(),'52 Week High')]/following-sibling::span | " +
                   "//td[contains(text(),'52 Week High')]/following-sibling::td")
    private WebElement weekHigh52;
    
    @FindBy(xpath = "//span[contains(text(),'52 Week Low')]/following-sibling::span | " +
                   "//td[contains(text(),'52 Week Low')]/following-sibling::td")
    private WebElement weekLow52;
    
    // Change and Percentage Elements
    @FindBy(xpath = "//span[contains(@class,'change') or contains(@class,'pChange')]")
    private WebElement priceChange;
    
    @FindBy(xpath = "//span[contains(@class,'percent') or contains(text(),'%')]")
    private WebElement percentageChange;
    
    // Volume and Market Cap
    @FindBy(xpath = "//span[contains(text(),'Volume')]/following-sibling::span")
    private WebElement volume;
    
    @FindBy(xpath = "//span[contains(text(),'Market Cap')]/following-sibling::span")
    private WebElement marketCap;
    
    // Constructor
    public StockDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.configReader = ConfigReader.getInstance();
        this.wait = new WebDriverWait(driver, configReader.getExplicitWait());
        PageFactory.initElements(driver, this);
        logger.info("Stock Details Page initialized");
    }
    
    /**
     * Wait for stock details page to load
     */
    public void waitForPageLoad() {
        try {
            // Wait for any price element to be visible
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[contains(@class,'price')]")
                ),
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[contains(text(),'₹')]")
                ),
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(@class,'stock-price')]")
                )
            ));
            
            // Additional wait for dynamic content
            Thread.sleep(3000);
            logger.info("Stock details page loaded successfully");
            
        } catch (Exception e) {
            logger.error("Error waiting for page load: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Extract complete stock information
     * @return StockInfo object with all extracted data
     */
    public StockInfo extractStockInfo() {
        logger.info("Extracting stock information from page");
        
        StockInfo stockInfo = new StockInfo();
        
        try {
            // Extract stock symbol
            stockInfo.setSymbol(extractStockSymbol());
            
            // Extract company name
            stockInfo.setCompanyName(extractCompanyName());
            
            // Extract current price
            stockInfo.setCurrentPrice(extractCurrentPrice());
            
            // Extract price change
            stockInfo.setPriceChange(extractPriceChange());
            
            // Extract percentage change
            stockInfo.setPercentageChange(extractPercentageChange());
            
            // Extract 52 week high
            stockInfo.setWeekHigh52(extract52WeekHigh());
            
            // Extract 52 week low
            stockInfo.setWeekLow52(extract52WeekLow());
            
            // Extract volume
            stockInfo.setVolume(extractVolume());
            
            // Extract market cap
            stockInfo.setMarketCap(extractMarketCap());
            
            logger.info("Stock information extracted successfully for: {}", stockInfo.getSymbol());
            
        } catch (Exception e) {
            logger.error("Error extracting stock information: {}", e.getMessage(), e);
        }
        
        return stockInfo;
    }
    
    /**
     * Extract stock symbol from page
     * @return Stock symbol
     */
    public String extractStockSymbol() {
        try {
            String[] symbolLocators = {
                "//h1[contains(@class,'symbol')] | //span[contains(@class,'symbol')]",
                "//div[contains(@class,'stock-info')]//h1",
                "//title | //h1"
            };
            
            for (String locator : symbolLocators) {
                try {
                    WebElement element = driver.findElement(By.xpath(locator));
                    String text = element.getText().trim();
                    if (!text.isEmpty()) {
                        // Extract symbol from text (usually first word)
                        String symbol = text.split("\\s+")[0];
                        logger.debug("Extracted stock symbol: {}", symbol);
                        return symbol;
                    }
                } catch (Exception e) {
                    logger.debug("Symbol not found with locator: {}", locator);
                }
            }
            
            // Fallback: extract from URL
            String url = driver.getCurrentUrl();
            if (url.contains("symbol=")) {
                String symbol = url.substring(url.indexOf("symbol=") + 7);
                if (symbol.contains("&")) {
                    symbol = symbol.substring(0, symbol.indexOf("&"));
                }
                logger.debug("Extracted symbol from URL: {}", symbol);
                return symbol;
            }
            
        } catch (Exception e) {
            logger.error("Error extracting stock symbol: {}", e.getMessage());
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Extract company name from page
     * @return Company name
     */
    public String extractCompanyName() {
        try {
            String[] nameLocators = {
                "//div[contains(@class,'company-name')]",
                "//span[contains(@class,'company')]",
                "//h2[contains(@class,'company')] | //h3[contains(@class,'company')]"
            };
            
            for (String locator : nameLocators) {
                try {
                    WebElement element = driver.findElement(By.xpath(locator));
                    String name = element.getText().trim();
                    if (!name.isEmpty()) {
                        logger.debug("Extracted company name: {}", name);
                        return name;
                    }
                } catch (Exception e) {
                    logger.debug("Company name not found with locator: {}", locator);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error extracting company name: {}", e.getMessage());
        }
        
        return "Unknown Company";
    }
    
    /**
     * Extract current stock price
     * @return Current price as double
     */
    public double extractCurrentPrice() {
        try {
            String[] priceLocators = {
                "//span[contains(@class,'price') and contains(text(),'₹')] | //span[@id='lastPrice']",
                "//div[contains(@class,'price')]//span[contains(text(),'₹')]",
                "//*[contains(@class,'current-price')] | //*[contains(@class,'ltp')]",
                "//span[contains(text(),'₹')]"
            };
            
            for (String locator : priceLocators) {
                try {
                    List<WebElement> elements = driver.findElements(By.xpath(locator));
                    for (WebElement element : elements) {
                        String priceText = element.getText().trim();
                        if (priceText.contains("₹")) {
                            double price = parsePrice(priceText);
                            if (price > 0) {
                                logger.debug("Extracted current price: {}", price);
                                return price;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Price not found with locator: {}", locator);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error extracting current price: {}", e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Extract price change
     * @return Price change as double
     */
    public double extractPriceChange() {
        try {
            String[] changeLocators = {
                "//span[contains(@class,'change') and not(contains(@class,'percent'))]",
                "//span[contains(@class,'pChange')]",
                "//*[contains(@class,'price-change')]"
            };
            
            for (String locator : changeLocators) {
                try {
                    WebElement element = driver.findElement(By.xpath(locator));
                    String changeText = element.getText().trim();
                    if (!changeText.isEmpty() && !changeText.contains("%")) {
                        double change = parsePrice(changeText);
                        logger.debug("Extracted price change: {}", change);
                        return change;
                    }
                } catch (Exception e) {
                    logger.debug("Price change not found with locator: {}", locator);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error extracting price change: {}", e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Extract percentage change
     * @return Percentage change as double
     */
    public double extractPercentageChange() {
        try {
            String[] percentLocators = {
                "//span[contains(@class,'percent') or contains(text(),'%')]",
                "//span[contains(@class,'pChange') and contains(text(),'%')]"
            };
            
            for (String locator : percentLocators) {
                try {
                    WebElement element = driver.findElement(By.xpath(locator));
                    String percentText = element.getText().trim();
                    if (percentText.contains("%")) {
                        double percent = parsePercentage(percentText);
                        logger.debug("Extracted percentage change: {}%", percent);
                        return percent;
                    }
                } catch (Exception e) {
                    logger.debug("Percentage change not found with locator: {}", locator);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error extracting percentage change: {}", e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Extract 52 week high price
     * @return 52 week high as double
     */
    public double extract52WeekHigh() {
        return extractWeekPrice("52 Week High", "52WH", "High");
    }
    
    /**
     * Extract 52 week low price
     * @return 52 week low as double
     */
    public double extract52WeekLow() {
        return extractWeekPrice("52 Week Low", "52WL", "Low");
    }
    
    /**
     * Extract 52 week price (high or low)
     * @param label Label to search for
     * @param shortLabel Short label alternative
     * @param type Type (High/Low)
     * @return Price as double
     */
    private double extractWeekPrice(String label, String shortLabel, String type) {
        try {
            String[] weekLocators = {
                String.format("//span[contains(text(),'%s')]/following-sibling::span", label),
                String.format("//td[contains(text(),'%s')]/following-sibling::td", label),
                String.format("//div[contains(text(),'%s')]//following::span[contains(text(),'₹')]", label),
                String.format("//*[contains(text(),'%s')]", shortLabel)
            };
            
            for (String locator : weekLocators) {
                try {
                    WebElement element = driver.findElement(By.xpath(locator));
                    String priceText = element.getText().trim();
                    if (!priceText.isEmpty()) {
                        double price = parsePrice(priceText);
                        if (price > 0) {
                            logger.debug("Extracted 52 week {}: {}", type, price);
                            return price;
                        }
                    }
                } catch (Exception e) {
                    logger.debug("52 week {} not found with locator: {}", type, locator);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error extracting 52 week {}: {}", type, e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Extract volume
     * @return Volume as string
     */
    public String extractVolume() {
        try {
            String[] volumeLocators = {
                "//span[contains(text(),'Volume')]/following-sibling::span",
                "//td[contains(text(),'Volume')]/following-sibling::td",
                "//*[contains(@class,'volume')]"
            };
            
            for (String locator : volumeLocators) {
                try {
                    WebElement element = driver.findElement(By.xpath(locator));
                    String volume = element.getText().trim();
                    if (!volume.isEmpty()) {
                        logger.debug("Extracted volume: {}", volume);
                        return volume;
                    }
                } catch (Exception e) {
                    logger.debug("Volume not found with locator: {}", locator);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error extracting volume: {}", e.getMessage());
        }
        
        return "N/A";
    }
    
    /**
     * Extract market cap
     * @return Market cap as string
     */
    public String extractMarketCap() {
        try {
            String[] marketCapLocators = {
                "//span[contains(text(),'Market Cap')]/following-sibling::span",
                "//td[contains(text(),'Market Cap')]/following-sibling::td",
                "//*[contains(@class,'market-cap')]"
            };
            
            for (String locator : marketCapLocators) {
                try {
                    WebElement element = driver.findElement(By.xpath(locator));
                    String marketCap = element.getText().trim();
                    if (!marketCap.isEmpty()) {
                        logger.debug("Extracted market cap: {}", marketCap);
                        return marketCap;
                    }
                } catch (Exception e) {
                    logger.debug("Market cap not found with locator: {}", locator);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error extracting market cap: {}", e.getMessage());
        }
        
        return "N/A";
    }
    
    /**
     * Parse price from text
     * @param priceText Price text containing currency symbols
     * @return Parsed price as double
     */
    private double parsePrice(String priceText) {
        try {
            // Remove currency symbols and commas
            String cleanPrice = priceText.replaceAll("[₹,\\s+()-]", "");
            
            // Handle negative values
            boolean isNegative = priceText.contains("-") || priceText.contains("(");
            
            // Extract numeric value
            String numericPart = cleanPrice.replaceAll("[^0-9.]", "");
            
            if (!numericPart.isEmpty()) {
                double price = Double.parseDouble(numericPart);
                return isNegative ? -price : price;
            }
            
        } catch (NumberFormatException e) {
            logger.warn("Unable to parse price: {}", priceText);
        }
        
        return 0.0;
    }
    
    /**
     * Parse percentage from text
     * @param percentText Percentage text
     * @return Parsed percentage as double
     */
    private double parsePercentage(String percentText) {
        try {
            // Remove % symbol and spaces
            String cleanPercent = percentText.replaceAll("[%\\s+()-]", "");
            
            // Handle negative values
            boolean isNegative = percentText.contains("-") || percentText.contains("(");
            
            // Extract numeric value
            String numericPart = cleanPercent.replaceAll("[^0-9.]", "");
            
            if (!numericPart.isEmpty()) {
                double percent = Double.parseDouble(numericPart);
                return isNegative ? -percent : percent;
            }
            
        } catch (NumberFormatException e) {
            logger.warn("Unable to parse percentage: {}", percentText);
        }
        
        return 0.0;
    }
    
    /**
     * Check if stock information is displayed on page
     * @return true if stock info is visible
     */
    public boolean isStockInfoDisplayed() {
        try {
            // Check if any price element is visible
            return !driver.findElements(By.xpath("//span[contains(text(),'₹')] | //*[contains(@class,'price')]")).isEmpty();
        } catch (Exception e) {
            logger.error("Error checking if stock info is displayed: {}", e.getMessage());
            return false;
        }
    }
}
