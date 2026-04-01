package com.cryptic.dto;

public record OrderItemRequest(
        String name,
        int quantity,
        double unitPrice
) {}
