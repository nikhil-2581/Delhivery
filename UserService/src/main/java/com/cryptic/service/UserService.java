package com.cryptic.service;

import com.cryptic.model.Address;
import com.cryptic.model.FoodPreferences;
import com.cryptic.model.UserProfile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final Map<Long, UserProfile> USERS = Map.of(
            1L, new UserProfile(
                    1L, "Alice", "alice@example.com", "9000000001",
                    List.of(
                            new Address(1L, "Home", "12 MG Road", "Bengaluru", "560001", true),
                            new Address(2L, "Work", "91 Koramangala", "Bengaluru", "560034", false)
                    ),
                    new FoodPreferences(false, List.of(), List.of("North Indian", "Chinese"))
            ),
            2L, new UserProfile(
                    2L, "Bob", "bob@example.com", "9000000002",
                    List.of(new Address(3L, "Home", "5 Anna Nagar", "Chennai", "600040", true)),
                    new FoodPreferences(true, List.of("nuts"), List.of("South Indian"))
            )
    );

    public Optional<UserProfile> findById(Long id) {
        return Optional.ofNullable(USERS.get(id));
    }

    public Optional<Address> getDefaultAddress(Long userId) {
        return findById(userId)
                .flatMap(u -> u.addresses().stream()
                        .filter(Address::isDefault)
                        .findFirst());
    }

    public List<Address> getAddresses(Long userId) {
        return findById(userId)
                .map(UserProfile::addresses)
                .orElse(List.of());
    }
}
