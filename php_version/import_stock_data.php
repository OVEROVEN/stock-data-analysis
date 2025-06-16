<?php
// 股票數據匯入腳本 - 實際執行版本
// Stock Data Import Script - Actual Execution Version

echo "=== 股票數據匯入開始 Stock Data Import Started ===\n";

// 資料庫連線設定 Database Configuration
$host = 'localhost';
$dbname = 'stock_db';
$username = 'root';
$password = '';

try {
    // 連接資料庫 Connect to Database
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    echo "✓ 資料庫連接成功 Database connected successfully\n";

    // 清空現有資料（如果需要重新匯入）
    // Clear existing data (if need to re-import)
    echo "清空現有資料... Clearing existing data...\n";
    $pdo->exec("DELETE FROM stock_data");
    echo "✓ 現有資料已清空 Existing data cleared\n";

    // 讀取CSV檔案 Read CSV File
    $csvFile = '/Users/afu/Desktop/stock_data_multiple_days.txt';
    echo "讀取檔案: $csvFile\n";
    
    if (!file_exists($csvFile)) {
        throw new Exception("找不到CSV檔案 CSV file not found: $csvFile");
    }

    $handle = fopen($csvFile, 'r');
    if (!$handle) {
        throw new Exception("無法開啟檔案 Cannot open file: $csvFile");
    }

    // 跳過標題行 Skip header row
    $header = fgetcsv($handle);
    echo "CSV標題行: " . implode(', ', $header) . "\n";

    // 準備SQL語句 Prepare SQL Statement
    $sql = "INSERT INTO stock_data (
        id, stock_name, close_price, open_price, high_price, low_price, 
        volume, market_cap, pe_ratio, dividend_yield, setor, trade_date
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    $stmt = $pdo->prepare($sql);

    // 匯入資料 Import Data
    $rowCount = 0;
    $successCount = 0;
    $errorCount = 0;

    echo "開始匯入資料... Starting data import...\n";
    
    while (($row = fgetcsv($handle)) !== FALSE) {
        $rowCount++;
        
        try {
            // 驗證資料完整性 Validate data integrity
            if (count($row) < 12) {
                echo "第 $rowCount 行資料不完整，跳過 Row $rowCount incomplete, skipping\n";
                $errorCount++;
                continue;
            }

            // 執行插入 Execute insert
            $result = $stmt->execute([
                (int)$row[0],      // id
                $row[1],           // stock_name
                (float)$row[2],    // close_price
                (float)$row[3],    // open_price
                (float)$row[4],    // high_price
                (float)$row[5],    // low_price
                (int)$row[6],      // volume
                (float)$row[7],    // market_cap
                !empty($row[8]) ? (float)$row[8] : null,  // pe_ratio
                !empty($row[9]) ? (float)$row[9] : null,  // dividend_yield
                $row[10],          // setor
                $row[11]           // trade_date
            ]);

            if ($result) {
                $successCount++;
                if ($successCount % 10 == 0) {
                    echo "已匯入 $successCount 筆資料... Imported $successCount records...\n";
                }
            }

        } catch (Exception $e) {
            $errorCount++;
            echo "第 $rowCount 行匯入失敗 Row $rowCount import failed: " . $e->getMessage() . "\n";
        }
    }

    fclose($handle);

    echo "\n=== 匯入結果 Import Results ===\n";
    echo "總處理行數 Total rows processed: $rowCount\n";
    echo "成功匯入 Successfully imported: $successCount\n";
    echo "失敗數量 Failed: $errorCount\n";

    // 驗證匯入結果 Verify import results
    $totalRecords = $pdo->query("SELECT COUNT(*) FROM stock_data")->fetchColumn();
    echo "資料庫中總記錄數 Total records in database: $totalRecords\n";

    // 顯示部分匯入的資料 Show some imported data
    echo "\n=== 匯入資料範例 Sample Imported Data ===\n";
    $sampleData = $pdo->query("
        SELECT id, stock_name, trade_date, close_price, open_price, high_price, low_price 
        FROM stock_data 
        ORDER BY trade_date DESC, id 
        LIMIT 5
    ")->fetchAll(PDO::FETCH_ASSOC);

    foreach ($sampleData as $row) {
        echo "{$row['id']} {$row['stock_name']} {$row['trade_date']} - ";
        echo "收盤: {$row['close_price']}, 開盤: {$row['open_price']}, ";
        echo "最高: {$row['high_price']}, 最低: {$row['low_price']}\n";
    }

    echo "\n✓ 基礎資料匯入完成！Basic data import completed!\n";
    echo "下一步請執行 calculate_indicators.php 計算DSA和移動平均\n";
    echo "Next step: run calculate_indicators.php to calculate DSA and moving averages\n";

} catch (PDOException $e) {
    echo "資料庫錯誤 Database error: " . $e->getMessage() . "\n";
} catch (Exception $e) {
    echo "錯誤 Error: " . $e->getMessage() . "\n";
}
?>