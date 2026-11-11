package com.playground.bank.level3.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ACCOUNT_EVENTS = "account-events";

    /** Created in RabbitMQ on startup if it doesn't exist yet. */
    @Bean
    Queue accountEventsQueue() {
        return new Queue(ACCOUNT_EVENTS);
    }

    /** Send and receive events as JSON (Spring's ObjectMapper handles Instant). */
    @Bean
    MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
