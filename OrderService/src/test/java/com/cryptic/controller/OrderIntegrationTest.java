package com.cryptic.controller;

import com.cryptic.client.NotificationClient;
import com.cryptic.client.UserClient;
import com.cryptic.model.Address;
import com.cryptic.model.FoodPreferences;
import com.cryptic.model.Order;
import com.cryptic.model.UserProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
                "org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration"
})
public class OrderIntegrationTest {

    @Autowired MockMvc mockMvc;

    @TestConfiguration
    static class Stubs {

        @Bean @Primary
        UserClient userClient() {
            return new UserClient(null, null) {
                @Override
                public Optional<UserProfile> getUser(Long id) {
                    if (id == 1L) return Optional.of(new UserProfile(
                            1L, "Alice", "alice@example.com", "9000000001",
                            List.of(), new FoodPreferences(false, List.of(), List.of())
                    ));
                    return Optional.empty();
                }

                @Override
                public Optional<Address> getDefaultAddress(Long id) {
                    if (id == 1L) return Optional.of(
                            new Address(1L, "Home", "12 MG Road", "Bengaluru", "560001", true));
                    return Optional.empty();
                }
            };
        }

        @Bean @Primary
        NotificationClient notificationClient() {
            return new NotificationClient(null, null) {
                @Override public void notifyOrderPlaced(String email, Order o) {}
                @Override public void notifyStatusChange(String email, Order o) {}
            };
        }
    }

    @Test
    void fullOrderLifecycle_placeThenConfirmThenDeliver() throws Exception {
        String placeBody = """
            {
              "userId": 1,
              "items": [{"name": "Masala Dosa", "quantity": 2, "unitPrice": 120.0}],
              "couponCode": "FIRST10"
            }
            """;

        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placeBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.discount").value(10.0))  // flat field now
                .andReturn().getResponse().getContentAsString();

        Long orderId = com.fasterxml.jackson.databind.json.JsonMapper.builder()
                .build().readTree(response).get("id").asLong();

        mockMvc.perform(patch("/orders/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        mockMvc.perform(patch("/orders/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"PREPARING\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/orders/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"CANCELLED\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/orders/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"OUT_FOR_DELIVERY\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/orders/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"DELIVERED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }
}
