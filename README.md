# 股票資料分析系統 Stock Data Analysis System

一個簡單易用的Java應用程式，可以將股票CSV資料匯入MySQL資料庫，並自動計算DSA指標和移動平均線。

## 📋 功能特色 Features

- ✅ CSV檔案自動匯入MySQL資料庫
- ✅ DSA指標計算 (支援正值、負值、零值)
- ✅ 5日移動平均線計算 (不足5日時使用可用天數)
- ✅ 資料統計分析和結果展示
- ✅ 完整的錯誤處理機制

## 🎯 DSA指標說明

DSA (Dynamic Stock Analysis) 指標公式：
```
DSA = [(最高價 - 開盤價) + (開盤價 - 最低價)] / [(最高價 - 開盤價)² + (開盤價 - 最低價)²]
```

**指標意義：**
- **正值**：開盤價相對平衡，位於高低價中間
- **負值**：開盤價偏向極值（接近最高價或最低價）
- **零值**：開盤價等於最高價或最低價

## 🛠️ 環境需求 Requirements

- Java 8 或以上版本
- MySQL 5.7 或以上版本
- Maven (可選，用於依賴管理)

## 📦 快速開始 Quick Start

### 1. 下載專案
```bash
git clone https://github.com/[your-username]/stock-data-analysis.git
cd stock-data-analysis
```

### 2. 準備環境
- 安裝並啟動MySQL
- 下載MySQL Connector/J驅動程式

### 3. 建立資料庫
```sql
CREATE DATABASE stock_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 編譯和執行
```bash
# 編譯
javac -cp ".:mysql-connector-j-9.1.0.jar" StockImporter.java

# 執行
java -cp ".:mysql-connector-j-9.1.0.jar" StockImporter
```

## 📁 專案結構 Project Structure

```
stock-data-analysis/
├── README.md                           # 專案說明
├── StockImporter.java                  # 主程式 (簡化版本)
├── mysql-connector-j-9.1.0.jar       # MySQL驅動程式
├── sample_data/
│   └── stock_data_multiple_days.txt   # 範例資料檔案
├── src/                               # 進階版本原始碼
│   ├── StockData.java                 # 股票資料實體類
│   ├── StockDataDAO.java              # 資料存取物件
│   └── CSVReader.java                 # CSV讀取器
├── sql/
│   ├── create_database.sql            # 建立資料庫腳本
│   └── create_table.sql               # 建立資料表腳本
├── docs/
│   ├── TUTORIAL.md                    # 詳細教學
│   └── API.md                         # API說明文件
└── LICENSE                            # 授權條款
```

## 📊 範例資料格式 Sample Data Format

CSV檔案應包含以下欄位：
```csv
id,stock_name,close_price,open_price,high_price,low_price,volume,market_cap,pe_ratio,dividend_yield,setor,trade_date
2330,台積電,568.00,565.00,572.00,563.00,25680000,14720000.00,18.5,2.1,半導體,2025-06-09
```

## 🔧 設定說明 Configuration

修改 `StockImporter.java` 中的資料庫連線設定：
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/stock_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";
```

## 📈 執行結果範例 Sample Output

```
=== 股票資料匯入開始 ===
✓ 資料庫連接成功
✓ 資料表準備完成
✓ 匯入完成，共 60 筆資料
✓ DSA指標計算完成，更新 60 筆記錄
✓ 5日移動平均計算完成

=== 結果展示 ===
DSA統計:
  總記錄數: 60
  正值DSA: 35
  負值DSA: 25
  最小DSA: -0.123456
  最大DSA: 0.234567

台積電(2330)最新資料:
日期       | 收盤價  | DSA      | 5日均線
-----------|---------|----------|--------
2025-06-09 | 568.00  | 0.123456 | 570.20
2025-06-08 | 572.50  | -0.087654| 569.80
```

## 🤝 貢獻指南 Contributing

1. Fork 這個專案
2. 建立你的功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的變更 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 開啟一個 Pull Request

## 📝 版本歷史 Changelog

### v1.0.0 (2025-06-16)
- 初始版本發布
- 基本CSV匯入功能
- DSA指標計算
- 5日移動平均計算

## 📄 授權條款 License

本專案採用 MIT 授權條款 - 詳見 [LICENSE](LICENSE) 檔案

## 👥 作者 Authors

- **你的名字** - *初始工作* - [YourGitHub](https://github.com/yourusername)

## 🙏 致謝 Acknowledgments

- 感謝 Claude AI 協助程式開發和架構設計
- 感謝所有提供回饋和建議的使用者

## 📞 聯絡方式 Contact

- GitHub Issues: [專案問題回報](https://github.com/[your-username]/stock-data-analysis/issues)
- Email: your.email@example.com

## 🔗 相關連結 Related Links

- [MySQL Connector/J 下載](https://dev.mysql.com/downloads/connector/j/)
- [Java 官方文件](https://docs.oracle.com/en/java/)
- [MySQL 官方文件](https://dev.mysql.com/doc/)
