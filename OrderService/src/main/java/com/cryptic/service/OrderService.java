package com.cryptic.service;

import com.cryptic.client.UserClient;
import com.cryptic.model.Order;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    private final UserClient userClient;
    private final Map<Long, Order> orders = new LinkedHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public OrderService(UserClient userClient) {
        this.userClient = userClient;
    }

    public Order create(Long userId, String item) {
        if (!userClient.userExists(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        Long id = idGen.getAndIncrement();
        Order order = new Order(id, userId, item, "PLACED");
        orders.put(id, order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }
}
