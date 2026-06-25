# 1. 使用穩定的 Alpine JDK 鏡像
FROM eclipse-temurin:21-jdk-alpine

# 2. 【核心修正】強行注入中文化與 UTF-8 語系環境變數，防止 Java 因中文編碼崩潰
ENV LANG=zh_TW.UTF-8
ENV LANGUAGE=zh_TW:zh
ENV LC_ALL=zh_TW.UTF-8

# 3. 設定工作目錄
WORKDIR /app

# 4. 複製並編譯單一檔案
COPY App.java /app/
RUN javac -encoding UTF-8 App.java

# 5. 開放連接埠
EXPOSE 8080

# 6. 執行服務
CMD ["java", "App"]
