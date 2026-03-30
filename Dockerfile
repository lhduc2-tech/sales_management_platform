# --- Giai đoạn 1: Build ứng dụng ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy file cấu hình và source code
COPY pom.xml .
COPY src ./src

# Build file .jar
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng ---
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy file .jar từ giai đoạn 1 sang
COPY --from=build /app/target/*.jar app.jar

# Mở port 8080
EXPOSE 8082

# Chạy ứng dụng (Vẫn giữ cấu hình tối ưu RAM)
ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-XX:MaxRAMPercentage=75.0", "-Dserver.port=8082", "-jar", "app.jar"]