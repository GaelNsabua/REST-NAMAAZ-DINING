package com.namaaz.webapp.reservations.bean;

import com.namaaz.webapp.reservations.client.ReservationServiceClient;
import com.namaaz.webapp.reservations.dto.RestaurantTableDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class TableBean implements Serializable {

    @Inject
    private ReservationServiceClient reservationServiceClient;

    private List<RestaurantTableDTO> tables;
    private RestaurantTableDTO selectedTable;
    private RestaurantTableDTO newTable;
    private boolean showDialog;
    private String filterStatus = "ALL";

    @PostConstruct
    public void init() {
        loadTables();
        newTable = new RestaurantTableDTO();
        newTable.setStatus("FREE");
    }

    public void loadTables() {
        if (filterStatus != null && !filterStatus.isEmpty() && !"ALL".equals(filterStatus)) {
            tables = reservationServiceClient.getTablesByStatus(filterStatus);
        } else {
            tables = reservationServiceClient.getAllTables();
        }
    }

    public void applyFilter() {
        loadTables();
    }

    public void clearFilter() {
        filterStatus = "ALL";
        loadTables();
    }

    public void prepareNewTable() {
        newTable = new RestaurantTableDTO();
        newTable.setStatus("FREE");
        showDialog = true;
    }

    public void prepareEditTable(RestaurantTableDTO table) {
        selectedTable = table;
        newTable = new RestaurantTableDTO();
        newTable.setId(table.getId());
        newTable.setTableNumber(table.getTableNumber());
        newTable.setCapacity(table.getCapacity());
        newTable.setStatus(table.getStatus());
        showDialog = true;
    }

    public void saveTable() {
        try {
            if (newTable.getId() == null || newTable.getId().isEmpty()) {
                RestaurantTableDTO created = reservationServiceClient.createTable(newTable);
                if (created != null) {
                    addMessage("Succès", "Table créée avec succès", FacesMessage.SEVERITY_INFO);
                    loadTables();
                } else {
                    addMessage("Erreur", "Impossible de créer la table", FacesMessage.SEVERITY_ERROR);
                }
            } else {
                RestaurantTableDTO updated = reservationServiceClient.updateTable(newTable.getId(), newTable);
                if (updated != null) {
                    addMessage("Succès", "Table mise à jour avec succès", FacesMessage.SEVERITY_INFO);
                    loadTables();
                } else {
                    addMessage("Erreur", "Impossible de mettre à jour la table", FacesMessage.SEVERITY_ERROR);
                }
            }
            showDialog = false;
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void deleteTable(String id) {
        try {
            boolean deleted = reservationServiceClient.deleteTable(id);
            if (deleted) {
                addMessage("Succès", "Table supprimée avec succès", FacesMessage.SEVERITY_INFO);
                loadTables();
            } else {
                addMessage("Erreur", "Impossible de supprimer la table", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void updateStatus(String id, String newStatus) {
        try {
            boolean updated = reservationServiceClient.updateTableStatus(id, newStatus);
            if (updated) {
                addMessage("Succès", "Statut mis à jour", FacesMessage.SEVERITY_INFO);
                loadTables();
            } else {
                addMessage("Erreur", "Impossible de mettre à jour le statut", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void cancelDialog() {
        showDialog = false;
        newTable = new RestaurantTableDTO();
        newTable.setStatus("FREE");
    }

    public List<SelectItem> getStatusSelectItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("FREE", "Libre"));
        items.add(new SelectItem("RESERVED", "Réservée"));
        items.add(new SelectItem("OCCUPIED", "Occupée"));
        items.add(new SelectItem("OUT_OF_SERVICE", "Hors service"));
        return items;
    }

    public List<SelectItem> getStatusFilterItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("ALL", "Tous les statuts"));
        items.add(new SelectItem("FREE", "Libres"));
        items.add(new SelectItem("RESERVED", "Réservées"));
        items.add(new SelectItem("OCCUPIED", "Occupées"));
        items.add(new SelectItem("OUT_OF_SERVICE", "Hors service"));
        return items;
    }

    public String getStatusLabel(String status) {
        switch (status) {
            case "FREE": return "Libre";
            case "RESERVED": return "Réservée";
            case "OCCUPIED": return "Occupée";
            case "OUT_OF_SERVICE": return "Hors service";
            default: return status;
        }
    }

    public String getStatusBadgeClass(String status) {
        switch (status) {
            case "FREE": return "badge-success";
            case "RESERVED": return "badge-warning";
            case "OCCUPIED": return "badge-danger";
            case "OUT_OF_SERVICE": return "badge-secondary";
            default: return "badge-info";
        }
    }

    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<RestaurantTableDTO> getTables() {
        return tables;
    }

    public void setTables(List<RestaurantTableDTO> tables) {
        this.tables = tables;
    }

    public RestaurantTableDTO getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(RestaurantTableDTO selectedTable) {
        this.selectedTable = selectedTable;
    }

    public RestaurantTableDTO getNewTable() {
        return newTable;
    }

    public void setNewTable(RestaurantTableDTO newTable) {
        this.newTable = newTable;
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
}
