package com.cryptic.model;

import java.util.List;

public record FoodPreferences(
        boolean isVegetarian,
        List<String> allergies,   // e.g. ["nuts", "dairy"]
        List<String> cuisines     // e.g. ["South Indian", "Chinese"]
) {}
