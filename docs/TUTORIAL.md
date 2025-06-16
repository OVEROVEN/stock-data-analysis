# 詳細教學 Detailed Tutorial

本教學將一步一步帶你完成股票資料分析系統的安裝和使用。

## 🏁 環境準備

### 1. 安裝Java

#### macOS用戶：
```bash
# 使用Homebrew安裝
brew install openjdk@17

# 設定環境變數
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
```

#### Windows用戶：
1. 前往 [Oracle官網](https://www.oracle.com/java/technologies/downloads/) 下載JDK
2. 執行安裝程式
3. 設定環境變數JAVA_HOME

#### 驗證安裝：
```bash
java -version
javac -version
```

### 2. 安裝MySQL

#### 使用XAMPP（推薦給初學者）：
1. 下載 [XAMPP](https://www.apachefriends.org/)
2. 安裝並啟動MySQL服務
3. 開啟phpMyAdmin建立資料庫

#### 直接安裝MySQL：
1. 下載 [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
2. 安裝並設定root密碼
3. 啟動MySQL服務

## 📥 下載和設定專案

### 1. 下載專案
```bash
git clone https://github.com/[your-username]/stock-data-analysis.git
cd stock-data-analysis
```

### 2. 下載MySQL驅動程式
如果專案中沒有包含，請下載：
```bash
curl -L -o mysql-connector-j-9.1.0.jar \
  "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar"
```

### 3. 建立資料庫
在phpMyAdmin或MySQL命令列執行：
```sql
CREATE DATABASE stock_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 🔧 程式設定

### 1. 修改資料庫連線設定
編輯 `StockImporter.java` 第8-10行：
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/stock_db?useSSL=false&allowPublicKeyRetrieval=true";
private static final String DB_USER = "root";          // 你的MySQL用戶名
private static final String DB_PASSWORD = "your_password"; // 你的MySQL密碼
```

### 2. 準備資料檔案
將你的CSV檔案命名為 `stock_data_multiple_days.txt` 並放在專案根目錄，或修改程式中的檔案路徑。

## 🚀 編譯和執行

### 1. 編譯程式
```bash
javac -cp ".:mysql-connector-j-9.1.0.jar" StockImporter.java
```

### 2. 執行程式
```bash
java -cp ".:mysql-connector-j-9.1.0.jar" StockImporter
```

### 3. 檢查結果
程式執行完成後，可以在phpMyAdmin中查看 `stock_db.stock_data` 表格，確認：
- 所有原始資料已匯入
- `dsa_indicator` 欄位有數值
- `ma_5_days` 欄位有數值

## 🔍 故障排除

### 常見錯誤1：Java相關
```
Error: Could not find or load main class StockImporter
```
**解決方法：**
- 確認Java已正確安裝
- 確認在正確的目錄執行
- 檢查classpath設定

### 常見錯誤2：MySQL連線
```
SQLException: Access denied for user 'root'@'localhost'
```
**解決方法：**
- 檢查MySQL是否啟動
- 確認用戶名和密碼正確
- 檢查資料庫是否存在

### 常見錯誤3：檔案路徑
```
FileNotFoundException: stock_data_multiple_days.txt
```
**解決方法：**
- 確認檔案存在且路徑正確
- 檢查檔案權限
- 使用絕對路徑測試

### 常見錯誤4：CSV格式
```
NumberFormatException: For input string
```
**解決方法：**
- 檢查CSV檔案格式
- 確認數值欄位沒有空值
- 檢查日期格式是否為 YYYY-MM-DD

## 📊 理解輸出結果

### DSA指標解釋
- **正值 (0 < DSA)**：開盤價相對平衡
- **負值 (DSA < 0)**：開盤價偏向極值
- **零值 (DSA = 0)**：開盤價等於最高價或最低價

### 移動平均解釋
- 前5天：使用可用天數的平均（如第1天只用1天，第2天用2天平均）
- 第5天之後：使用5日移動平均

## 🎯 進階使用

### 1. 修改移動平均天數
修改 `calculateMA5()` 方法中的數字4（因為包含當天，所以5日平均是往前4天）：
```java
int start = Math.max(0, i - 9); // 改為10日移動平均
```

### 2. 新增其他技術指標
可以參考DSA的計算方式，新增其他指標如RSI、MACD等。

### 3. 匯出結果到Excel
可以新增方法將計算結果匯出為Excel檔案，方便進一步分析。

## 📝 學習建議

1. **理解程式流程**：按照README中的架構圖理解每個部分的作用
2. **修改參數實驗**：嘗試改變移動平均天數、資料庫名稱等
3. **新增功能**：試著加入新的計算指標或匯出功能
4. **效能優化**：學習如何提升大量資料的處理速度

有任何問題歡迎在GitHub Issues中提問！
