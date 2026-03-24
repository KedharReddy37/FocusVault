package com.focusvault.auth_service.security;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String BROWSING_EVENTS_TOPIC = "browsing-events";

    @Bean
    public NewTopic browsingEventsTopic() {
        return TopicBuilder.name(BROWSING_EVENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}