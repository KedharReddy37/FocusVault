package com.focusvault.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "browsing_events")
public class BrowsingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ── Constructors ──────────────────────────
    public BrowsingEvent() {}

    // ── Getters ───────────────────────────────
    public Long getId() { return id; }
    public String getDomain() { return domain; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Category getCategory() { return category; }
    public User getUser() { return user; }

    // ── Setters ───────────────────────────────
    public void setId(Long id) { this.id = id; }
    public void setDomain(String domain) { this.domain = domain; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setCategory(Category category) { this.category = category; }
    public void setUser(User user) { this.user = user; }

    // ── Builder ───────────────────────────────
    public static BrowsingEventBuilder builder() { return new BrowsingEventBuilder(); }

    public static class BrowsingEventBuilder {
        private String domain;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Category category;
        private User user;

        public BrowsingEventBuilder domain(String domain) { this.domain = domain; return this; }
        public BrowsingEventBuilder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public BrowsingEventBuilder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
        public BrowsingEventBuilder category(Category category) { this.category = category; return this; }
        public BrowsingEventBuilder user(User user) { this.user = user; return this; }

        public BrowsingEvent build() {
            BrowsingEvent event = new BrowsingEvent();
            event.domain = this.domain;
            event.startTime = this.startTime;
            event.endTime = this.endTime;
            event.category = this.category;
            event.user = this.user;
            return event;
        }
    }
}