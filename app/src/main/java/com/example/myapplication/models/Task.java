package com.example.myapplication.models;

public class Task {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String priority;
    private boolean completed;
    private long createdAt;

    public Task() {
    }

    public Task(String userId, String title, String description, String priority, boolean completed, long createdAt) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }
}