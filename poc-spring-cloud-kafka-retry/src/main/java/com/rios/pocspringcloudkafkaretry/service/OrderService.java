package com.rios.pocspringcloudkafkaretry.service;

import com.rios.pocspringcloudkafkaretry.domain.Order;
import com.rios.pocspringcloudkafkaretry.messaging.OrderProducer;
import com.rios.pocspringcloudkafkaretry.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public Order create() {
        Order order = orderRepository.save(new Order(UUID.randomUUID()));
        log.info("Order created: {}", order.getId());

        orderProducer.ordersToSend().send(MessageBuilder.withPayload(order).build());
        log.info("Order sent to kafka: {}", order.getId());
        return order;
    }
}
