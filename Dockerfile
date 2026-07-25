# ==========================================================================
# Stage 1: Build the application with Maven
# ==========================================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Copy only the POM first so Docker can cache the dependency download layer
# and skip re-downloading them when only source code changes.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Now copy the rest of the source and build the jar.
COPY src ./src
RUN mvn -B -DskipTests clean package

# ==========================================================================
# Stage 2: Run the application on a minimal JRE (no Maven, no build tools)
# ==========================================================================
FROM eclipse-temurin:17-jre-alpine

# Run as a non-root user for better container security.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy only the built jar from the build stage - keeps the final image small.
COPY --from=build /build/target/weather-app.jar app.jar

EXPOSE 8080

# Basic container-level health check hitting the home page.
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget -qO- http://localhost:8080/ || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
