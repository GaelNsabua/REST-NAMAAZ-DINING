package com.namaaz.service.clients.orders.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entité Order (Commande)
 */
@Entity
@Table(name = "orders")
public class Order implements Serializable {
    
    @Id
    @GeneratedValue
    @Column(length = 36)
    private String id;
    
    @NotNull(message = "Client ID is required")
    @Column(name = "client_id", nullable = false, length = 36)
    private String clientId;
    
    @Column(name = "reservation_id", length = 36)
    private String reservationId;
    
    @Column(name = "table_id", length = 36)
    private String tableId;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderStatus status = OrderStatus.NEW;
    
    @NotNull
    @DecimalMin(value = "0.0", message = "Total amount must be positive")
    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
    
    // Helper methods
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
    
    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Constructors
    public Order() {}
    
    public Order(String clientId) {
        this.clientId = clientId;
        this.status = OrderStatus.NEW;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    
    public String getTableId() {
        return tableId;
    }
    
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
