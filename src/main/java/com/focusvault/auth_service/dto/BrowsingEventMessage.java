package com.focusvault.auth_service.dto;

import com.focusvault.auth_service.entity.Category;
import java.time.LocalDateTime;

public class BrowsingEventMessage {

    private String email;
    private String domain;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Category category;

    public BrowsingEventMessage() {}

    public BrowsingEventMessage(String email, String domain,
                                 LocalDateTime startTime, LocalDateTime endTime,
                                 Category category) {
        this.email = email;
        this.domain = domain;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
    }

    public String getEmail() { return email; }
    public String getDomain() { return domain; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Category getCategory() { return category; }

    public void setEmail(String email) { this.email = email; }
    public void setDomain(String domain) { this.domain = domain; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setCategory(Category category) { this.category = category; }
}