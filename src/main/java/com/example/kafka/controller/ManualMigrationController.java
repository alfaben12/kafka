package com.example.kafka.controller;

import com.example.kafka.config.KafkaProducerConfig;
import com.example.kafka.properties.KafkaProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
public class ManualMigrationController {

    private ReactiveKafkaProducerTemplate<String, String> producer;

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        producer = kafkaProducerConfig.initProducer(String.class);
    }

    @GetMapping("/migrate")
    public Mono<String> migrate() throws IOException {
        BufferedReader reader;
        String filePath = kafkaProperties.getMigrate().getPath();
        reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();
        Integer counter = 0;
        while (line != null) {
            List<String> row = Stream.of(line.split(",", -1)).collect(Collectors.toList());
            Map payload = new HashMap<>();
            payload.put("ajaib_id", Integer.valueOf(row.get(0)));
            payload.put("account_type", "R");
            String payloadString = objectMapper.writeValueAsString(payload);

            log.info(counter.toString());
            producer.send(kafkaProperties.getProducer().getTopic().getMigrate(), payloadString).doOnSuccess(v -> log.info(v.toString())).subscribe();

            line = reader.readLine();
            counter++;
        }
        reader.close();

        return Mono.just(counter.toString());
    }

    @GetMapping("/update-status/{status}")
    public Mono<String> updateStatus(@PathVariable("status") String status) throws IOException {
        BufferedReader reader;
        String filePath = "";
        if (status.equals("TRANSACTION")) {
            filePath = kafkaProperties.getUpdateStatus().getTransaction().getPath();
        } else if (status.equals("RDN")) {
            filePath = kafkaProperties.getUpdateStatus().getRdn().getPath();
        } else if (status.equals("FOP")) {
            filePath = kafkaProperties.getUpdateStatus().getFop().getPath();
        } else if (status.equals("S21")) {
            filePath = kafkaProperties.getUpdateStatus().getS21().getPath();
        } else if (status.equals("PORTFOLIO")) {
            filePath = kafkaProperties.getUpdateStatus().getPortfolio().getPath();
        } else {
            return Mono.just("Account not found!");
        }

        reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();
        Integer counter = 0;
        while (line != null) {
            List<String> row = Stream.of(line.split(",", -1)).collect(Collectors.toList());
            Map payload = new HashMap<>();
            payload.put("ajaib_id", Integer.valueOf(row.get(0)));
            payload.put("migration_status", "DONE");
            payload.put("account", status);

            String payloadString = objectMapper.writeValueAsString(payload);

            log.info(counter.toString());
            producer.send(kafkaProperties.getProducer().getTopic().getUpdateStatus(), payloadString).doOnSuccess(v -> log.info(v.toString())).subscribe();

            line = reader.readLine();
            counter++;
        }
        reader.close();

        return Mono.just(filePath);
    }
}
