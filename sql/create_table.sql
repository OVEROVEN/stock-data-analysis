-- 在phpMyAdmin中執行這個SQL腳本
-- Execute this SQL script in phpMyAdmin

USE stock_db;

CREATE TABLE stock_data (
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
    PRIMARY KEY (id, trade_date),
    INDEX idx_stock_date (id, trade_date),
    INDEX idx_trade_date (trade_date)
);
