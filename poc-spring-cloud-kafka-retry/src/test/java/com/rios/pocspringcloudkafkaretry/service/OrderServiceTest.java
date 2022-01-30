package com.rios.pocspringcloudkafkaretry.service;

import com.rios.pocspringcloudkafkaretry.domain.Order;
import com.rios.pocspringcloudkafkaretry.infrastructure.OrderReceiverClient;
import com.rios.pocspringcloudkafkaretry.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import java.util.UUID;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Mock
    private OrderReceiverClient orderReceiverClient;

    @Container
    public static PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>("postgres")
            .withPassword("dbpass")
            .withUsername("postgres");

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgresSQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgresSQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgresSQLContainer.getPassword());
        registry.add("spring.cloud.stream.kafka.binder.brokers", () -> kafkaContainer.getBootstrapServers());
    }

    @Test
    void Given_ValidOrder_When_CreateOrder_Then_ReceiveOKFromOrderReceiver() {
        UUID uuid = UUID.randomUUID();
        Order order = new Order(uuid);

        Mockito.when(orderReceiverClient.sendToOrderReceiver(order)).thenReturn(order);

        orderService.create(uuid);

        Assertions.assertEquals(1, orderRepository.findAll().size());

    }

}