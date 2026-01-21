package com.namaaz.clientsorders.entities;

import java.util.UUID;
import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Payment {
    @Id
    private UUID id;

    @ManyToOne
    private Order order;

    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionRef;

    // getters / setters
}
