package com.rios.pocspringcloudkafkaretry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rios.pocspringcloudkafkaretry.domain.Order;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    public static PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>("postgres")
            .withPassword("dbpass")
            .withUsername("postgres");

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("kymeric/cp-kafka").asCompatibleSubstituteFor("confluentinc/cp-kafka")).withExposedPorts(9093);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgresSQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgresSQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgresSQLContainer.getPassword());
        registry.add("spring.cloud.stream.kafka.binder.brokers", () -> kafkaContainer.getBootstrapServers());
        registry.add( "spring.autoconfigure.exclude", () -> "org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration");
    }

    public Producer<String, String> getKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaContainer.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public Consumer<String, String> getKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Test
    void Given_ValidOrder_When_CreateOrderAndOrderReceiverIsNotAvailable_Then_SendTheMessageToDLQAfterRetries() throws IOException {

        //given
        UUID uuid = UUID.randomUUID();
        Order order = new Order(uuid);

        Consumer<String, String> dlqConsumer = getKafkaConsumer();
        dlqConsumer.subscribe(List.of("error.orders-to-send.orderReceivedGroup"));

        //when
        Producer<String, String> kafkaProducer = getKafkaProducer();
        kafkaProducer.send(new ProducerRecord<>("orders-to-send", objectMapper.writeValueAsString(order)));

        //then
        ConsumerRecords<String, String> records = dlqConsumer.poll(Duration.ofSeconds(10));
        Assertions.assertEquals(1, records.count());
        records.forEach(record -> {
            try {
                Order received = objectMapper.readValue(record.value(), Order.class);
                Assertions.assertEquals(uuid, received.getId());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

    }

}