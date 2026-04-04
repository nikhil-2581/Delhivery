package com.cryptic.service;

import com.cryptic.model.Address;
import com.cryptic.model.UserProfile;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest {
    private final UserService userService = new UserService();

    @Test
    void findById_existingUser_returnsProfile() {
        Optional<UserProfile> result = userService.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Alice");
        assertThat(result.get().email()).isEqualTo("alice@example.com");
    }

    @Test
    void findById_unknownUser_returnsEmpty() {
        assertThat(userService.findById(999L)).isEmpty();
    }

    @Test
    void getDefaultAddress_returnsAddressMarkedDefault() {
        Optional<Address> addr = userService.getDefaultAddress(1L);
        assertThat(addr).isPresent();
        assertThat(addr.get().isDefault()).isTrue();
        assertThat(addr.get().city()).isEqualTo("Bengaluru");
    }

    @Test
    void getAddresses_returnsAllAddressesForUser() {
        assertThat(userService.getAddresses(1L)).hasSize(2);
    }

    @Test
    void getAddresses_unknownUser_returnsEmptyList() {
        assertThat(userService.getAddresses(999L)).isEmpty();
    }
}
