package com.namaaz.menu.entities;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

@Entity
public class MenuItem {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @ManyToOne(optional = false)
    private Category category;

    private Boolean available = true;
    private Integer prepTime;

    // getters / setters
}
