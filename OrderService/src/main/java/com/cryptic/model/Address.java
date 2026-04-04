package com.cryptic.model;
//added for testing
public record Address(
        Long id,
        String label,
        String line1,
        String city,
        String pincode,
        boolean isDefault
) {}
