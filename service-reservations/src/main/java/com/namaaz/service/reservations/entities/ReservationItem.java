package com.namaaz.service.reservations.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "reservation_item")
public class ReservationItem {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    
    @NotNull(message = "L'ID du plat est obligatoire")
    @Column(name = "menu_item_id", nullable = false)
    private UUID menuItemId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Column(nullable = false)
    private Integer quantity = 1;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Column(name = "price_snapshot", precision = 10, scale = 2)
    private BigDecimal priceSnapshot;
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    // Constructeurs
    public ReservationItem() {
    }
    
    public ReservationItem(UUID menuItemId, Integer quantity, BigDecimal priceSnapshot) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.priceSnapshot = priceSnapshot;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
    
    // Getters et Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public UUID getMenuItemId() {
        return menuItemId;
    }
    
    public void setMenuItemId(UUID menuItemId) {
        this.menuItemId = menuItemId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPriceSnapshot() {
        return priceSnapshot;
    }
    
    public void setPriceSnapshot(BigDecimal priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
