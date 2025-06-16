-- Create Database Script for Stock Analysis
-- 股票分析資料庫建立腳本

-- Create database (run this first in phpMyAdmin or MySQL command line)
-- 建立資料庫（先在phpMyAdmin或MySQL命令列執行）
CREATE DATABASE IF NOT EXISTS stock_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
-- 使用資料庫
USE stock_db;

-- Create the main stock data table
-- 建立主要股票資料表
CREATE TABLE IF NOT EXISTS stock_data (
    id INT NOT NULL COMMENT '股票代碼 Stock ID',
    stock_name VARCHAR(50) NOT NULL COMMENT '股票名稱 Stock Name',
    close_price DECIMAL(10, 2) NOT NULL COMMENT '收盤價 Close Price',
    open_price DECIMAL(10, 2) NOT NULL COMMENT '開盤價 Open Price',
    high_price DECIMAL(10, 2) NOT NULL COMMENT '最高價 High Price',
    low_price DECIMAL(10, 2) NOT NULL COMMENT '最低價 Low Price',
    volume BIGINT NOT NULL COMMENT '成交量 Volume',
    market_cap DECIMAL(15, 2) NOT NULL COMMENT '市值 Market Cap',
    pe_ratio DECIMAL(8, 2) COMMENT '本益比 PE Ratio',
    dividend_yield DECIMAL(5, 2) COMMENT '股息殖利率 Dividend Yield',
    setor VARCHAR(50) COMMENT '行業別 Sector',
    trade_date DATE NOT NULL COMMENT '交易日期 Trade Date',
    dsa_indicator DECIMAL(10, 6) COMMENT 'DSA指標 DSA Indicator',
    ma_5_days DECIMAL(10, 2) COMMENT '5日移動平均 5-Day Moving Average',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間 Created Time',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間 Updated Time',
    PRIMARY KEY (id, trade_date),
    INDEX idx_stock_date (id, trade_date),
    INDEX idx_trade_date (trade_date),
    INDEX idx_sector (setor),
    INDEX idx_dsa (dsa_indicator),
    INDEX idx_ma5 (ma_5_days)
) ENGINE=InnoDB COMMENT='股票交易資料表 Stock Trading Data Table';

-- Show table structure
-- 顯示表格結構
DESCRIBE stock_data;
