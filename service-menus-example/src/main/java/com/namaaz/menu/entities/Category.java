package com.namaaz.menu.entities;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Category {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // getters / setters
}
