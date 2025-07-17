package com.example.streaming.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProcessedEvent {
    
    @JsonProperty("eventType")
    private String eventType;
    
    @JsonProperty("aggregatedValue")
    private double aggregatedValue;
    
    @JsonProperty("count")
    private long count;
    
    @JsonProperty("windowStart")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime windowStart;
    
    @JsonProperty("windowEnd")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime windowEnd;
    
    @JsonProperty("processedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime processedAt;

    public ProcessedEvent() {
    }

    public ProcessedEvent(String eventType, double aggregatedValue, long count, 
                         LocalDateTime windowStart, LocalDateTime windowEnd, LocalDateTime processedAt) {
        this.eventType = eventType;
        this.aggregatedValue = aggregatedValue;
        this.count = count;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.processedAt = processedAt;
    }

    // Getters and setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public double getAggregatedValue() {
        return aggregatedValue;
    }

    public void setAggregatedValue(double aggregatedValue) {
        this.aggregatedValue = aggregatedValue;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public LocalDateTime getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(LocalDateTime windowStart) {
        this.windowStart = windowStart;
    }

    public LocalDateTime getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(LocalDateTime windowEnd) {
        this.windowEnd = windowEnd;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessedEvent that = (ProcessedEvent) o;
        return Double.compare(that.aggregatedValue, aggregatedValue) == 0 &&
               count == that.count &&
               Objects.equals(eventType, that.eventType) &&
               Objects.equals(windowStart, that.windowStart) &&
               Objects.equals(windowEnd, that.windowEnd) &&
               Objects.equals(processedAt, that.processedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, aggregatedValue, count, windowStart, windowEnd, processedAt);
    }

    @Override
    public String toString() {
        return "ProcessedEvent{" +
               "eventType='" + eventType + '\'' +
               ", aggregatedValue=" + aggregatedValue +
               ", count=" + count +
               ", windowStart=" + windowStart +
               ", windowEnd=" + windowEnd +
               ", processedAt=" + processedAt +
               '}';
    }
}