package com.example.aibasedcareercounsellingapp.models;

public class HistoryItem {
    private String type; // "Prediction" or "Resume Analysis"
    private String details;
    private long timestamp;

    public HistoryItem() {}

    public HistoryItem(String type, String details, long timestamp) {
        this.type = type;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public String getDetails() { return details; }
    public long getTimestamp() { return timestamp; }
}
