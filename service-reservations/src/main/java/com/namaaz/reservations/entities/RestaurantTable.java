package com.namaaz.reservations.entities;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RestaurantTable {
    @Id
    private UUID id;
    private Integer tableNumber;
    private Integer seats;
    private String location;
    private TableStatus status;

    // getters / setters
}
