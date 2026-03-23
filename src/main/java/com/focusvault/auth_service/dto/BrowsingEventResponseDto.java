package com.focusvault.auth_service.dto;

import com.focusvault.auth_service.entity.Category;
import java.time.LocalDateTime;

public class BrowsingEventResponseDto {

    private Long id;
    private String domain;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Category category;
    private long durationSeconds;

    public BrowsingEventResponseDto() {}

    public Long getId() { return id; }
    public String getDomain() { return domain; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Category getCategory() { return category; }
    public long getDurationSeconds() { return durationSeconds; }

    public void setId(Long id) { this.id = id; }
    public void setDomain(String domain) { this.domain = domain; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setCategory(Category category) { this.category = category; }
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }

    public static BrowsingEventResponseDtoBuilder builder() { return new BrowsingEventResponseDtoBuilder(); }

    public static class BrowsingEventResponseDtoBuilder {
        private Long id;
        private String domain;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Category category;
        private long durationSeconds;

        public BrowsingEventResponseDtoBuilder id(Long id) { this.id = id; return this; }
        public BrowsingEventResponseDtoBuilder domain(String domain) { this.domain = domain; return this; }
        public BrowsingEventResponseDtoBuilder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public BrowsingEventResponseDtoBuilder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
        public BrowsingEventResponseDtoBuilder category(Category category) { this.category = category; return this; }
        public BrowsingEventResponseDtoBuilder durationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; return this; }

        public BrowsingEventResponseDto build() {
            BrowsingEventResponseDto dto = new BrowsingEventResponseDto();
            dto.id = this.id;
            dto.domain = this.domain;
            dto.startTime = this.startTime;
            dto.endTime = this.endTime;
            dto.category = this.category;
            dto.durationSeconds = this.durationSeconds;
            return dto;
        }
    }
}