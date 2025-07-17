package com.example.streaming.processor;

import com.example.streaming.model.ProcessedEvent;
import com.example.streaming.model.UserActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class StreamProcessor {

    private static final Logger logger = LoggerFactory.getLogger(StreamProcessor.class);
    private static final String INPUT_TOPIC = "user-activities";
    private static final String OUTPUT_TOPIC = "processed-events";
    
    private final ObjectMapper objectMapper;
    private KafkaStreams streams;
    private final ConcurrentMap<String, ProcessedEvent> latestProcessedEvents = new ConcurrentHashMap<>();

    @Autowired
    public StreamProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void startStreaming() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streaming-processor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        buildTopology(builder);

        streams = new KafkaStreams(builder.build(), props);
        
        streams.setStateListener((newState, oldState) -> {
            logger.info("Kafka Streams state transition: {} -> {}", oldState, newState);
        });

        streams.start();
        logger.info("Kafka Streams started");
    }

    private void buildTopology(StreamsBuilder builder) {
        KStream<String, String> userActivities = builder.stream(INPUT_TOPIC);

        // Parse JSON to UserActivity objects
        KStream<String, UserActivity> parsedActivities = userActivities
                .mapValues(this::parseUserActivity)
                .filter((key, value) -> value != null);

        // Group by action and create time windows
        KGroupedStream<String, UserActivity> groupedByAction = parsedActivities
                .groupBy((key, value) -> value.getAction(), Grouped.with(Serdes.String(), Serdes.String()));

        // Create 30-second tumbling windows and aggregate
        TimeWindowedKStream<String, UserActivity> windowedStream = groupedByAction
                .windowedBy(TimeWindows.of(Duration.ofSeconds(30)));

        KTable<Windowed<String>, ProcessedEvent> aggregatedEvents = windowedStream
                .aggregate(
                        () -> new ProcessedEvent(),
                        (key, value, aggregate) -> {
                            if (aggregate.getEventType() == null) {
                                aggregate.setEventType(key);
                                aggregate.setCount(0);
                                aggregate.setAggregatedValue(0.0);
                            }
                            aggregate.setCount(aggregate.getCount() + 1);
                            aggregate.setAggregatedValue(aggregate.getAggregatedValue() + value.getValue());
                            aggregate.setProcessedAt(LocalDateTime.now());
                            return aggregate;
                        },
                        Materialized.with(Serdes.String(), Serdes.String())
                );

        // Convert aggregated events to JSON and send to output topic
        aggregatedEvents.toStream()
                .map((windowedKey, processedEvent) -> {
                    processedEvent.setWindowStart(LocalDateTime.now().minusSeconds(30));
                    processedEvent.setWindowEnd(LocalDateTime.now());
                    
                    // Store for API access
                    latestProcessedEvents.put(processedEvent.getEventType(), processedEvent);
                    
                    String jsonValue = serializeProcessedEvent(processedEvent);
                    return KeyValue.pair(windowedKey.key(), jsonValue);
                })
                .to(OUTPUT_TOPIC);

        // Log processed events
        aggregatedEvents.toStream().foreach((windowedKey, event) -> {
            logger.info("Processed event: type={}, count={}, avgValue={}, window=[{} - {}]",
                    event.getEventType(),
                    event.getCount(),
                    event.getCount() > 0 ? event.getAggregatedValue() / event.getCount() : 0,
                    event.getWindowStart(),
                    event.getWindowEnd());
        });
    }

    private UserActivity parseUserActivity(String json) {
        try {
            return objectMapper.readValue(json, UserActivity.class);
        } catch (Exception e) {
            logger.error("Failed to parse UserActivity from JSON: {}", json, e);
            return null;
        }
    }

    private String serializeProcessedEvent(ProcessedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            logger.error("Failed to serialize ProcessedEvent", e);
            return "{}";
        }
    }

    public ConcurrentMap<String, ProcessedEvent> getLatestProcessedEvents() {
        return latestProcessedEvents;
    }

    @PreDestroy
    public void stopStreaming() {
        if (streams != null) {
            streams.close();
            logger.info("Kafka Streams stopped");
        }
    }
}