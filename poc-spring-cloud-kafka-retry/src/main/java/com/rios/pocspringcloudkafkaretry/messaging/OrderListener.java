package com.rios.pocspringcloudkafkaretry.messaging;

import com.rios.pocspringcloudkafkaretry.domain.Order;
import com.rios.pocspringcloudkafkaretry.infrastructure.OrderReceiverClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListener {

    private final OrderReceiverClient orderReceiverClient;

    @StreamListener(OrderConsumer.CHANNEL)
    public void listen(Message<Order> message) {
        Order order = message.getPayload();
        log.info("Sending order to Order Receiver: {}", order.getId());

        Order receivedOrder = orderReceiverClient.sendToOrderReceiver(order);
        log.info("Order Receiver response: {}", receivedOrder.getId());
    }
}
