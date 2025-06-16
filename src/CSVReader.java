import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV檔案讀取器
 * CSV File Reader
 */
public class CSVReader {
    
    /**
     * 讀取股票資料CSV檔案
     * Read stock data CSV file
     */
    public static List<StockData> readStockDataFromCSV(String filePath) throws IOException {
        List<StockData> stockDataList = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        System.out.println("讀取CSV檔案: " + filePath);
        System.out.println("Reading CSV file: " + filePath);
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            int successCount = 0;
            int errorCount = 0;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                
                // 跳過標題行 Skip header row
                if (isFirstLine) {
                    System.out.println("CSV標題行 Header: " + line);
                    isFirstLine = false;
                    continue;
                }
                
                try {
                    String[] fields = parseCSVLine(line);
                    
                    if (fields.length < 12) {
                        System.err.println("第 " + lineNumber + " 行資料不完整，跳過 Line " + lineNumber + " incomplete, skipping");
                        errorCount++;
                        continue;
                    }
                    
                    StockData stockData = new StockData();
                    
                    // 解析各欄位 Parse fields
                    stockData.setId(Integer.parseInt(fields[0].trim()));
                    stockData.setStockName(fields[1].trim());
                    stockData.setClosePrice(new BigDecimal(fields[2].trim()));
                    stockData.setOpenPrice(new BigDecimal(fields[3].trim()));
                    stockData.setHighPrice(new BigDecimal(fields[4].trim()));
                    stockData.setLowPrice(new BigDecimal(fields[5].trim()));
                    stockData.setVolume(Long.parseLong(fields[6].trim()));
                    stockData.setMarketCap(new BigDecimal(fields[7].trim()));
                    
                    // 處理可能為空的欄位 Handle potentially null fields
                    stockData.setPeRatio(parseNullableBigDecimal(fields[8].trim()));
                    stockData.setDividendYield(parseNullableBigDecimal(fields[9].trim()));
                    stockData.setSector(fields[10].trim());
                    stockData.setTradeDate(LocalDate.parse(fields[11].trim(), dateFormatter));
                    
                    stockDataList.add(stockData);
                    successCount++;
                    
                    if (successCount % 10 == 0) {
                        System.out.println("已讀取 " + successCount + " 筆資料... Read " + successCount + " records...");
                    }
                    
                } catch (Exception e) {
                    System.err.println("第 " + lineNumber + " 行解析失敗 Line " + lineNumber + " parsing failed: " + e.getMessage());
                    System.err.println("資料內容 Data: " + line);
                    errorCount++;
                }
            }
            
            System.out.println("\n=== CSV讀取結果 CSV Reading Results ===");
            System.out.println("總處理行數 Total lines processed: " + (lineNumber - 1));
            System.out.println("成功讀取 Successfully read: " + successCount);
            System.out.println("失敗數量 Failed: " + errorCount);
            
        } catch (IOException e) {
            System.err.println("讀取檔案失敗 File reading failed: " + e.getMessage());
            throw e;
        }
        
        return stockDataList;
    }
    
    /**
     * 解析CSV行，處理逗號分隔
     * Parse CSV line, handle comma separation
     */
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // 添加最後一個欄位 Add the last field
        fields.add(currentField.toString());
        
        return fields.toArray(new String[0]);
    }
    
    /**
     * 解析可能為空的BigDecimal
     * Parse potentially null BigDecimal
     */
    private static BigDecimal parseNullableBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
