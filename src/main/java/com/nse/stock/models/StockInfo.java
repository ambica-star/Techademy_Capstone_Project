package com.nse.stock.models;

/**
 * Model class to represent stock information
 * Contains all relevant stock data extracted from NSE website
 */
public class StockInfo {
    
    private String symbol;
    private String companyName;
    private double currentPrice;
    private double priceChange;
    private double percentageChange;
    private double weekHigh52;
    private double weekLow52;
    private String volume;
    private String marketCap;
    private double purchasePrice;
    private double profitLoss;
    private double profitLossPercentage;
    
    // Default constructor
    public StockInfo() {
    }
    
    // Constructor with basic info
    public StockInfo(String symbol, String companyName, double currentPrice) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }
    
    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public double getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public double getPriceChange() {
        return priceChange;
    }
    
    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }
    
    public double getPercentageChange() {
        return percentageChange;
    }
    
    public void setPercentageChange(double percentageChange) {
        this.percentageChange = percentageChange;
    }
    
    public double getWeekHigh52() {
        return weekHigh52;
    }
    
    public void setWeekHigh52(double weekHigh52) {
        this.weekHigh52 = weekHigh52;
    }
    
    public double getWeekLow52() {
        return weekLow52;
    }
    
    public void setWeekLow52(double weekLow52) {
        this.weekLow52 = weekLow52;
    }
    
    public String getVolume() {
        return volume;
    }
    
    public void setVolume(String volume) {
        this.volume = volume;
    }
    
    public String getMarketCap() {
        return marketCap;
    }
    
    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }
    
    public double getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
        calculateProfitLoss();
    }
    
    public double getProfitLoss() {
        return profitLoss;
    }
    
    public void setProfitLoss(double profitLoss) {
        this.profitLoss = profitLoss;
    }
    
    public double getProfitLossPercentage() {
        return profitLossPercentage;
    }
    
    public void setProfitLossPercentage(double profitLossPercentage) {
        this.profitLossPercentage = profitLossPercentage;
    }
    
    /**
     * Calculate profit/loss based on current price and purchase price
     */
    public void calculateProfitLoss() {
        if (purchasePrice > 0 && currentPrice > 0) {
            profitLoss = currentPrice - purchasePrice;
            profitLossPercentage = (profitLoss / purchasePrice) * 100;
        }
    }
    
    /**
     * Check if the stock is in profit
     * @return true if profit, false if loss
     */
    public boolean isProfit() {
        return profitLoss > 0;
    }
    
    /**
     * Check if the stock is in loss
     * @return true if loss, false if profit
     */
    public boolean isLoss() {
        return profitLoss < 0;
    }
    
    /**
     * Get profit/loss status as string
     * @return "PROFIT", "LOSS", or "BREAK_EVEN"
     */
    public String getProfitLossStatus() {
        if (profitLoss > 0) {
            return "PROFIT";
        } else if (profitLoss < 0) {
            return "LOSS";
        } else {
            return "BREAK_EVEN";
        }
    }
    
    /**
     * Validate if all required stock information is available
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return symbol != null && !symbol.isEmpty() && 
               currentPrice > 0;
    }
    
    /**
     * Check if 52 week high/low data is available
     * @return true if available
     */
    public boolean has52WeekData() {
        return weekHigh52 > 0 && weekLow52 > 0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "StockInfo{symbol='%s', companyName='%s', currentPrice=%.2f, " +
            "priceChange=%.2f, percentageChange=%.2f%%, weekHigh52=%.2f, " +
            "weekLow52=%.2f, volume='%s', marketCap='%s', purchasePrice=%.2f, " +
            "profitLoss=%.2f, profitLossPercentage=%.2f%%, status='%s'}",
            symbol, companyName, currentPrice, priceChange, percentageChange,
            weekHigh52, weekLow52, volume, marketCap, purchasePrice,
            profitLoss, profitLossPercentage, getProfitLossStatus()
        );
    }
    
    /**
     * Get formatted current price with currency symbol
     * @return Formatted price string
     */
    public String getFormattedCurrentPrice() {
        return String.format("₹%.2f", currentPrice);
    }
    
    /**
     * Get formatted profit/loss with currency symbol
     * @return Formatted profit/loss string
     */
    public String getFormattedProfitLoss() {
        String sign = profitLoss >= 0 ? "+" : "";
        return String.format("%s₹%.2f (%.2f%%)", sign, profitLoss, profitLossPercentage);
    }
    
    /**
     * Get formatted 52 week range
     * @return Formatted range string
     */
    public String getFormatted52WeekRange() {
        if (has52WeekData()) {
            return String.format("₹%.2f - ₹%.2f", weekLow52, weekHigh52);
        }
        return "N/A";
    }
}
