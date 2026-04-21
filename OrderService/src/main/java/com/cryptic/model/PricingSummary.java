package com.cryptic.model;

import java.math.BigDecimal;

public record PricingSummary(
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal discount,
        BigDecimal total
) {}
