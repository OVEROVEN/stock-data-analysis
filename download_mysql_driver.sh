#!/bin/bash
# 下載MySQL JDBC驅動程式

echo "下載MySQL JDBC驅動程式..."
curl -L -o /Users/afu/Desktop/mysql-connector-java-8.0.33.jar \
  "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar"

echo "✓ 下載完成！"
echo "檔案位置: /Users/afu/Desktop/mysql-connector-java-8.0.33.jar"
