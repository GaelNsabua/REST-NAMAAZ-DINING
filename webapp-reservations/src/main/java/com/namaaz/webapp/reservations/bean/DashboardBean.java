package com.namaaz.webapp.reservations.bean;

import com.namaaz.webapp.reservations.client.ReservationServiceClient;
import com.namaaz.webapp.reservations.dto.RestaurantTableDTO;
import com.namaaz.webapp.reservations.dto.ReservationDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Inject
    private ReservationServiceClient reservationServiceClient;

    private long totalTables;
    private long freeTables;
    private long totalReservations;
    private long pendingReservations;

    @PostConstruct
    public void init() {
        loadStatistics();
    }

    public void loadStatistics() {
        List<RestaurantTableDTO> allTables = reservationServiceClient.getAllTables();
        totalTables = allTables.size();

        freeTables = allTables.stream()
                .filter(table -> "FREE".equals(table.getStatus()))
                .count();

        List<ReservationDTO> allReservations = reservationServiceClient.getAllReservations();
        totalReservations = allReservations.size();

        pendingReservations = allReservations.stream()
                .filter(res -> "PENDING".equals(res.getStatus()))
                .count();
    }

    // Getters
    public long getTotalTables() {
        return totalTables;
    }

    public long getFreeTables() {
        return freeTables;
    }

    public long getTotalReservations() {
        return totalReservations;
    }

    public long getPendingReservations() {
        return pendingReservations;
    }
}
