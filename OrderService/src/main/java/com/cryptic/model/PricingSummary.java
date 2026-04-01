package com.cryptic.model;

public record PricingSummary(
        double subtotal,
        double deliveryFee,
        double discount,
        double total
) {}
