#!/bin/sh
set -x  # Enable debug mode to print all commands

# Print environment variables (without sensitive data)
echo "Starting application with the following configuration:"
echo "SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL:-NOT_SET}"
echo "SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME:-NOT_SET}"
echo "SPRING_DATASOURCE_PASSWORD is set: $([ ! -z "$SPRING_DATASOURCE_PASSWORD" ] && echo 'YES' || echo 'NO')"
echo "JAVA_OPTS=${JAVA_OPTS}"
echo "SERVER_PORT=${SERVER_PORT}"

# Export environment variables explicitly
export SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL:-jdbc:sqlserver://testingdockerserver.database.windows.net:1433;database=testingdockerdb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30}"
export SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME}"
export SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD}"

# Print the exported URL (masked)
echo "Exported SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL/\/\/.*@/\/\/*****@}"

# Check if the JAR file exists
if [ ! -f app.jar ]; then
    echo "ERROR: app.jar not found!"
    ls -la
    exit 1
fi

# Check Java version
java -version

# Print disk space
df -h

# Print memory info
free -m || echo "free command not available"

# Start the application with more verbose logging
echo "Starting Java application..."
exec java ${JAVA_OPTS} -Dlogging.level.root=DEBUG -Dlogging.level.org.springframework=DEBUG -Dlogging.level.com.testing=TRACE -jar app.jar