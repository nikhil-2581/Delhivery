package com.cryptic.dto;

import java.util.List;

public record PlaceOrderRequest(
        Long userId,
        List<OrderItemRequest> items,
        String couponCode         // nullable
) {}
