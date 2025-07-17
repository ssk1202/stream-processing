package com.example.streaming.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserActivity {
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("action")
    private String action;
    
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    @JsonProperty("value")
    private double value;
    
    @JsonProperty("sessionId")
    private String sessionId;

    public UserActivity() {
    }

    public UserActivity(String userId, String action, LocalDateTime timestamp, double value, String sessionId) {
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
        this.value = value;
        this.sessionId = sessionId;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserActivity that = (UserActivity) o;
        return Double.compare(that.value, value) == 0 &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(action, that.action) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, action, timestamp, value, sessionId);
    }

    @Override
    public String toString() {
        return "UserActivity{" +
               "userId='" + userId + '\'' +
               ", action='" + action + '\'' +
               ", timestamp=" + timestamp +
               ", value=" + value +
               ", sessionId='" + sessionId + '\'' +
               '}';
    }
}