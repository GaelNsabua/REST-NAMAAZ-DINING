package com.namaaz.service.reservations.entities;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "restaurant_table")
public class RestaurantTable {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @NotNull(message = "Le numéro de table est obligatoire")
    @Column(name = "table_number", nullable = false, unique = true)
    private Integer tableNumber;
    
    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    @Column(nullable = false)
    private Integer seats;
    
    @Column(length = 100)
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TableStatus status = TableStatus.FREE;
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    // Constructeurs
    public RestaurantTable() {
    }
    
    public RestaurantTable(Integer tableNumber, Integer seats, String location) {
        this.tableNumber = tableNumber;
        this.seats = seats;
        this.location = location;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
    
    // Getters et Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Integer getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public Integer getSeats() {
        return seats;
    }
    
    public void setSeats(Integer seats) {
        this.seats = seats;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public TableStatus getStatus() {
        return status;
    }
    
    public void setStatus(TableStatus status) {
        this.status = status;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
