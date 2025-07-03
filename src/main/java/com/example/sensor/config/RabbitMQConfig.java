package com.example.sensor.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String CHARGING_QUEUE = "charging.data.queue";

    @Bean
    public Queue chargingQueue() {
        return new Queue(CHARGING_QUEUE, false); // durable=false za jednostavnost
    }
}
