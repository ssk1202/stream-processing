package com.example.streaming.controller;

import com.example.streaming.model.ProcessedEvent;
import com.example.streaming.processor.StreamProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/streaming")
@CrossOrigin(origins = "*")
public class StreamingController {

    private final StreamProcessor streamProcessor;

    @Autowired
    public StreamingController(StreamProcessor streamProcessor) {
        this.streamProcessor = streamProcessor;
    }

    @GetMapping("/events")
    public ResponseEntity<Collection<ProcessedEvent>> getLatestEvents() {
        Map<String, ProcessedEvent> events = streamProcessor.getLatestProcessedEvents();
        return ResponseEntity.ok(events.values());
    }

    @GetMapping("/events/{eventType}")
    public ResponseEntity<ProcessedEvent> getEventByType(@PathVariable String eventType) {
        ProcessedEvent event = streamProcessor.getLatestProcessedEvents().get(eventType);
        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/events/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Collection<ProcessedEvent>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(tick -> streamProcessor.getLatestProcessedEvents().values());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "timestamp", System.currentTimeMillis(),
                "totalEventTypes", streamProcessor.getLatestProcessedEvents().size()
        );
        return ResponseEntity.ok(health);
    }
}