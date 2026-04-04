package com.cryptic.service;

import com.cryptic.dto.OrderItemRequest;
import com.cryptic.model.PricingSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    private List<OrderItemRequest> items(double... prices) {
        return java.util.Arrays.stream(prices)
                .mapToObj(p -> new OrderItemRequest("Item", 1, p))
                .toList();
    }

    @Test
    void calculate_noDiscount_addsDeliveryFee() {
        PricingSummary result = pricingService.calculate(items(100.0), null);
        assertThat(result.subtotal()).isEqualTo(100.0);
        assertThat(result.deliveryFee()).isEqualTo(30.0);
        assertThat(result.discount()).isEqualTo(0.0);
        assertThat(result.total()).isEqualTo(130.0);
    }

    @Test
    void calculate_aboveThreshold_freeDelivery() {
        PricingSummary result = pricingService.calculate(items(300.0), null);
        assertThat(result.deliveryFee()).isEqualTo(0.0);
        assertThat(result.total()).isEqualTo(300.0);
    }

    @Test
    void calculate_validCoupon_appliesDiscount() {
        PricingSummary result = pricingService.calculate(items(200.0), "SAVE50");
        assertThat(result.discount()).isEqualTo(50.0);
        assertThat(result.total()).isEqualTo(180.0); // 200 - 50 + 30 delivery
    }

    @Test
    void calculate_invalidCoupon_throwsException() {
        assertThatThrownBy(() -> pricingService.calculate(items(200.0), "FAKE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid coupon");
    }

    @Test
    void calculate_couponExceedsSubtotal_throwsException() {
        assertThatThrownBy(() -> pricingService.calculate(items(30.0), "SAVE50"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot exceed");
    }

    @Test
    void calculate_multipleItems_subtotalSumsCorrectly() {
        List<OrderItemRequest> items = List.of(
                new OrderItemRequest("Butter Chicken", 2, 180.0),
                new OrderItemRequest("Naan", 3, 40.0)
        );
        PricingSummary result = pricingService.calculate(items, null);
        assertThat(result.subtotal()).isEqualTo(480.0); // 360 + 120
        assertThat(result.deliveryFee()).isEqualTo(0.0); // above threshold
    }

    @Test
    void calculate_blankCouponCode_treatedAsNoCoupon() {
        PricingSummary result = pricingService.calculate(items(100.0), "   ");
        assertThat(result.discount()).isEqualTo(0.0);
    }
}
