package com.cryptic.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "food_preferences")
public class FoodPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_vegetarian", nullable = false)
    private boolean isVegetarian;

    @Column(columnDefinition = "TEXT[]")
    private List<String> allergies;

    @Column(columnDefinition = "TEXT[]")
    private List<String> cuisines;

    protected FoodPreferences() {}

    public FoodPreferences(User user, boolean isVegetarian,
                           List<String> allergies, List<String> cuisines) {
        this.user = user;
        this.isVegetarian = isVegetarian;
        this.allergies = allergies;
        this.cuisines = cuisines;
    }

    public boolean isVegetarian() { return isVegetarian; }
    public List<String> getAllergies() { return allergies; }
    public List<String> getCuisines() { return cuisines; }
}
