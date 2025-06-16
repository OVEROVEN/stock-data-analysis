#!/bin/bash
# Java安裝腳本 Java Installation Script

echo "=== Java安裝助手 Java Installation Helper ==="

# 檢查是否已有Java
if command -v java &> /dev/null; then
    echo "✓ 發現Java版本："
    java -version
    echo ""
    echo "如果版本太舊，請繼續安裝新版本"
else
    echo "❌ 未發現Java"
fi

echo ""
echo "選擇安裝方式 Choose installation method:"
echo "1. 使用Homebrew安裝OpenJDK 17 (推薦)"
echo "2. 手動下載安裝包"
echo ""

read -p "請選擇 (1 或 2): " choice

case $choice in
    1)
        echo "使用Homebrew安裝..."
        
        # 檢查Homebrew
        if ! command -v brew &> /dev/null; then
            echo "正在安裝Homebrew..."
            /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        fi
        
        echo "正在安裝OpenJDK 17..."
        brew install openjdk@17
        
        # 設置環境變數
        echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
        echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@17"' >> ~/.zshrc
        
        echo "✓ 安裝完成！請執行以下命令或重啟終端機："
        echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"'
        echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@17"'
        ;;
    2)
        echo "請手動下載安裝："
        echo "1. 前往 https://adoptium.net/"
        echo "2. 選擇 macOS + JDK 17"
        echo "3. 下載並安裝 .pkg 檔案"
        echo "4. 安裝完成後重新執行Java程式"
        ;;
    *)
        echo "無效選擇"
        ;;
esac
