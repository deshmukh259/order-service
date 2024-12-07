package com.pd.order_service.adaptor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerAdaptor {


    // @KafkaListener(topics = "shipment-order",groupId = "alpha1")
    @KafkaListener(topics = "order_inventory_topic", groupId = "alpha1")
    public void consumeMessage(String message) {
        System.out.println("my first message received : " + message);
    }
}
