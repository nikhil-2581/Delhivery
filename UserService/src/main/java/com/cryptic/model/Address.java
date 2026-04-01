package com.cryptic.model;

public record Address(
        Long id,
        String label,        // "Home", "Work", etc.
        String line1,
        String city,
        String pincode,
        boolean isDefault
) {}
