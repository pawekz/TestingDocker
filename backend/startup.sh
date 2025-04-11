#!/bin/sh
set -e

# Print environment variables (without sensitive data)
echo "Starting application with the following configuration:"
echo "SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL/\/\/.*@/\/\/*****@}"
echo "SPRING_DATASOURCE_USERNAME=******"
echo "JAVA_OPTS=${JAVA_OPTS}"
echo "SERVER_PORT=${SERVER_PORT}"

# Start the application
exec java ${JAVA_OPTS} -jar app.jar