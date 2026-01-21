package com.namaaz.clientsorders.entities;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Client {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;

    // getters / setters
}
