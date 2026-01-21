package com.namaaz.service.clients.orders.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entité Payment (Paiement)
 */
@Entity
@Table(name = "payment")
public class Payment implements Serializable {
    
    @Id
    @GeneratedValue
    @Column(length = 36)
    private String id;
    
    @NotNull(message = "Order ID is required")
    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentMethod method;
    
    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Size(max = 200)
    @Column(name = "transaction_ref", length = 200)
    private String transactionRef;
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
    
    // Constructors
    public Payment() {}
    
    public Payment(String orderId, BigDecimal amount, PaymentMethod method) {
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public PaymentMethod getMethod() {
        return method;
    }
    
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public String getTransactionRef() {
        return transactionRef;
    }
    
    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
