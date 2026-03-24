package com.focusvault.auth_service.service;

import com.focusvault.auth_service.dto.BrowsingEventMessage;
import com.focusvault.auth_service.security.KafkaTopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BrowsingEventProducer {

    private static final Logger log = LoggerFactory.getLogger(BrowsingEventProducer.class);

    private final KafkaTemplate<String, BrowsingEventMessage> kafkaTemplate;

    public BrowsingEventProducer(KafkaTemplate<String, BrowsingEventMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(BrowsingEventMessage message) {
        log.info("Sending event to Kafka: domain={}, user={}",
                message.getDomain(), message.getEmail());

        kafkaTemplate.send(KafkaTopicConfig.BROWSING_EVENTS_TOPIC, message);

        log.info("Event sent to Kafka successfully");
    }
}