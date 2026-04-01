package com.cryptic.model;

import java.time.Instant;
import java.util.List;

public record Order(
        Long id,
        Long userId,
        List<OrderItem> items,
        PricingSummary pricing,
        OrderStatus status,
        String deliveryAddress,
        Instant placedAt,
        Instant updatedAt
) {}
