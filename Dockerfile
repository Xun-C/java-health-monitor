# 使用官方穩定、輕量級的 Alpine Java 影像
FROM eclipse-temurin:21-jdk-alpine

# 設定貨櫃內的工作目錄
WORKDIR /app

# 修正核心：直接複製根目錄下的 App.java 到貨櫃中（不再尋找 src 資料夾）
COPY App.java /app/

# 編譯位於工作目錄下的 App.java
RUN javac App.java

# 開放 8080 連接埠
EXPOSE 8080

# 啟動伺服器，直接執行 App 類別
CMD ["java", "App"]
