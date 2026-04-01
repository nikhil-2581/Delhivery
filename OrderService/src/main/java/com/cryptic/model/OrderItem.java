package com.cryptic.model;

public record OrderItem(
        String name,
        int quantity,
        double unitPrice
) {}
