package com.focusvault.auth_service.dto;

import com.focusvault.auth_service.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BrowsingEventRequestDto {

    @NotBlank(message = "Domain is required")
    private String domain;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Category is required")
    private Category category;

    public String getDomain() { return domain; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Category getCategory() { return category; }

    public void setDomain(String domain) { this.domain = domain; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setCategory(Category category) { this.category = category; }
}