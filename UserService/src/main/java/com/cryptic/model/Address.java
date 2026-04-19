package com.cryptic.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String line1;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String pincode;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    protected Address() {}

    public Address(User user, String label, String line1,
                   String city, String pincode, boolean isDefault) {
        this.user = user;
        this.label = label;
        this.line1 = line1;
        this.city = city;
        this.pincode = pincode;
        this.isDefault = isDefault;
    }

    public Long getId()       { return id; }
    public String getLabel()  { return label; }
    public String getLine1()  { return line1; }
    public String getCity()   { return city; }
    public String getPincode(){ return pincode; }

    @JsonProperty("isDefault")
    public boolean isDefault() { return isDefault; }
}
