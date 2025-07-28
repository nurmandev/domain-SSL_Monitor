# Domain-SSL Monitor

A comprehensive Spring Boot application for monitoring SSL certificate expiry dates across multiple domains with async processing, database storage, and REST API endpoints.

## Features

- ✅ **SSL Certificate Monitoring**: Check expiry dates for multiple domains
- ⚡ **Async Processing**: Bulk domain checks with configurable thread pools
- 🗄️ **Database Storage**: Historical tracking with H2 and PostgreSQL support
- 🔄 **REST API**: Full CRUD operations with OpenAPI documentation
- 📊 **Configurable Thresholds**: 7, 30, 90-day expiry warnings
- 🐳 **Docker Support**: Complete containerization with Docker Compose
- 📝 **Structured Logging**: JSON logging with Logback
- 🧪 **Unit Tests**: Comprehensive test coverage
- 📈 **Monitoring**: Actuator endpoints for health checks

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker

### Running with Docker (Recommended)

```bash
# Build and start the application
./build.sh
./run.sh

# Or manually with Docker Compose
docker-compose up -d
```

### Running Locally

```bash
# Build the application
mvn clean package

# Run with H2 database
mvn spring-boot:run

# Run with PostgreSQL
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

## API Endpoints

### Check SSL Certificates

```bash
# Check multiple domains asynchronously
curl -X POST http://localhost:8080/api/ssl/check \
  -H "Content-Type: application/json" \
  -d '{
    "domains": ["google.com", "github.com", "stackoverflow.com"],
    "warningThresholdDays": 30,
    "criticalThresholdDays": 7
  }'

# Check single domain
curl "http://localhost:8080/api/ssl/check/google.com?warningThresholdDays=30&criticalThresholdDays=7"
```

### Query Historical Data

```bash
# Get domain history
curl "http://localhost:8080/api/ssl/history/google.com"

# Get expiring certificates
curl "http://localhost:8080/api/ssl/expiring?days=30"

# Get certificates by status
curl "http://localhost:8080/api/ssl/status?statuses=EXPIRING_SOON,EXPIRED"

# Get recent checks
curl "http://localhost:8080/api/ssl/recent?hours=24"
```

## Configuration

### Application Properties

```yaml
ssl:
  monitor:
    default-warning-threshold: 30
    default-critical-threshold: 7
    connection-timeout: 10000
    max-concurrent-checks: 50
```

### Database Configuration

#### H2 (Default)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  h2:
    console:
      enabled: true
      path: /h2-console
```

## Development

### Project Structure

```
ssl-domain-monitor/
├── src/main/java/com/monitor/ssl/
│   ├── Application.java
│   ├── config/                 # Configuration classes
│   ├── controller/            # REST controllers
│   ├── dto/                   # Data transfer objects
│   ├── entity/                # JPA entities
│   ├── enums/                 # Enums
│   ├── repository/            # Data repositories
│   └── service/               # Business logic
├── src/main/resources/
│   ├── application.yml        # Application configuration
│   └── logback-spring.xml     # Logging configuration
├── src/test/                  # Unit tests
├── docker/                    # Docker configuration
├── docker-compose.yml         # Docker Compose setup
└── README.md
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SslMonitorServiceTest

# Run tests with coverage
mvn test jacoco:report
```

### Building

```bash
# Clean build
mvn clean package

# Skip tests
mvn clean package -DskipTests

# Build Docker image
docker build -f docker/Dockerfile -t ssl-domain-monitor:latest .
```

## Monitoring & Observability

### Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# Detailed health info
curl http://localhost:8080/actuator/health/db
```

### Metrics

```bash
# Application metrics
curl http://localhost:8080/actuator/metrics

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

### Logging

The application uses structured JSON logging with the following levels:

- **DEBUG**: Detailed SSL check information
- **INFO**: General application flow
- **WARN**: Potential issues
- **ERROR**: SSL check failures and exceptions

## API Documentation

Interactive API documentation is available at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Database Schema

### SSL Certificate Checks Table

```sql
CREATE TABLE ssl_certificate_checks (
    id BIGSERIAL PRIMARY KEY,
    domain VARCHAR(255) NOT NULL,
    certificate_issuer VARCHAR(500),
    certificate_subject VARCHAR(500),
    valid_from TIMESTAMP,
    valid_until TIMESTAMP,
    days_until_expiry INTEGER,
    status VARCHAR(50) NOT NULL,
    error_message TEXT,
    check_timestamp TIMESTAMP NOT NULL,
    response_time_ms BIGINT
);
```

### Certificate Status Enum

- `VALID`: Certificate is valid and not expiring soon
- `EXPIRING_SOON`: Certificate expires within threshold
- `EXPIRED`: Certificate has expired
- `INVALID`: Certificate is invalid
- `ERROR`: Error occurred during check



