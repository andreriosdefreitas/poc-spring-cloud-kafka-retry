package com.rios.pocspringcloudkafkaretry.repository;

import com.rios.pocspringcloudkafkaretry.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
}
