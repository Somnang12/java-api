# --- Stage 1: Build the application ---
FROM gradle:8-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Build the WAR/JAR file (skipping tests for faster deployment)
RUN gradle build -x test --no-daemon

# --- Stage 2: Run the application ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built artifact from the build stage
# Note: Since you have id 'war' in your gradle, the output is likely a .war file
COPY --from=build /home/gradle/src/build/libs/*.war app.war

# Set environment variables (optional defaults)
ENV PORT=8080
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.war"]



