package com.cryptic.model;
//added for testing
//Mirror DTO of UserProfile from UserService for order service

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private List<Address> addresses = new ArrayList<>();
    private FoodPreferences preferences;

    protected UserProfile() {}

    public UserProfile(Long id, String name, String email, String phone,
                       List<Address> addresses, FoodPreferences preferences) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.addresses = addresses;
        this.preferences = preferences;
    }

    public Long getId()                    { return id; }
    public String getName()               { return name; }
    public String getEmail()              { return email; }
    public String getPhone()              { return phone; }
    public List<Address> getAddresses()   { return addresses; }
    public FoodPreferences getPreferences() { return preferences; }
}
