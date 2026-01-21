package com.namaaz.clientsorders.entities;

import java.util.UUID;
import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderItem {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    private Order order;

    private UUID menuItemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    // getters / setters
}
