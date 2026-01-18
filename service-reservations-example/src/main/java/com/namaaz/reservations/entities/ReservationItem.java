package com.namaaz.reservations.entities;

import java.util.UUID;
import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ReservationItem {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    private Reservation reservation;

    private UUID menuItemId;
    private Integer quantity;
    private BigDecimal priceSnapshot;

    // getters / setters
}
