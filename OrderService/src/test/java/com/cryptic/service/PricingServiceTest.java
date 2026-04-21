package com.cryptic.service;

import com.cryptic.dto.OrderItemRequest;
import com.cryptic.model.PricingSummary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    private List<OrderItemRequest> items(String... prices) {
        return java.util.Arrays.stream(prices)
                .map(p -> new OrderItemRequest("Item", 1, new BigDecimal(p)))
                .toList();
    }

    @Test
    void calculate_noDiscount_addsDeliveryFee() {
        PricingSummary result = pricingService.calculate(items("100.00"), null);
        assertThat(result.subtotal()).isEqualTo(new BigDecimal("100.00"));
        assertThat(result.deliveryFee()).isEqualTo(new BigDecimal("30.00"));
        assertThat(result.discount()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.total()).isEqualTo(new BigDecimal("130.00"));
    }

    @Test
    void calculate_aboveThreshold_freeDelivery() {
        PricingSummary result = pricingService.calculate(items("300.00"), null);
        assertThat(result.deliveryFee()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.total()).isEqualTo(new BigDecimal("300.00"));
    }

    @Test
    void calculate_validCoupon_appliesDiscount() {
        PricingSummary result = pricingService.calculate(items("200.00"), "SAVE50");
        assertThat(result.discount()).isEqualTo(new BigDecimal("50.00"));
        assertThat(result.total()).isEqualTo(new BigDecimal("180.00")); // 200 - 50 + 30 delivery
    }

    @Test
    void calculate_invalidCoupon_throwsException() {
        assertThatThrownBy(() -> pricingService.calculate(items("200.00"), "FAKE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid coupon");
    }

    @Test
    void calculate_couponExceedsSubtotal_throwsException() {
        assertThatThrownBy(() -> pricingService.calculate(items("30.00"), "SAVE50"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot exceed");
    }

    @Test
    void calculate_multipleItems_subtotalSumsCorrectly() {
        List<OrderItemRequest> items = List.of(
                new OrderItemRequest("Butter Chicken", 2, new BigDecimal("180.00")),
                new OrderItemRequest("Naan", 3, new BigDecimal("40.00"))
        );
        PricingSummary result = pricingService.calculate(items, null);
        assertThat(result.subtotal()).isEqualTo(new BigDecimal("480.00")); // 360 + 120
        assertThat(result.deliveryFee()).isEqualTo(new BigDecimal("0.00")); // above threshold
    }

    @Test
    void calculate_blankCouponCode_treatedAsNoCoupon() {
        PricingSummary result = pricingService.calculate(items("100.00"), "   ");
        assertThat(result.discount()).isEqualTo(new BigDecimal("0.00"));
    }
}
