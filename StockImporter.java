import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

/**
 * 簡單的股票資料匯入程式
 * Simple Stock Data Import Program
 */
public class StockImporter {
    
    // 資料庫連線設定 Database connection settings
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stock_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    public static void main(String[] args) {
        StockImporter importer = new StockImporter();
        
        try {
            System.out.println("=== 股票資料匯入開始 ===");
            
            // 1. 連接資料庫
            importer.connectDatabase();
            
            // 2. 建立表格
            importer.createTable();
            
            // 3. 讀取並匯入CSV
            importer.importCSV("sample_data/stock_data_multiple_days.txt");
            
            // 4. 計算DSA指標
            importer.calculateDSA();
            
            // 5. 計算5日移動平均
            importer.calculateMA5();
            
            // 6. 顯示結果
            importer.showResults();
            
            System.out.println("\n=== 全部完成！ ===");
            
        } catch (Exception e) {
            System.err.println("錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Connection conn;
    
    // 連接資料庫
    private void connectDatabase() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        System.out.println("✓ 資料庫連接成功");
    }
    
    // 建立表格
    private void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS stock_data (
                id INT,
                stock_name VARCHAR(50),
                close_price DECIMAL(10,2),
                open_price DECIMAL(10,2),
                high_price DECIMAL(10,2),
                low_price DECIMAL(10,2),
                volume BIGINT,
                market_cap DECIMAL(15,2),
                pe_ratio DECIMAL(8,2),
                dividend_yield DECIMAL(5,2),
                setor VARCHAR(50),
                trade_date DATE,
                dsa_indicator DECIMAL(10,6),
                ma_5_days DECIMAL(10,2),
                PRIMARY KEY (id, trade_date)
            )
            """;
        
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        stmt.execute("DELETE FROM stock_data"); // 清空舊資料
        System.out.println("✓ 資料表準備完成");
    }
    
    // 匯入CSV
    private void importCSV(String filePath) throws Exception {
        System.out.println("讀取檔案: " + filePath);
        
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = br.readLine(); // 跳過標題行
        
        String sql = """
            INSERT INTO stock_data 
            (id, stock_name, close_price, open_price, high_price, low_price, 
             volume, market_cap, pe_ratio, dividend_yield, setor, trade_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int count = 0;
        
        while ((line = br.readLine()) != null) {
            String[] fields = line.split(",");
            
            pstmt.setInt(1, Integer.parseInt(fields[0]));           // id
            pstmt.setString(2, fields[1]);                          // stock_name
            pstmt.setBigDecimal(3, new BigDecimal(fields[2]));      // close_price
            pstmt.setBigDecimal(4, new BigDecimal(fields[3]));      // open_price
            pstmt.setBigDecimal(5, new BigDecimal(fields[4]));      // high_price
            pstmt.setBigDecimal(6, new BigDecimal(fields[5]));      // low_price
            pstmt.setLong(7, Long.parseLong(fields[6]));            // volume
            pstmt.setBigDecimal(8, new BigDecimal(fields[7]));      // market_cap
            pstmt.setBigDecimal(9, new BigDecimal(fields[8]));      // pe_ratio
            pstmt.setBigDecimal(10, new BigDecimal(fields[9]));     // dividend_yield
            pstmt.setString(11, fields[10]);                        // setor
            pstmt.setString(12, fields[11]);                        // trade_date
            
            pstmt.executeUpdate();
            count++;
        }
        
        br.close();
        System.out.println("✓ 匯入完成，共 " + count + " 筆資料");
    }
    
    // 計算DSA指標
    private void calculateDSA() throws SQLException {
        System.out.println("計算DSA指標...");
        
        String sql = """
            UPDATE stock_data 
            SET dsa_indicator = (
                CASE 
                    WHEN (POWER(high_price - open_price, 2) + POWER(open_price - low_price, 2)) = 0 
                    THEN 0
                    ELSE ((high_price - open_price) + (open_price - low_price)) / 
                         (POWER(high_price - open_price, 2) + POWER(open_price - low_price, 2))
                END
            )
            """;
        
        Statement stmt = conn.createStatement();
        int updated = stmt.executeUpdate(sql);
        System.out.println("✓ DSA計算完成，更新 " + updated + " 筆記錄");
    }
    
    // 計算5日移動平均
    private void calculateMA5() throws SQLException {
        System.out.println("計算5日移動平均...");
        
        // 取得所有股票代碼
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT id FROM stock_data ORDER BY id");
        
        List<Integer> stockIds = new ArrayList<>();
        while (rs.next()) {
            stockIds.add(rs.getInt("id"));
        }
        
        // 為每支股票計算移動平均
        for (int stockId : stockIds) {
            // 取得該股票所有交易日的收盤價，按日期排序
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT trade_date, close_price FROM stock_data WHERE id = ? ORDER BY trade_date"
            );
            pstmt.setInt(1, stockId);
            ResultSet priceRs = pstmt.executeQuery();
            
            List<String> dates = new ArrayList<>();
            List<BigDecimal> prices = new ArrayList<>();
            
            while (priceRs.next()) {
                dates.add(priceRs.getString("trade_date"));
                prices.add(priceRs.getBigDecimal("close_price"));
            }
            
            // 計算每一天的5日移動平均
            for (int i = 0; i < prices.size(); i++) {
                // 取前面最多5天（包含當天）
                int start = Math.max(0, i - 4);
                BigDecimal sum = BigDecimal.ZERO;
                int count = 0;
                
                for (int j = start; j <= i; j++) {
                    sum = sum.add(prices.get(j));
                    count++;
                }
                
                BigDecimal ma5 = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                
                // 更新資料庫
                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE stock_data SET ma_5_days = ? WHERE id = ? AND trade_date = ?"
                );
                updateStmt.setBigDecimal(1, ma5);
                updateStmt.setInt(2, stockId);
                updateStmt.setString(3, dates.get(i));
                updateStmt.executeUpdate();
            }
        }
        
        System.out.println("✓ 5日移動平均計算完成");
    }
    
