package com.example.userservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    NewTopic userCreated() { return TopicBuilder.name("user.created").partitions(3).replicas(1).build(); }
    @Bean
    NewTopic userUpdated() { return TopicBuilder.name("user.updated").partitions(3).replicas(1).build(); }
    @Bean
    NewTopic userDeleted() { return TopicBuilder.name("user.deleted").partitions(3).replicas(1).build(); }
}