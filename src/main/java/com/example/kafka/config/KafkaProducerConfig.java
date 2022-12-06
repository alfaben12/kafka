package com.example.kafka.config;

import com.example.kafka.properties.KafkaProperties;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaProducerConfig {
    @Autowired
    private KafkaProperties kafkaProperties;

    public <T> ReactiveKafkaProducerTemplate<String, T> initProducer(Class<T> deserializer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getProducer().getBrokers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }
}
