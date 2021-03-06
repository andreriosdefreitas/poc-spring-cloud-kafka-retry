package com.rios.pocspringcloudkafkaretry.messaging;

import com.rios.pocspringcloudkafkaretry.domain.Order;
import com.rios.pocspringcloudkafkaretry.infrastructure.OrderReceiverClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListener {

    private final OrderReceiverClient orderReceiverClient;

    @StreamListener(OrderConsumer.CHANNEL)
    public void listen(Message<Order> message) throws ConnectException {
        Order order = message.getPayload();
        log.info("Sending order to Order Receiver: {}", order.getId());

        log.info("Order: {} Delivery attempt: {}", order.getId(), message.getHeaders().get("deliveryAttempt"));
        Order receivedOrder = orderReceiverClient.sendToOrderReceiver(order);
        log.info("Order Receiver response: {}", receivedOrder.getId());
    }
}
