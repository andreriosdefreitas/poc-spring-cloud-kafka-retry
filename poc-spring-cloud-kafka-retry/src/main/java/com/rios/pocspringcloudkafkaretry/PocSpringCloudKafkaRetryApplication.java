package com.rios.pocspringcloudkafkaretry;

import com.rios.pocspringcloudkafkaretry.messaging.OrderConsumer;
import com.rios.pocspringcloudkafkaretry.messaging.OrderProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding({OrderProducer.class, OrderConsumer.class})
@EnableFeignClients
@SpringBootApplication
public class PocSpringCloudKafkaRetryApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocSpringCloudKafkaRetryApplication.class, args);
	}

}
