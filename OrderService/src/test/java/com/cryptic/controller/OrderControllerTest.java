package com.cryptic.controller;

import com.cryptic.model.Order;
import com.cryptic.model.OrderItem;
import com.cryptic.model.OrderStatus;
import com.cryptic.model.PricingSummary;
import com.cryptic.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean OrderService orderService;

    private Order sampleOrder(OrderStatus status) {
        return new Order(
                1L, status, "12 MG Road, Bengaluru",
                new BigDecimal("360.00"),
                new BigDecimal("0.00"),
                new BigDecimal("0.00"),
                new BigDecimal("360.00")
        );
    }

    @Test
    void placeOrder_validRequest_returns200() throws Exception {
        when(orderService.place(any())).thenReturn(sampleOrder(OrderStatus.PLACED));

        String body = """
            {
              "userId": 1,
              "items": [{"name": "Butter Chicken", "quantity": 2, "unitPrice": 180.0}],
              "couponCode": null
            }
            """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.total").value(360.0));
    }

    @Test
    void placeOrder_invalidUser_returns400() throws Exception {
        when(orderService.place(any()))
                .thenThrow(new IllegalArgumentException("User not found: 99"));

        String body = """
            {"userId": 99, "items": [{"name": "X", "quantity": 1, "unitPrice": 100.0}]}
            """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User not found: 99"));
    }

    @Test
    void getOrder_exists_returns200() throws Exception {
        when(orderService.findById(1L)).thenReturn(Optional.of(sampleOrder(OrderStatus.CONFIRMED)));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void getOrder_notFound_returns404() throws Exception {
        when(orderService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_legalTransition_returns200() throws Exception {
        when(orderService.updateStatus(1L, OrderStatus.CONFIRMED))
                .thenReturn(sampleOrder(OrderStatus.CONFIRMED));

        mockMvc.perform(patch("/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void updateStatus_illegalTransition_returns400() throws Exception {
        when(orderService.updateStatus(any(), any()))
                .thenThrow(new IllegalStateException("Cannot transition from PREPARING to CANCELLED"));

        mockMvc.perform(patch("/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"CANCELLED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot transition from PREPARING to CANCELLED"));
    }
}
