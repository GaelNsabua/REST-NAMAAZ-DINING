package com.namaaz.service.reservations.entities;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "reservation")
public class Reservation {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @NotNull(message = "L'ID du client est obligatoire")
    @Column(name = "client_id", nullable = false)
    private UUID clientId;
    
    @NotNull(message = "Le nombre de personnes est obligatoire")
    @Min(value = 1, message = "Le nombre de personnes doit être au moins 1")
    @Column(name = "num_people", nullable = false)
    private Integer numPeople;
    
    @NotNull(message = "L'heure de début est obligatoire")
    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;
    
    @Column(name = "end_time")
    private OffsetDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "reservation_table",
        joinColumns = @JoinColumn(name = "reservation_id"),
        inverseJoinColumns = @JoinColumn(name = "table_id")
    )
    private Set<RestaurantTable> tables = new HashSet<>();
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    // Constructeurs
    public Reservation() {
    }
    
    public Reservation(UUID clientId, Integer numPeople, OffsetDateTime startTime) {
        this.clientId = clientId;
        this.numPeople = numPeople;
        this.startTime = startTime;
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
    
    // Helper methods
    public void addTable(RestaurantTable table) {
        tables.add(table);
    }
    
    public void removeTable(RestaurantTable table) {
        tables.remove(table);
    }
    
    // Getters et Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getClientId() {
        return clientId;
    }
    
    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }
    
    public Integer getNumPeople() {
        return numPeople;
    }
    
    public void setNumPeople(Integer numPeople) {
        this.numPeople = numPeople;
    }
    
    public OffsetDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }
    
    public OffsetDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Set<RestaurantTable> getTables() {
        return tables;
    }
    
    public void setTables(Set<RestaurantTable> tables) {
        this.tables = tables;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
