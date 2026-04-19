package com.cryptic.controller;

import com.cryptic.controllers.UserController;
import com.cryptic.model.Address;
import com.cryptic.model.FoodPreferences;
import com.cryptic.model.User;
import com.cryptic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean  UserService userService;

    private User sampleUser() {
        return new User("Alice", "alice@example.com", "9000000001");
    }

    private Address sampleAddress(User user) {
        return new Address(user, "Home", "12 MG Road", "Bengaluru", "560001", true);
    }

    @Test
    void getUser_exists_returns200() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(sampleUser()));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDefaultAddress_exists_returns200() throws Exception {
        User user = sampleUser();
        Address addr = sampleAddress(user);
        when(userService.getDefaultAddress(1L)).thenReturn(Optional.of(addr));

        mockMvc.perform(get("/users/1/addresses/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Bengaluru"))
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    void getAddresses_returns200WithList() throws Exception {
        User user = sampleUser();
        when(userService.getAddresses(1L))
                .thenReturn(List.of(sampleAddress(user)));

        mockMvc.perform(get("/users/1/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
