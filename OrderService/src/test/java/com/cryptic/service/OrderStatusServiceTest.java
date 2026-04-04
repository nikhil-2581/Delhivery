package com.cryptic.service;

import com.cryptic.model.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderStatusServiceTest {

    private final OrderStatusService statusService = new OrderStatusService();

    @Test
    void validate_legalTransition_doesNotThrow() {
        assertThatNoException().isThrownBy(() ->
                statusService.validate(OrderStatus.PLACED, OrderStatus.CONFIRMED));
    }

    @Test
    void validate_illegalTransition_throwsException() {
        assertThatThrownBy(() ->
                statusService.validate(OrderStatus.PREPARING, OrderStatus.CANCELLED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot transition");
    }

    @Test
    void validate_cancelFromPlaced_isAllowed() {
        assertThatNoException().isThrownBy(() ->
                statusService.validate(OrderStatus.PLACED, OrderStatus.CANCELLED));
    }

    @Test
    void validate_cancelFromDelivered_throws() {
        assertThatThrownBy(() ->
                statusService.validate(OrderStatus.DELIVERED, OrderStatus.CANCELLED))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void validate_fullHappyPath_noExceptions() {
        assertThatNoException().isThrownBy(() -> {
            statusService.validate(OrderStatus.PLACED,          OrderStatus.CONFIRMED);
            statusService.validate(OrderStatus.CONFIRMED,       OrderStatus.PREPARING);
            statusService.validate(OrderStatus.PREPARING,       OrderStatus.OUT_FOR_DELIVERY);
            statusService.validate(OrderStatus.OUT_FOR_DELIVERY,OrderStatus.DELIVERED);
        });
    }
}
