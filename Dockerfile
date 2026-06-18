FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY src/ /app/src/
RUN javac src/App.java
EXPOSE 8080
CMD ["java", "-cp", "src", "App"]
