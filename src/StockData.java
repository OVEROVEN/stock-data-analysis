import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票數據實體類
 * Stock Data Entity Class
 */
public class StockData {
    private int id;                    // 股票代碼 Stock ID
    private String stockName;          // 股票名稱 Stock Name
    private BigDecimal closePrice;     // 收盤價 Close Price
    private BigDecimal openPrice;      // 開盤價 Open Price
    private BigDecimal highPrice;      // 最高價 High Price
    private BigDecimal lowPrice;       // 最低價 Low Price
    private long volume;               // 成交量 Volume
    private BigDecimal marketCap;      // 市值 Market Cap
    private BigDecimal peRatio;        // 本益比 PE Ratio
    private BigDecimal dividendYield;  // 股息殖利率 Dividend Yield
    private String sector;             // 行業別 Sector
    private LocalDate tradeDate;       // 交易日期 Trade Date
    private BigDecimal dsaIndicator;   // DSA指標 DSA Indicator
    private BigDecimal ma5Days;        // 5日移動平均 5-Day Moving Average

    // 建構子 Constructor
    public StockData() {}

    public StockData(int id, String stockName, BigDecimal closePrice, BigDecimal openPrice,
                    BigDecimal highPrice, BigDecimal lowPrice, long volume, BigDecimal marketCap,
                    BigDecimal peRatio, BigDecimal dividendYield, String sector, LocalDate tradeDate) {
        this.id = id;
        this.stockName = stockName;
        this.closePrice = closePrice;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
        this.marketCap = marketCap;
        this.peRatio = peRatio;
        this.dividendYield = dividendYield;
        this.sector = sector;
        this.tradeDate = tradeDate;
    }

    // Getter和Setter方法 Getter and Setter methods
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public BigDecimal getClosePrice() { return closePrice; }
    public void setClosePrice(BigDecimal closePrice) { this.closePrice = closePrice; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }

    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public BigDecimal getMarketCap() { return marketCap; }
    public void setMarketCap(BigDecimal marketCap) { this.marketCap = marketCap; }

    public BigDecimal getPeRatio() { return peRatio; }
    public void setPeRatio(BigDecimal peRatio) { this.peRatio = peRatio; }

    public BigDecimal getDividendYield() { return dividendYield; }
    public void setDividendYield(BigDecimal dividendYield) { this.dividendYield = dividendYield; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getDsaIndicator() { return dsaIndicator; }
    public void setDsaIndicator(BigDecimal dsaIndicator) { this.dsaIndicator = dsaIndicator; }

    public BigDecimal getMa5Days() { return ma5Days; }
    public void setMa5Days(BigDecimal ma5Days) { this.ma5Days = ma5Days; }

    /**
     * 計算DSA指標
     * Calculate DSA Indicator
     * DSA = [(High - Open) + (Open - Low)] / [(High - Open)² + (Open - Low)²]
     */
    public BigDecimal calculateDSA() {
        if (highPrice == null || openPrice == null || lowPrice == null) {
            return null;
        }

        BigDecimal highMinusOpen = highPrice.subtract(openPrice);
        BigDecimal openMinusLow = openPrice.subtract(lowPrice);
        
        // 分子 Numerator: (High - Open) + (Open - Low)
        BigDecimal numerator = highMinusOpen.add(openMinusLow);
        
        // 分母 Denominator: (High - Open)² + (Open - Low)²
        BigDecimal denominator = highMinusOpen.multiply(highMinusOpen)
                                            .add(openMinusLow.multiply(openMinusLow));
        
        // 避免除以零 Avoid division by zero
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        // 計算DSA，保留6位小數 Calculate DSA with 6 decimal places
        return numerator.divide(denominator, 6, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public String toString() {
        return String.format("StockData{id=%d, stockName='%s', tradeDate=%s, closePrice=%s, dsaIndicator=%s}",
                           id, stockName, tradeDate, closePrice, dsaIndicator);
    }
}
