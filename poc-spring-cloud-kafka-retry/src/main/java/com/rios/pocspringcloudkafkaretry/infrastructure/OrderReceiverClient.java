package com.rios.pocspringcloudkafkaretry.infrastructure;

import com.rios.pocspringcloudkafkaretry.domain.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.ConnectException;

@FeignClient(value = "orderReceiver", url = "http://localhost:8081")
public interface OrderReceiverClient {

    @PostMapping("/order-receiver")
    Order sendToOrderReceiver(@RequestBody Order order) throws ConnectException;
}
