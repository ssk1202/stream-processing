# Java Streaming Processing Application

A comprehensive real-time data streaming and processing application built with Java, Spring Boot, and Apache Kafka.

## Features

- **Real-time Event Production**: Generates sample user activity events every 2 seconds
- **Stream Processing**: Uses Kafka Streams for windowed aggregations and transformations
- **REST API**: Provides endpoints to access processed events and system health
- **Real-time Dashboard**: Web interface with live charts and event monitoring
- **Scalable Architecture**: Modular design with proper separation of concerns

## Architecture

### Components

1. **Event Producer** (`EventProducer.java`)
   - Generates random user activity events
   - Publishes to `user-activities` Kafka topic
   - Configurable event generation rate

2. **Stream Processor** (`StreamProcessor.java`)
   - Consumes events from `user-activities` topic
   - Performs windowed aggregations (30-second windows)
   - Groups events by action type
   - Publishes results to `processed-events` topic

3. **REST API** (`StreamingController.java`)
   - `/api/streaming/events` - Get latest processed events
   - `/api/streaming/events/{type}` - Get specific event type
   - `/api/streaming/events/stream` - Server-sent events stream
   - `/api/streaming/health` - System health check

4. **Web Dashboard** (`dashboard.html`)
   - Real-time charts showing event counts by type
   - System health monitoring
   - Live event feed
   - Responsive design

## Data Models

### UserActivity
```json
{
  "userId": "user1",
  "action": "purchase",
  "timestamp": "2024-01-15T10:30:00",
  "value": 125.50,
  "sessionId": "uuid-string"
}
```

### ProcessedEvent
```json
{
  "eventType": "purchase",
  "aggregatedValue": 500.25,
  "count": 4,
  "windowStart": "2024-01-15T10:30:00",
  "windowEnd": "2024-01-15T10:30:30",
  "processedAt": "2024-01-15T10:30:31"
}
```

## Prerequisites

- Java 17 or higher
- Apache Kafka (local installation)
- Maven 3.6+

## Setup Instructions

### 1. Start Kafka

Download and start Kafka locally:

```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka server
bin/kafka-server-start.sh config/server.properties
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The application will:
- Start on port 8080
- Create required Kafka topics automatically
- Begin producing and processing events

### 3. Access the Dashboard

Open your browser and navigate to:
```
http://localhost:8080
```

### 4. API Endpoints

- Health check: `http://localhost:8080/api/streaming/health`
- Latest events: `http://localhost:8080/api/streaming/events`
- Event stream: `http://localhost:8080/api/streaming/events/stream`

## Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
server:
  port: 8080
```

### Kafka Topics

- `user-activities`: Raw user activity events
- `processed-events`: Aggregated and processed events

## Monitoring

The application includes:

- **Health checks** via Spring Boot Actuator
- **Metrics** endpoint for monitoring
- **Real-time dashboard** with system status
- **Comprehensive logging** with configurable levels

## Event Processing Pipeline

1. **Generation**: Random user activities are generated every 2 seconds
2. **Ingestion**: Events are published to Kafka topic
3. **Processing**: Kafka Streams processes events in 30-second windows
4. **Aggregation**: Events are grouped by action type and aggregated
5. **Storage**: Processed events are stored in memory for API access
6. **Visualization**: Dashboard displays real-time metrics and charts

## Extending the Application

### Adding New Event Types

1. Modify `EventProducer.java` to include new actions
2. Update processing logic in `StreamProcessor.java` if needed
3. Dashboard will automatically display new event types

### Custom Processing Logic

1. Extend `StreamProcessor.buildTopology()` method
2. Add new transformation or aggregation operations
3. Create new output topics if needed

### Additional APIs

1. Add new endpoints in `StreamingController.java`
2. Implement custom query logic
3. Update dashboard to consume new endpoints

## Troubleshooting

### Common Issues

1. **Kafka Connection Failed**
   - Ensure Kafka is running on localhost:9092
   - Check firewall settings

2. **Topics Not Created**
   - Verify Kafka auto-topic creation is enabled
   - Manually create topics if needed

3. **Dashboard Not Loading**
   - Check browser console for errors
   - Verify API endpoints are accessible

### Logs

Check application logs for detailed error information:
- Stream processing errors
- Kafka connection issues
- API request failures

## Performance Tuning

### Kafka Streams

- Adjust window size for different aggregation periods
- Modify commit interval for throughput vs latency trade-offs
- Configure appropriate number of stream threads

### Production Deployment

- Use external Kafka cluster
- Configure proper replication and partitioning
- Set up monitoring and alerting
- Implement proper error handling and recovery