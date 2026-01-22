package com.namaaz.webapp.clients.orders.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class ReservationDTO implements Serializable {
    
    private String id;
    private String clientId;
    private OffsetDateTime reservationDateTime;
    private String status;

    public ReservationDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public OffsetDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(OffsetDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
