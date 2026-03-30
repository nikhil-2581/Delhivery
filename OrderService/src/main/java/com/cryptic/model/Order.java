package com.cryptic.model;

public record Order(Long id, Long userId, String item, String status) {}
