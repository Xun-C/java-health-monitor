# 使用輕量級的 JDK 21 影像作為基礎
FROM openjdk:21-slim

# 設定工作目錄
WORKDIR /app

# 將專案內 src 目錄的所有原始碼複製進去
COPY src/ /app/src/

# 編譯 App.java
RUN javac src/App.java

# 開放 8080 連接埠
EXPOSE 8080

# 啟動伺服器
CMD ["java", "-cp", "src", "App"]
