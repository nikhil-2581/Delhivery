package com.cryptic.dto;

public record RegisterRequest(
        String name,
        String email,
        String phone,
        String password,
        String role       // "CUSTOMER", "DRIVER", "ADMIN"
) {}
