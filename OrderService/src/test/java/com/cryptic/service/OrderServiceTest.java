package com.cryptic.service;

import com.cryptic.client.NotificationClient;
import com.cryptic.client.UserClient;
import com.cryptic.dto.OrderItemRequest;
import com.cryptic.dto.PlaceOrderRequest;
import com.cryptic.model.*;
import com.cryptic.repo.OrderRepository;
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
    @Mock OrderRepository orderRepository;
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

    private Order savedOrder(Long id, OrderStatus status) {
        Order o = new Order(1L, status, "12 MG Road, Bengaluru",
                360.0, 0.0, 0.0, 360.0);
        // simulate what DB would return with an id
        return o;
    }

    @Test
    void place_validRequest_createsOrderWithPricing() {
        when(userClient.getUser(1L)).thenReturn(Optional.of(alice));
        when(userClient.getDefaultAddress(1L)).thenReturn(Optional.of(defaultAddress));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PlaceOrderRequest req = new PlaceOrderRequest(
                1L,
                List.of(new OrderItemRequest("Butter Chicken", 2, 180.0)),
                null
        );

        Order result = orderService.place(req);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(result.getSubtotal()).isEqualTo(360.0);
        assertThat(result.getDeliveryAddress()).contains("Bengaluru");
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
        Order existing = new Order(1L, OrderStatus.PLACED,
                "12 MG Road, Bengaluru", 80.0, 30.0, 0.0, 110.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userClient.getUser(1L)).thenReturn(Optional.of(alice));

        Order confirmed = orderService.updateStatus(1L, OrderStatus.CONFIRMED);

        assertThat(confirmed.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(notificationClient).notifyStatusChange(eq("alice@example.com"), any());
    }

    @Test
    void updateStatus_illegalTransition_throwsAndDoesNotNotify() {
        Order existing = new Order(1L, OrderStatus.PREPARING,
                "12 MG Road, Bengaluru", 80.0, 30.0, 0.0, 110.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> orderService.updateStatus(1L, OrderStatus.CANCELLED))
                .isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(notificationClient);
    }

    @Test
    void findByUser_returnsOnlyOrdersForThatUser() {
        Order o1 = new Order(1L, OrderStatus.PLACED, "Addr", 100.0, 30.0, 0.0, 130.0);
        Order o2 = new Order(1L, OrderStatus.PLACED, "Addr", 50.0, 30.0, 0.0, 80.0);

        when(orderRepository.findByUserId(1L)).thenReturn(List.of(o1, o2));
        when(orderRepository.findByUserId(2L)).thenReturn(List.of());

        assertThat(orderService.findByUser(1L)).hasSize(2);
        assertThat(orderService.findByUser(2L)).isEmpty();
    }
}
