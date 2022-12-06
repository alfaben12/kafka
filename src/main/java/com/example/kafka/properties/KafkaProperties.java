package com.example.kafka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public class KafkaProperties {
    private Producer producer;
    private Migrate migrate;
    private UpdateStatus updateStatus;

    @Getter
    @Setter
    public static class Producer {
        private String brokers;
        private Topic topic;

        @Getter
        @Setter
        public static class Topic {
            private String migrate;
            private String updateStatus;
        }
    }

    @Getter
    @Setter
    public static class Migrate {
        private String path;
    }

    @Getter
    @Setter
    public static class UpdateStatus {
        private Transaction transaction;
        private Rdn rdn;
        private Fop fop;
        private S21 s21;
        private Portfolio portfolio;

        @Getter
        @Setter
        public static class Transaction {
            private String path;
        }

        @Getter
        @Setter
        public static class Rdn {
            private String path;
        }

        @Getter
        @Setter
        public static class Fop {
            private String path;
        }

        @Getter
        @Setter
        public static class S21 {
            private String path;
        }

        @Getter
        @Setter
        public static class Portfolio {
            private String path;
        }
    }
}
