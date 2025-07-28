FROM openjdk:17-jdk-slim

LABEL maintainer="SSL Monitor Team <support@sslmonitor.com>"
LABEL version="1.0.0"
LABEL description="SSL Domain Monitor Application"

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Create non-root user
RUN groupadd -r sslmonitor && useradd -r -g sslmonitor sslmonitor

# Copy Maven dependencies (for better layer caching)
COPY target/dependency-jars /app/lib

# Copy the application JAR
COPY target/ssl-domain-monitor-*.jar /app/ssl-domain-monitor.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R sslmonitor:sslmonitor /app

# Switch to non-root user
USER sslmonitor

# Expose port
EXPOSE 8080