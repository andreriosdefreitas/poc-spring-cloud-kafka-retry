package com.rios.pocspringcloudkafkaretry.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface OderConsumerDLQ {

    String CHANNEL = "ordersReceivedDLQ";

    @Input(CHANNEL)
    SubscribableChannel ordersReceivedDLQ();

}
