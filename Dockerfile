# ==========================================
# Stage 1: Build the Application
# ==========================================
FROM gradle:8-jdk17 AS builder
WORKDIR /app

# Copy the Gradle config and source code
COPY build.gradle settings.gradle ./
COPY src ./src

# Build the JAR, skipping tests to speed up cloud deployment
RUN gradle clean build -x test

# ==========================================
# Stage 2: Run the Application
# ==========================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy ONLY the executable JAR from the builder stage
# (We use *-SNAPSHOT.jar to avoid grabbing the non-executable plain.jar)
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# Expose the port (API usually uses 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]