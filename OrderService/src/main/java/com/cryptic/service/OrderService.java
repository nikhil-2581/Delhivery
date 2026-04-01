package com.cryptic.service;

import com.cryptic.client.NotificationClient;
import com.cryptic.client.UserClient;
import com.cryptic.dto.PlaceOrderRequest;
import com.cryptic.model.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    private final UserClient userClient;
    private final PricingService pricingService;
    private final OrderStatusService statusService;
    private final NotificationClient notificationClient;

    private final Map<Long, Order> orders = new LinkedHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public OrderService(UserClient userClient,
                        PricingService pricingService,
                        OrderStatusService statusService,
                        NotificationClient notificationClient) {
        this.userClient = userClient;
        this.pricingService = pricingService;
        this.statusService = statusService;
        this.notificationClient = notificationClient;
    }

    public Order place(PlaceOrderRequest req) {
        UserProfile user = userClient.getUser(req.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + req.userId()));

        Address delivery = userClient.getDefaultAddress(req.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No default address for user: " + req.userId()));

        PricingSummary pricing = pricingService.calculate(req.items(), req.couponCode());

        List<OrderItem> items = req.items().stream()
                .map(i -> new OrderItem(i.name(), i.quantity(), i.unitPrice()))
                .toList();

        Order order = new Order(
                idGen.getAndIncrement(),
                req.userId(),
                items,
                pricing,
                OrderStatus.PLACED,
                delivery.line1() + ", " + delivery.city(),
                Instant.now(),
                Instant.now()
        );

        orders.put(order.id(), order);
        notificationClient.notifyOrderPlaced(user.email(), order);
        return order;
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        statusService.validate(order.status(), newStatus);

        Order updated = new Order(
                order.id(), order.userId(), order.items(), order.pricing(),
                newStatus, order.deliveryAddress(), order.placedAt(), Instant.now()
        );
        orders.put(updated.id(), updated);

        UserProfile user = userClient.getUser(order.userId()).orElse(null);
        if (user != null) {
            notificationClient.notifyStatusChange(user.email(), updated);
        }

        return updated;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> findByUser(Long userId) {
        return orders.values().stream()
                .filter(o -> o.userId().equals(userId))
                .toList();
    }
}
