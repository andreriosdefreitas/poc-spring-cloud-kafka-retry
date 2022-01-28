package com.rios.pocweb.web;

import com.rios.pocweb.dto.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order-receiver")
public class OrderReceiverController {

    @PostMapping
    public ResponseEntity<Order> receiveOrder(@RequestBody Order order) {
        log.info("Order received: {}", order.getId());
        return ResponseEntity.ok(order);
    }
}
