# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies separately (for better caching)
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
USER appuser

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Configure JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SERVER_PORT=8080

EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Run with proper JVM settings
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar