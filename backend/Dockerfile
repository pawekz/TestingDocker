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

# Install debugging tools
RUN apk add --no-cache wget curl procps busybox-extras net-tools

# Create a non-root user
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar
COPY startup.sh .
RUN chmod +x startup.sh

# Configure JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SERVER_PORT=8080

# Print environment variables for debugging (will be executed at container build time)
RUN echo "Environment variables will be printed at startup"

EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q --spider http://localhost:8080/health || exit 1

# Switch to non-root user
USER appuser

# Run with startup script
ENTRYPOINT ["./startup.sh"]