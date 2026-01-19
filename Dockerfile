# --- Stage 1: Build the application ---
FROM gradle:8.10-jdk21 AS build
WORKDIR /home/gradle/src

# Copy only the necessary files for dependency resolution first (for caching)
COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle gradle/ ./gradle/

# Copy the rest of the source code
COPY --chown=gradle:gradle . .

# Build the WAR file, skipping tests for speed
RUN gradle bootWar -x test --no-daemon

# --- Stage 2: Runtime Environment ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the generated WAR from the build stage
# We rename it to 'app.war' to match our CMD
COPY --from=build /home/gradle/src/build/libs/app.war app.war

# Set the port Render expects (8080 by default for Spring Boot)
ENV PORT=8080
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.war"]