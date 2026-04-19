package com.cryptic.service;

import com.cryptic.client.NotificationClient;
import com.cryptic.client.UserClient;
import com.cryptic.dto.PlaceOrderRequest;
import com.cryptic.model.*;
import com.cryptic.repo.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final PricingService pricingService;
    private final OrderStatusService statusService;
    private final NotificationClient notificationClient;

    public OrderService(OrderRepository orderRepository,
                        UserClient userClient,
                        PricingService pricingService,
                        OrderStatusService statusService,
                        NotificationClient notificationClient) {
        this.orderRepository = orderRepository;
        this.userClient = userClient;
        this.pricingService = pricingService;
        this.statusService = statusService;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public Order place(PlaceOrderRequest req) {
        UserProfile user = userClient.getUser(req.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + req.userId()));

        Address delivery = userClient.getDefaultAddress(req.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No default address for user: " + req.userId()));

        PricingSummary pricing = pricingService.calculate(req.items(), req.couponCode());

        Order order = new Order(
                req.userId(),
                OrderStatus.PLACED,
                delivery.getLine1() + ", " + delivery.getCity(),
                pricing.subtotal(), pricing.deliveryFee(),
                pricing.discount(), pricing.total()
        );

        req.items().forEach(i ->
                order.getItems().add(new OrderItem(order, i.name(), i.quantity(), i.unitPrice()))
        );

        Order saved = orderRepository.save(order);
        notificationClient.notifyOrderPlaced(user.getEmail(), saved);
        return saved;
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        statusService.validate(order.getStatus(), newStatus);
        order.updateStatus(newStatus);

        Order saved = orderRepository.save(order);
        userClient.getUser(order.getUserId())
                .ifPresent(u -> notificationClient.notifyStatusChange(u.getEmail(), saved));
        return saved;
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
