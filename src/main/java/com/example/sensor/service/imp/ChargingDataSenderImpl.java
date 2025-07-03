package com.example.sensor.service.imp;

import com.example.sensor.config.RabbitMQConfig;
import com.example.sensor.model.ElectricCharger;
import com.example.sensor.service.ChargingDataSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChargingDataSenderImpl implements ChargingDataSender {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ChargingDataSenderImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void sendChargingData(ElectricCharger charger) {
        try {
            String json = objectMapper.writeValueAsString(charger);
            rabbitTemplate.convertAndSend(RabbitMQConfig.CHARGING_QUEUE, json);
            System.out.println("📤 Poslata poruka: " + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
