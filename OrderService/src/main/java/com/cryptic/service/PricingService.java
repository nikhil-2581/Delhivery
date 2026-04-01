package com.cryptic.service;

import com.cryptic.dto.OrderItemRequest;
import com.cryptic.model.PricingSummary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PricingService {
    private static final double DELIVERY_FEE = 30.0;
    private static final double FREE_DELIVERY_THRESHOLD = 299.0;

    private static final Map<String, Double> COUPONS = Map.of(
            "FIRST10", 10.0,   // flat ₹10
            "SAVE50",  50.0,
            "FLAT100", 100.0
    );

    public PricingSummary calculate(List<OrderItemRequest> items, String couponCode) {
        double subtotal = items.stream()
                .mapToDouble(i -> i.unitPrice() * i.quantity())
                .sum();

        double discount = resolveCoupon(couponCode, subtotal);
        double deliveryFee = subtotal >= FREE_DELIVERY_THRESHOLD ? 0.0 : DELIVERY_FEE;
        double total = Math.max(0, subtotal - discount) + deliveryFee;

        return new PricingSummary(subtotal, deliveryFee, discount, total);
    }

    private double resolveCoupon(String code, double subtotal) {
        if (code == null || code.isBlank()) return 0.0;
        Double flat = COUPONS.get(code.toUpperCase());
        if (flat == null) throw new IllegalArgumentException("Invalid coupon: " + code);
        if (flat > subtotal) throw new IllegalArgumentException(
                "Coupon discount cannot exceed order value");
        return flat;
    }
}
