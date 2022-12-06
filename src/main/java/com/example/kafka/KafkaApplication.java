package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
@Slf4j
public class KafkaApplication {

	static class EnvironmentPrepared implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
		@Override
		public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
			String kafkaBrokerLocal = "localhost:9092";
			Boolean useProduction = event.getEnvironment().getProperty("use-production",Boolean.class, Boolean.FALSE);
			String kafkaBrokers = event.getEnvironment().getProperty("kafka.producer.brokers",String.class, kafkaBrokerLocal);

			log.info("use-production: {}", useProduction);
			log.info("kafka broker: {}", kafkaBrokers);
			if (!kafkaBrokers.equals(kafkaBrokerLocal) && !useProduction) {
				throw new RuntimeException("APPLICATION FAILED TO START: BROKER=PRODUCTION, use-production must be true");
			}
		}
	}


	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(KafkaApplication.class);
		springApplication.addListeners(new KafkaApplication.EnvironmentPrepared());
		springApplication.run(args);
	}

}
