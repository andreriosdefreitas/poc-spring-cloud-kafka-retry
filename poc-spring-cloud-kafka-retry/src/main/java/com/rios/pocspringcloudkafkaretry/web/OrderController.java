package com.rios.pocspringcloudkafkaretry.web;

import com.rios.pocspringcloudkafkaretry.domain.Order;
import com.rios.pocspringcloudkafkaretry.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder() {
        Order saved = orderService.create();
        return ResponseEntity.ok(saved);
    }
}
