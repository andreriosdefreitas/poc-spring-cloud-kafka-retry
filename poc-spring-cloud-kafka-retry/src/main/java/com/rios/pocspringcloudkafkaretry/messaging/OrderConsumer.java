package com.rios.pocspringcloudkafkaretry.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface OrderConsumer {

    String CHANNEL = "ordersReceived";

    @Input(CHANNEL)
    SubscribableChannel ordersReceived();
}
