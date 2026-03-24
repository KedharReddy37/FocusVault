package com.focusvault.auth_service.service;

import com.focusvault.auth_service.dto.BrowsingEventMessage;
import com.focusvault.auth_service.entity.BrowsingEvent;
import com.focusvault.auth_service.entity.User;
import com.focusvault.auth_service.repository.BrowsingEventRepository;
import com.focusvault.auth_service.repository.UserRepository;
import com.focusvault.auth_service.security.KafkaTopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BrowsingEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(BrowsingEventConsumer.class);

    private final BrowsingEventRepository browsingEventRepository;
    private final UserRepository userRepository;

    public BrowsingEventConsumer(BrowsingEventRepository browsingEventRepository,
                                  UserRepository userRepository) {
        this.browsingEventRepository = browsingEventRepository;
        this.userRepository = userRepository;
    }

    @KafkaListener(
        topics = KafkaTopicConfig.BROWSING_EVENTS_TOPIC,
        groupId = "focusvault-group"
    )
    @CacheEvict(value = "summary", key = "#message.email")
    public void consumeEvent(BrowsingEventMessage message) {
        log.info("Received event from Kafka: domain={}, user={}",
                message.getDomain(), message.getEmail());

        User user = userRepository.findByEmail(message.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + message.getEmail()));

        BrowsingEvent event = BrowsingEvent.builder()
                .domain(message.getDomain())
                .startTime(message.getStartTime())
                .endTime(message.getEndTime())
                .category(message.getCategory())
                .user(user)
                .build();

        browsingEventRepository.save(event);

        log.info("Event saved to PostgreSQL: domain={}", message.getDomain());
    }
}