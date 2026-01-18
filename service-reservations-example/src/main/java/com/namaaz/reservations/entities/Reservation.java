package com.namaaz.reservations.entities;

import java.util.UUID;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Reservation {
    @Id
    private UUID id;
    private UUID clientId;
    private Integer numPeople;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private ReservationStatus status;
    private String notes;

    @ManyToMany
    private Set<RestaurantTable> tables;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationItem> items;

    // getters / setters
}
