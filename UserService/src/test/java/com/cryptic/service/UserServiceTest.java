package com.cryptic.service;

import com.cryptic.model.Address;
import com.cryptic.model.User;
import com.cryptic.repo.AddressRepository;
import com.cryptic.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    AddressRepository addressRepository;

    @InjectMocks
    UserService userService;

    private User alice;
    private Address homeAddress;
    private Address workAddress;

    @BeforeEach
    void setUp() {
        alice = new User("Alice", "alice@example.com", "9000000001");
        homeAddress = new Address(alice, "Home", "12 MG Road", "Bengaluru", "560001", true);
        workAddress = new Address(alice, "Work", "91 Koramangala", "Bengaluru", "560034", false);
    }

    @Test
    void findById_existingUser_returnsProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));

        Optional<User> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alice");
        assertThat(result.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void findById_unknownUser_returnsEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThat(userService.findById(999L)).isEmpty();
    }

    @Test
    void getDefaultAddress_returnsAddressMarkedDefault() {
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L))
                .thenReturn(Optional.of(homeAddress));

        Optional<Address> addr = userService.getDefaultAddress(1L);

        assertThat(addr).isPresent();
        assertThat(addr.get().isDefault()).isTrue();
        assertThat(addr.get().getCity()).isEqualTo("Bengaluru");
    }

    @Test
    void getAddresses_returnsAllAddressesForUser() {
        when(addressRepository.findByUserId(1L))
                .thenReturn(List.of(homeAddress, workAddress));

        assertThat(userService.getAddresses(1L)).hasSize(2);
    }

    @Test
    void getAddresses_unknownUser_returnsEmptyList() {
        when(addressRepository.findByUserId(999L)).thenReturn(List.of());

        assertThat(userService.getAddresses(999L)).isEmpty();
    }
}
