package com.cryptic.service;

import com.cryptic.dto.OrderItemRequest;
import com.cryptic.model.PricingSummary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class PricingService {

    private static final BigDecimal DELIVERY_FEE = new BigDecimal("30.00");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("299.00");

    private static final Map<String, BigDecimal> COUPONS = Map.of(
            "FIRST10", new BigDecimal("10.00"),
            "SAVE50",  new BigDecimal("50.00"),
            "FLAT100", new BigDecimal("100.00")
    );

    public PricingSummary calculate(List<OrderItemRequest> items, String couponCode) {
        BigDecimal subtotal = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal discount = resolveCoupon(couponCode, subtotal);
        BigDecimal deliveryFee = subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0
                ? BigDecimal.ZERO : DELIVERY_FEE;
        BigDecimal total = subtotal.subtract(discount).add(deliveryFee)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        return new PricingSummary(subtotal, deliveryFee, discount, total);
    }

    private BigDecimal resolveCoupon(String code, BigDecimal subtotal) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;
        BigDecimal flat = COUPONS.get(code.toUpperCase());
        if (flat == null) throw new IllegalArgumentException("Invalid coupon: " + code);
        if (flat.compareTo(subtotal) > 0) throw new IllegalArgumentException(
                "Coupon discount cannot exceed order value");
        return flat;
    }
}