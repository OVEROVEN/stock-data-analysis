<?php
// DSA指標和移動平均計算腳本
// DSA Indicator and Moving Average Calculation Script

echo "=== 開始計算DSA指標和5日移動平均 Starting DSA and 5-Day MA Calculation ===\n";

// 資料庫連線設定
$host = 'localhost';
$dbname = 'stock_db';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    echo "✓ 資料庫連接成功\n";

    // 檢查資料是否存在
    $recordCount = $pdo->query("SELECT COUNT(*) FROM stock_data")->fetchColumn();
    echo "資料庫中共有 $recordCount 筆記錄\n";

    if ($recordCount == 0) {
        throw new Exception("資料庫中沒有資料，請先執行 import_stock_data.php");
    }

    // 1. 計算DSA指標
    echo "\n1. 計算DSA指標... Calculating DSA Indicator...\n";
    
    $dsaUpdateSQL = "
    UPDATE stock_data 
    SET dsa_indicator = (
        CASE 
            WHEN (POWER(high_price - open_price, 2) + POWER(open_price - low_price, 2)) = 0 THEN 0
            ELSE ((high_price - open_price) + (open_price - low_price)) / 
                 (POWER(high_price - open_price, 2) + POWER(open_price - low_price, 2))
        END
    )
    ";
    
    $pdo->exec($dsaUpdateSQL);
    echo "✓ DSA指標計算完成\n";

    // 驗證DSA計算結果
    $dsaStats = $pdo->query("
        SELECT 
            COUNT(*) as total,
            COUNT(CASE WHEN dsa_indicator > 0 THEN 1 END) as positive,
            COUNT(CASE WHEN dsa_indicator < 0 THEN 1 END) as negative,
            COUNT(CASE WHEN dsa_indicator = 0 THEN 1 END) as zero,
            ROUND(MIN(dsa_indicator), 6) as min_val,
            ROUND(MAX(dsa_indicator), 6) as max_val
        FROM stock_data
    ")->fetch(PDO::FETCH_ASSOC);

    echo "DSA統計 DSA Statistics:\n";
    echo "  總數 Total: {$dsaStats['total']}\n";
    echo "  正值 Positive: {$dsaStats['positive']}\n";
    echo "  負值 Negative: {$dsaStats['negative']}\n";
    echo "  零值 Zero: {$dsaStats['zero']}\n";
    echo "  最小值 Min: {$dsaStats['min_val']}\n";
    echo "  最大值 Max: {$dsaStats['max_val']}\n";

    // 2. 計算5日移動平均
    echo "\n2. 計算5日移動平均... Calculating 5-Day Moving Average...\n";

    // 取得所有股票代碼
    $stockIDs = $pdo->query("SELECT DISTINCT id FROM stock_data ORDER BY id")->fetchAll(PDO::FETCH_COLUMN);
    echo "處理 " . count($stockIDs) . " 支股票\n";

    $processedStocks = 0;
    foreach ($stockIDs as $stockId) {
        // 取得該股票所有記錄，按日期排序
        $records = $pdo->query("
            SELECT trade_date, close_price 
            FROM stock_data 
            WHERE id = $stockId 
            ORDER BY trade_date ASC
        ")->fetchAll(PDO::FETCH_ASSOC);

        // 為每筆記錄計算移動平均
        for ($i = 0; $i < count($records); $i++) {
            // 取前面最多5天（包含當天）
            $startIndex = max(0, $i - 4);
            $endIndex = $i;
            
            $sum = 0;
            $count = 0;
            
            for ($j = $startIndex; $j <= $endIndex; $j++) {
                $sum += $records[$j]['close_price'];
                $count++;
            }
            
            $ma5 = $sum / $count;
            
            // 更新移動平均值
            $updateStmt = $pdo->prepare("
                UPDATE stock_data 
                SET ma_5_days = ? 
                WHERE id = ? AND trade_date = ?
            ");
            $updateStmt->execute([$ma5, $stockId, $records[$i]['trade_date']]);
        }
        
        $processedStocks++;
        if ($processedStocks % 5 == 0) {
            echo "已處理 $processedStocks 支股票... Processed $processedStocks stocks...\n";
        }
    }

    echo "✓ 5日移動平均計算完成\n";

    // 3. 顯示計算結果範例
    echo "\n3. 計算結果範例 Sample Results:\n";
    echo "台積電 (2330) 最近5天資料:\n";
    
    $sampleResults = $pdo->query("
        SELECT trade_date, 
               close_price,
               open_price,
               high_price,
               low_price,
               ROUND(dsa_indicator, 6) as dsa,
               ROUND(ma_5_days, 2) as ma5
        FROM stock_data 
        WHERE id = 2330 
        ORDER BY trade_date DESC 
        LIMIT 5
    ")->fetchAll(PDO::FETCH_ASSOC);

    echo "日期       | 收盤價  | 開盤價  | 最高價  | 最低價  | DSA      | 5日均線\n";
    echo "-----------|---------|---------|---------|---------|----------|--------\n";
    
    foreach ($sampleResults as $row) {
        printf("%-10s | %-7.2f | %-7.2f | %-7.2f | %-7.2f | %-8s | %-7.2f\n",
            $row['trade_date'],
            $row['close_price'],
            $row['open_price'],
            $row['high_price'],
            $row['low_price'],
            $row['dsa'],
            $row['ma5']
        );
    }

    // 4. DSA計算驗證範例
    echo "\n4. DSA計算驗證 DSA Calculation Verification:\n";
    $verifyExample = $pdo->query("
        SELECT trade_date, open_price, high_price, low_price,
               (high_price - open_price) as high_minus_open,
               (open_price - low_price) as open_minus_low,
               ((high_price - open_price) + (open_price - low_price)) as numerator,
               (POWER(high_price - open_price, 2) + POWER(open_price - low_price, 2)) as denominator,
               ROUND(dsa_indicator, 6) as dsa
        FROM stock_data 
        WHERE id = 2330 
        ORDER BY trade_date DESC 
        LIMIT 1
    ")->fetch(PDO::FETCH_ASSOC);

    echo "台積電最新一天DSA計算詳細:\n";
    echo "日期: {$verifyExample['trade_date']}\n";
    echo "開盤價: {$verifyExample['open_price']}\n";
    echo "最高價: {$verifyExample['high_price']}\n";
    echo "最低價: {$verifyExample['low_price']}\n";
    echo "High - Open = {$verifyExample['high_minus_open']}\n";
    echo "Open - Low = {$verifyExample['open_minus_low']}\n";
    echo "分子 Numerator = {$verifyExample['numerator']}\n";
    echo "分母 Denominator = {$verifyExample['denominator']}\n";
    echo "DSA = {$verifyExample['dsa']}\n";

    echo "\n✓ 所有計算完成！All calculations completed!\n";
    echo "現在可以執行 query_analysis.php 查看詳細分析\n";
    echo "Now you can run query_analysis.php for detailed analysis\n";

} catch (PDOException $e) {
    echo "資料庫錯誤: " . $e->getMessage() . "\n";
} catch (Exception $e) {
    echo "錯誤: " . $e->getMessage() . "\n";
}
?>