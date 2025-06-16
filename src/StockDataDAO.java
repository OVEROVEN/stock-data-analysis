import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * 股票數據資料庫操作類
 * Stock Data Database Operations Class
 */
public class StockDataDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stock_db?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection connection;

    /**
     * 建立資料庫連接
     * Establish database connection
     */
    public void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ 資料庫連接成功 Database connected successfully");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC驅動程式未找到 MySQL JDBC driver not found", e);
        }
    }

    /**
     * 關閉資料庫連接
     * Close database connection
     */
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("✓ 資料庫連接已關閉 Database connection closed");
        }
    }

    /**
     * 建立資料表
     * Create table
     */
    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS stock_data (
                id INT NOT NULL COMMENT '股票代碼',
                stock_name VARCHAR(50) NOT NULL COMMENT '股票名稱',
                close_price DECIMAL(10, 2) NOT NULL COMMENT '收盤價',
                open_price DECIMAL(10, 2) NOT NULL COMMENT '開盤價',
                high_price DECIMAL(10, 2) NOT NULL COMMENT '最高價',
                low_price DECIMAL(10, 2) NOT NULL COMMENT '最低價',
                volume BIGINT NOT NULL COMMENT '成交量',
                market_cap DECIMAL(15, 2) NOT NULL COMMENT '市值',
                pe_ratio DECIMAL(8, 2) COMMENT '本益比',
                dividend_yield DECIMAL(5, 2) COMMENT '股息殖利率',
                setor VARCHAR(50) COMMENT '行業別',
                trade_date DATE NOT NULL COMMENT '交易日期',
                dsa_indicator DECIMAL(10, 6) COMMENT 'DSA指標',
                ma_5_days DECIMAL(10, 2) COMMENT '5日移動平均',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (id, trade_date),
                INDEX idx_stock_date (id, trade_date),
                INDEX idx_trade_date (trade_date),
                INDEX idx_sector (setor)
            ) ENGINE=InnoDB COMMENT='股票交易資料表'
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ 資料表建立成功 Table created successfully");
        }
    }

    /**
     * 清空資料表
     * Clear table data
     */
    public void clearTable() throws SQLException {
        String sql = "DELETE FROM stock_data";
        try (Statement stmt = connection.createStatement()) {
            int deletedRows = stmt.executeUpdate(sql);
            System.out.println("✓ 清空了 " + deletedRows + " 筆舊資料 Cleared " + deletedRows + " old records");
        }
    }

    /**
     * 插入股票資料
     * Insert stock data
     */
    public void insertStockData(StockData stockData) throws SQLException {
        String sql = """
            INSERT INTO stock_data 
            (id, stock_name, close_price, open_price, high_price, low_price, 
             volume, market_cap, pe_ratio, dividend_yield, setor, trade_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            stock_name = VALUES(stock_name),
            close_price = VALUES(close_price),
            open_price = VALUES(open_price),
            high_price = VALUES(high_price),
            low_price = VALUES(low_price),
            volume = VALUES(volume),
            market_cap = VALUES(market_cap),
            pe_ratio = VALUES(pe_ratio),
            dividend_yield = VALUES(dividend_yield),
            setor = VALUES(setor)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, stockData.getId());
            pstmt.setString(2, stockData.getStockName());
            pstmt.setBigDecimal(3, stockData.getClosePrice());
            pstmt.setBigDecimal(4, stockData.getOpenPrice());
            pstmt.setBigDecimal(5, stockData.getHighPrice());
            pstmt.setBigDecimal(6, stockData.getLowPrice());
            pstmt.setLong(7, stockData.getVolume());
            pstmt.setBigDecimal(8, stockData.getMarketCap());
            pstmt.setBigDecimal(9, stockData.getPeRatio());
            pstmt.setBigDecimal(10, stockData.getDividendYield());
            pstmt.setString(11, stockData.getSector());
            pstmt.setDate(12, Date.valueOf(stockData.getTradeDate()));

            pstmt.executeUpdate();
        }
    }

    /**
     * 批次插入股票資料
     * Batch insert stock data
     */
    public void insertStockDataBatch(List<StockData> stockDataList) throws SQLException {
        String sql = """
            INSERT INTO stock_data 
            (id, stock_name, close_price, open_price, high_price, low_price, 
             volume, market_cap, pe_ratio, dividend_yield, setor, trade_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            stock_name = VALUES(stock_name),
            close_price = VALUES(close_price),
            open_price = VALUES(open_price),
            high_price = VALUES(high_price),
            low_price = VALUES(low_price),
            volume = VALUES(volume),
            market_cap = VALUES(market_cap),
            pe_ratio = VALUES(pe_ratio),
            dividend_yield = VALUES(dividend_yield),
            setor = VALUES(setor)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            for (StockData stockData : stockDataList) {
                pstmt.setInt(1, stockData.getId());
                pstmt.setString(2, stockData.getStockName());
                pstmt.setBigDecimal(3, stockData.getClosePrice());
                pstmt.setBigDecimal(4, stockData.getOpenPrice());
                pstmt.setBigDecimal(5, stockData.getHighPrice());
                pstmt.setBigDecimal(6, stockData.getLowPrice());
                pstmt.setLong(7, stockData.getVolume());
                pstmt.setBigDecimal(8, stockData.getMarketCap());
                pstmt.setBigDecimal(9, stockData.getPeRatio());
                pstmt.setBigDecimal(10, stockData.getDividendYield());
                pstmt.setString(11, stockData.getSector());
                pstmt.setDate(12, Date.valueOf(stockData.getTradeDate()));

                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);

            System.out.println("✓ 批次插入完成，共處理 " + results.length + " 筆資料 Batch insert completed, processed " + results.length + " records");
        } catch (SQLException e) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw e;
        }
    }

    /**
     * 更新DSA指標
     * Update DSA indicators
     */
    public void updateDSAIndicators() throws SQLException {
        System.out.println("計算DSA指標中... Calculating DSA indicators...");

        String selectSql = "SELECT id, trade_date, open_price, high_price, low_price FROM stock_data ORDER BY id, trade_date";
        String updateSql = "UPDATE stock_data SET dsa_indicator = ? WHERE id = ? AND trade_date = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateSql);
             ResultSet rs = selectStmt.executeQuery()) {

            connection.setAutoCommit(false);
            int updateCount = 0;

            while (rs.next()) {
                int id = rs.getInt("id");
                Date tradeDate = rs.getDate("trade_date");
                BigDecimal openPrice = rs.getBigDecimal("open_price");
                BigDecimal highPrice = rs.getBigDecimal("high_price");
                BigDecimal lowPrice = rs.getBigDecimal("low_price");

                // 計算DSA Calculate DSA
                BigDecimal dsa = calculateDSA(openPrice, highPrice, lowPrice);

                updateStmt.setBigDecimal(1, dsa);
                updateStmt.setInt(2, id);
                updateStmt.setDate(3, tradeDate);
                updateStmt.addBatch();

                updateCount++;
                if (updateCount % 100 == 0) {
                    updateStmt.executeBatch();
                    System.out.println("已處理 " + updateCount + " 筆DSA計算... Processed " + updateCount + " DSA calculations...");
                }
            }

            updateStmt.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);

            System.out.println("✓ DSA指標計算完成，共更新 " + updateCount + " 筆資料 DSA calculation completed, updated " + updateCount + " records");
        } catch (SQLException e) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw e;
        }
    }

    /**
     * 計算DSA指標
     * Calculate DSA indicator
     */
    private BigDecimal calculateDSA(BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice) {
        if (openPrice == null || highPrice == null || lowPrice == null) {
            return null;
        }

        BigDecimal highMinusOpen = highPrice.subtract(openPrice);
        BigDecimal openMinusLow = openPrice.subtract(lowPrice);
        
        BigDecimal numerator = highMinusOpen.add(openMinusLow);
        BigDecimal denominator = highMinusOpen.multiply(highMinusOpen)
                                            .add(openMinusLow.multiply(openMinusLow));
        
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return numerator.divide(denominator, 6, RoundingMode.HALF_UP);
    }

    /**
     * 更新5日移動平均
     * Update 5-day moving averages
     */
    public void updateMovingAverages() throws SQLException {
        System.out.println("計算5日移動平均中... Calculating 5-day moving averages...");

        // 取得所有股票代碼 Get all stock IDs
        String stockIdsSql = "SELECT DISTINCT id FROM stock_data ORDER BY id";
        List<Integer> stockIds = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(stockIdsSql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stockIds.add(rs.getInt("id"));
            }
        }

        System.out.println("處理 " + stockIds.size() + " 支股票的移動平均... Processing moving averages for " + stockIds.size() + " stocks...");

        String selectSql = "SELECT trade_date, close_price FROM stock_data WHERE id = ? ORDER BY trade_date";
        String updateSql = "UPDATE stock_data SET ma_5_days = ? WHERE id = ? AND trade_date = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {

            connection.setAutoCommit(false);
            int processedStocks = 0;

            for (Integer stockId : stockIds) {
                selectStmt.setInt(1, stockId);

                List<BigDecimal> prices = new ArrayList<>();
                List<Date> dates = new ArrayList<>();

                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        prices.add(rs.getBigDecimal("close_price"));
                        dates.add(rs.getDate("trade_date"));
                    }
                }

                // 計算每一天的5日移動平均 Calculate 5-day MA for each day
                for (int i = 0; i < prices.size(); i++) {
                    int startIndex = Math.max(0, i - 4);
                    BigDecimal sum = BigDecimal.ZERO;
                    int count = 0;

                    for (int j = startIndex; j <= i; j++) {
                        sum = sum.add(prices.get(j));
                        count++;
                    }

                    BigDecimal ma5 = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

                    updateStmt.setBigDecimal(1, ma5);
                    updateStmt.setInt(2, stockId);
                    updateStmt.setDate(3, dates.get(i));
                    updateStmt.addBatch();
                }

                processedStocks++;
                if (processedStocks % 5 == 0) {
                    updateStmt.executeBatch();
                    System.out.println("已處理 " + processedStocks + " 支股票... Processed " + processedStocks + " stocks...");
                }
            }

            updateStmt.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);

            System.out.println("✓ 5日移動平均計算完成 5-day moving average calculation completed");
        } catch (SQLException e) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw e;
        }
    }

    /**
     * 取得記錄總數
     * Get total record count
     */
    public int getTotalRecords() throws SQLException {
        String sql = "SELECT COUNT(*) FROM stock_data";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /**
     * 取得範例資料用於驗證
     * Get sample data for verification
     */
    public List<StockData> getSampleData(int limit) throws SQLException {
        String sql = """
            SELECT id, stock_name, trade_date, close_price, open_price, high_price, low_price,
                   dsa_indicator, ma_5_days
            FROM stock_data 
            ORDER BY trade_date DESC, id 
            LIMIT ?
            """;

        List<StockData> sampleData = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StockData stockData = new StockData();
                    stockData.setId(rs.getInt("id"));
                    stockData.setStockName(rs.getString("stock_name"));
                    stockData.setTradeDate(rs.getDate("trade_date").toLocalDate());
                    stockData.setClosePrice(rs.getBigDecimal("close_price"));
                    stockData.setOpenPrice(rs.getBigDecimal("open_price"));
                    stockData.setHighPrice(rs.getBigDecimal("high_price"));
                    stockData.setLowPrice(rs.getBigDecimal("low_price"));
                    stockData.setDsaIndicator(rs.getBigDecimal("dsa_indicator"));
                    stockData.setMa5Days(rs.getBigDecimal("ma_5_days"));

                    sampleData.add(stockData);
                }
            }
        }

        return sampleData;
    }
}
