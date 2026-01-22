package com.namaaz.webapp.clients.orders.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentDTO implements Serializable {
    
    private String id;
    private String orderId;
    private BigDecimal amount;
    private String paymentMethod; // CASH, CARD, ONLINE
    private String status; // PENDING, OK, FAILED
    private OffsetDateTime paymentDateTime;
    private String transactionId;

    public PaymentDTO() {
    }

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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(OffsetDateTime paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
