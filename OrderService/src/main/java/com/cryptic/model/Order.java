package com.cryptic.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private double subtotal;

    @Column(name = "delivery_fee", nullable = false)
    private double deliveryFee;

    @Column(nullable = false)
    private double discount;

    @Column(nullable = false)
    private double total;

    @Column(name = "placed_at", nullable = false)
    private Instant placedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Order() {}

    public Order(Long userId, OrderStatus status, String deliveryAddress,
                 double subtotal, double deliveryFee, double discount, double total) {
        this.userId = userId;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.discount = discount;
        this.total = total;
        this.placedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

    public Long getId()                  { return id; }
    public Long getUserId()              { return userId; }
    public OrderStatus getStatus()       { return status; }
    public String getDeliveryAddress()   { return deliveryAddress; }
    public List<OrderItem> getItems()    { return items; }
    public double getSubtotal()          { return subtotal; }
    public double getDeliveryFee()       { return deliveryFee; }
    public double getDiscount()          { return discount; }
    public double getTotal()             { return total; }
    public Instant getPlacedAt()         { return placedAt; }
    public Instant getUpdatedAt()        { return updatedAt; }
}
