package com.cryptic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    protected OrderItem() {}

    public OrderItem(Order order, String name, int quantity, double unitPrice) {
        this.order = order;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getId()         { return id; }
    public String getName()     { return name; }
    public int getQuantity()    { return quantity; }
    public double getUnitPrice(){ return unitPrice; }
}
