package com.cryptic.service;

import com.cryptic.client.NotificationClient;
import com.cryptic.client.UserClient;
import com.cryptic.dto.OrderItemRequest;
import com.cryptic.dto.PlaceOrderRequest;
import com.cryptic.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock UserClient userClient;
    @Mock NotificationClient notificationClient;
    @Spy PricingService pricingService = new PricingService();
    @Spy  OrderStatusService statusService = new OrderStatusService();

    @InjectMocks
    OrderService orderService;

    private UserProfile alice;
    private Address defaultAddress;

    @BeforeEach
    void setUp() {
        defaultAddress = new Address(1L, "Home", "12 MG Road", "Bengaluru", "560001", true);
        alice = new UserProfile(
                1L, "Alice", "alice@example.com", "9000000001",
                List.of(defaultAddress),
                new FoodPreferences(false, List.of(), List.of())
        );
    }

    @Test
    void place_validRequest_createsOrderWithPricing() {
        when(userClient.getUser(1L)).thenReturn(Optional.of(alice));
        when(userClient.getDefaultAddress(1L)).thenReturn(Optional.of(defaultAddress));

        PlaceOrderRequest req = new PlaceOrderRequest(
                1L,
                List.of(new OrderItemRequest("Butter Chicken", 2, 180.0)),
                null
        );

        Order result = orderService.place(req);

        assertThat(result.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(result.pricing().subtotal()).isEqualTo(360.0);
        assertThat(result.deliveryAddress()).contains("Bengaluru");
        verify(notificationClient).notifyOrderPlaced(eq("alice@example.com"), any());
    }

    @Test
    void place_unknownUser_throwsException() {
        when(userClient.getUser(99L)).thenReturn(Optional.empty());

        PlaceOrderRequest req = new PlaceOrderRequest(
                99L, List.of(new OrderItemRequest("Item", 1, 100.0)), null);

        assertThatThrownBy(() -> orderService.place(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        verifyNoInteractions(notificationClient);
    }

    @Test
    void place_noDefaultAddress_throwsException() {
        when(userClient.getUser(1L)).thenReturn(Optional.of(alice));
        when(userClient.getDefaultAddress(1L)).thenReturn(Optional.empty());

        PlaceOrderRequest req = new PlaceOrderRequest(
                1L, List.of(new OrderItemRequest("Item", 1, 100.0)), null);

        assertThatThrownBy(() -> orderService.place(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No default address");
    }

    @Test
    void updateStatus_legalTransition_updatesOrder() {
        when(userClient.getUser(1L)).thenReturn(Optional.of(alice));
        when(userClient.getDefaultAddress(1L)).thenReturn(Optional.of(defaultAddress));

        Order placed = orderService.place(new PlaceOrderRequest(
                1L, List.of(new OrderItemRequest("Dosa", 1, 80.0)), null));

        Order confirmed = orderService.updateStatus(placed.id(), OrderStatus.CONFIRMED);

        assertThat(confirmed.status()).isEqualTo(OrderStatus.CONFIRMED);
        verify(notificationClient).notifyStatusChange(eq("alice@example.com"), any());
    }

    @Test
    void updateStatus_illegalTransition_throwsAndDoesNotNotify() {
        when(userClient.getUser(1L)).thenReturn(Optional.of(alice));
        when(userClient.getDefaultAddress(1L)).thenReturn(Optional.of(defaultAddress));

        Order placed = orderService.place(new PlaceOrderRequest(
                1L, List.of(new OrderItemRequest("Dosa", 1, 80.0)), null));

        // Skip to PREPARING first (so CANCELLED is illegal)
        orderService.updateStatus(placed.id(), OrderStatus.CONFIRMED);
        orderService.updateStatus(placed.id(), OrderStatus.PREPARING);

        assertThatThrownBy(() ->
                orderService.updateStatus(placed.id(), OrderStatus.CANCELLED))
                .isInstanceOf(IllegalStateException.class);

        // Notification was called for CONFIRMED and PREPARING, but NOT for the failed CANCELLED
        verify(notificationClient, times(2)).notifyStatusChange(any(), any());
    }

    @Test
    void findByUser_returnsOnlyOrdersForThatUser() {
        when(userClient.getUser(1L)).thenReturn(Optional.of(alice));
        when(userClient.getDefaultAddress(1L)).thenReturn(Optional.of(defaultAddress));

        orderService.place(new PlaceOrderRequest(
                1L, List.of(new OrderItemRequest("Idli", 2, 50.0)), null));
        orderService.place(new PlaceOrderRequest(
                1L, List.of(new OrderItemRequest("Vada", 1, 40.0)), null));

        assertThat(orderService.findByUser(1L)).hasSize(2);
        assertThat(orderService.findByUser(2L)).isEmpty();
    }
}
