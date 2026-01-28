package com.namaaz.webapp.reservations.bean;

import com.namaaz.webapp.reservations.client.ClientServiceClient;
import com.namaaz.webapp.reservations.client.ReservationServiceClient;
import com.namaaz.webapp.reservations.dto.ClientDTO;
import com.namaaz.webapp.reservations.dto.ReservationDTO;
import com.namaaz.webapp.reservations.dto.RestaurantTableDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class ReservationBean implements Serializable {

    @Inject
    private ReservationServiceClient reservationServiceClient;

    @Inject
    private ClientServiceClient clientServiceClient;

    private List<ReservationDTO> reservations;
    private List<ClientDTO> clients;
    private List<RestaurantTableDTO> freeTables;
    private ReservationDTO selectedReservation;
    private ReservationDTO newReservation;
    private boolean showDialog;
    private String filterStatus;
    private String dateTimeInput;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @PostConstruct
    public void init() {
        loadClients();
        loadFreeTables();
        loadReservations();
        newReservation = new ReservationDTO();
        newReservation.setStatus("PENDING");
        newReservation.setReservationDateTime(OffsetDateTime.now().plusDays(1));
        newReservation.setTableIds(new ArrayList<>());
    }

    public void loadClients() {
        clients = clientServiceClient.getAllClients();
    }

    public void loadFreeTables() {
        freeTables = reservationServiceClient.getTablesByStatus("FREE");
    }

    public void loadReservations() {
        if (filterStatus != null && !filterStatus.isEmpty() && !"ALL".equals(filterStatus)) {
            reservations = reservationServiceClient.getReservationsByStatus(filterStatus);
        } else {
            reservations = reservationServiceClient.getAllReservations();
        }
        
        // Enrich with client names
        for (ReservationDTO res : reservations) {
            if (res.getClientId() != null) {
                ClientDTO client = clients.stream()
                        .filter(c -> c.getId().equals(res.getClientId()))
                        .findFirst()
                        .orElse(null);
                if (client != null) {
                    res.setClientName(client.getFullName());
                    res.setClientPhone(client.getPhone());
                }
            }
        }
    }

    public void applyFilter() {
        loadReservations();
    }

    public void clearFilter() {
        filterStatus = null;
        loadReservations();
    }

    public void prepareNewReservation() {
        newReservation = new ReservationDTO();
        newReservation.setStatus("PENDING");
        newReservation.setReservationDateTime(OffsetDateTime.now().plusDays(1));
        newReservation.setTableIds(new ArrayList<>());
        dateTimeInput = LocalDateTime.now().plusDays(1).format(FORMATTER);
        loadFreeTables();
        showDialog = true;
    }

    public void prepareEditReservation(ReservationDTO reservation) {
        selectedReservation = reservation;
        newReservation = new ReservationDTO();
        newReservation.setId(reservation.getId());
        newReservation.setClientId(reservation.getClientId());
        newReservation.setReservationDateTime(reservation.getReservationDateTime());
        newReservation.setNumberOfGuests(reservation.getNumberOfGuests());
        newReservation.setStatus(reservation.getStatus());
        newReservation.setSpecialRequests(reservation.getSpecialRequests());
        newReservation.setTableIds(reservation.getTableIds() != null ? 
            new ArrayList<>(reservation.getTableIds()) : new ArrayList<>());
        if (reservation.getReservationDateTime() != null) {
            dateTimeInput = reservation.getReservationDateTime().toLocalDateTime().format(FORMATTER);
        }
        loadFreeTables();
        showDialog = true;
    }

    public void saveReservation() {
        try {
            // Convertir dateTimeInput en OffsetDateTime
            if (dateTimeInput != null && !dateTimeInput.isEmpty()) {
                LocalDateTime localDateTime = LocalDateTime.parse(dateTimeInput, FORMATTER);
                newReservation.setReservationDateTime(localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime());
            }
            
            if (newReservation.getId() == null || newReservation.getId().isEmpty()) {
                ReservationDTO created = reservationServiceClient.createReservation(newReservation);
                if (created != null) {
                    addMessage("Succès", "Réservation créée avec succès", FacesMessage.SEVERITY_INFO);
                    loadReservations();
                    loadFreeTables();
                } else {
                    addMessage("Erreur", "Impossible de créer la réservation", FacesMessage.SEVERITY_ERROR);
                }
            } else {
                ReservationDTO updated = reservationServiceClient.updateReservation(newReservation.getId(), newReservation);
                if (updated != null) {
                    addMessage("Succès", "Réservation mise à jour avec succès", FacesMessage.SEVERITY_INFO);
                    loadReservations();
                    loadFreeTables();
                } else {
                    addMessage("Erreur", "Impossible de mettre à jour la réservation", FacesMessage.SEVERITY_ERROR);
                }
            }
            showDialog = false;
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void deleteReservation(String id) {
        try {
            boolean deleted = reservationServiceClient.deleteReservation(id);
            if (deleted) {
                addMessage("Succès", "Réservation supprimée avec succès", FacesMessage.SEVERITY_INFO);
                loadReservations();
                loadFreeTables();
            } else {
                addMessage("Erreur", "Impossible de supprimer la réservation", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void confirmReservation(String id) {
        try {
            boolean confirmed = reservationServiceClient.confirmReservation(id);
            if (confirmed) {
                addMessage("Succès", "Réservation confirmée", FacesMessage.SEVERITY_INFO);
                loadReservations();
            } else {
                addMessage("Erreur", "Impossible de confirmer la réservation", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void cancelReservation(String id) {
        try {
            boolean cancelled = reservationServiceClient.cancelReservation(id);
            if (cancelled) {
                addMessage("Succès", "Réservation annulée", FacesMessage.SEVERITY_INFO);
                loadReservations();
                loadFreeTables();
            } else {
                addMessage("Erreur", "Impossible d'annuler la réservation", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void cancelDialog() {
        showDialog = false;
        newReservation = new ReservationDTO();
        newReservation.setStatus("PENDING");
        newReservation.setReservationDateTime(OffsetDateTime.now().plusDays(1));
        newReservation.setTableIds(new ArrayList<>());
        dateTimeInput = null;
    }

    public List<SelectItem> getClientSelectItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("", "-- Sélectionner un client --"));
        for (ClientDTO client : clients) {
            items.add(new SelectItem(client.getId(), client.getFullName() + " (" + client.getPhone() + ")"));
        }
        return items;
    }

    public List<SelectItem> getTableSelectItems() {
        List<SelectItem> items = new ArrayList<>();
        for (RestaurantTableDTO table : freeTables) {
            items.add(new SelectItem(table.getId(), 
                "Table " + table.getTableNumber() + " (" + table.getCapacity() + " pers.)"));
        }
        return items;
    }

    public List<SelectItem> getStatusFilterItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("ALL", "Tous les statuts"));
        items.add(new SelectItem("PENDING", "En attente"));
        items.add(new SelectItem("CONFIRMED", "Confirmées"));
        items.add(new SelectItem("CANCELLED", "Annulées"));
        return items;
    }

    public String getStatusLabel(String status) {
        switch (status) {
            case "PENDING": return "En attente";
            case "CONFIRMED": return "Confirmée";
            case "CANCELLED": return "Annulée";
            default: return status;
        }
    }

    public String getStatusBadgeClass(String status) {
        switch (status) {
            case "PENDING": return "badge-warning";
            case "CONFIRMED": return "badge-success";
            case "CANCELLED": return "badge-danger";
            default: return "badge-info";
        }
    }

    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<ReservationDTO> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDTO> reservations) {
        this.reservations = reservations;
    }

    public List<ClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<ClientDTO> clients) {
        this.clients = clients;
    }

    public List<RestaurantTableDTO> getFreeTables() {
        return freeTables;
    }

    public void setFreeTables(List<RestaurantTableDTO> freeTables) {
        this.freeTables = freeTables;
    }

    public ReservationDTO getSelectedReservation() {
        return selectedReservation;
    }

    public void setSelectedReservation(ReservationDTO selectedReservation) {
        this.selectedReservation = selectedReservation;
    }

    public ReservationDTO getNewReservation() {
        return newReservation;
    }

    public void setNewReservation(ReservationDTO newReservation) {
        this.newReservation = newReservation;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public String getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }

    public String getDateTimeInput() {
        return dateTimeInput;
    }

    public void setDateTimeInput(String dateTimeInput) {
        this.dateTimeInput = dateTimeInput;
    }
}
