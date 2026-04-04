package com.cryptic.model;
//added for testing
import java.util.List;

public record UserProfile(
        Long id,
        String name,
        String email,
        String phone,
        List<Address> addresses,
        FoodPreferences preferences
) {}
