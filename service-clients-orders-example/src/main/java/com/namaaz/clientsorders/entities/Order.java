package com.namaaz.clientsorders.entities;

import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Order {
    @Id
    private UUID id;
    private UUID clientId;
    private UUID reservationId;
    private UUID tableId;
    private OrderStatus status;
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;

    // getters / setters
}
