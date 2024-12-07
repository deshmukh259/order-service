package com.pd.order_service.adaptor;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaAdaptor {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaAdaptor(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendMessage(String topic, String orderEvent) {

        kafkaTemplate.send(topic, orderEvent);
        System.out.println("Send message " + orderEvent);
    }
}
