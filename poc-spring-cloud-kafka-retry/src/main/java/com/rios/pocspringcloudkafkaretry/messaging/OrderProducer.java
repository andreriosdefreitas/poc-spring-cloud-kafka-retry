package com.rios.pocspringcloudkafkaretry.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrderProducer {

    @Output("ordersToSend")
    MessageChannel ordersToSend();

}