    // 顯示結果
    private void showResults() throws SQLException {
        System.out.println("\n=== 結果展示 ===");
        
        // 顯示DSA統計
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("""
            SELECT 
                COUNT(*) as total,
                COUNT(CASE WHEN dsa_indicator > 0 THEN 1 END) as positive,
                COUNT(CASE WHEN dsa_indicator < 0 THEN 1 END) as negative,
                ROUND(MIN(dsa_indicator), 6) as min_dsa,
                ROUND(MAX(dsa_indicator), 6) as max_dsa
            FROM stock_data
            """);
        
        if (rs.next()) {
            System.out.println("DSA統計:");
            System.out.println("  總記錄數: " + rs.getInt("total"));
            System.out.println("  正值DSA: " + rs.getInt("positive"));
            System.out.println("  負值DSA: " + rs.getInt("negative"));
            System.out.println("  最小DSA: " + rs.getBigDecimal("min_dsa"));
            System.out.println("  最大DSA: " + rs.getBigDecimal("max_dsa"));
        }
        
        // 顯示台積電最新5筆資料
        System.out.println("\n台積電(2330)最新資料:");
        System.out.println("日期       | 收盤價  | DSA      | 5日均線");
        System.out.println("-----------|---------|----------|--------");
        
        PreparedStatement pstmt = conn.prepareStatement("""
            SELECT trade_date, close_price, 
                   ROUND(dsa_indicator, 6) as dsa, 
                   ROUND(ma_5_days, 2) as ma5
            FROM stock_data 
            WHERE id = 2330 
            ORDER BY trade_date DESC 
            LIMIT 5
            """);
        
        ResultSet tsmcRs = pstmt.executeQuery();
        while (tsmcRs.next()) {
            System.out.printf("%-10s | %-7.2f | %-8s | %-7.2f%n",
                tsmcRs.getString("trade_date"),
                tsmcRs.getBigDecimal("close_price"),
                tsmcRs.getBigDecimal("dsa"),
                tsmcRs.getBigDecimal("ma5")
            );
        }
    }
}
