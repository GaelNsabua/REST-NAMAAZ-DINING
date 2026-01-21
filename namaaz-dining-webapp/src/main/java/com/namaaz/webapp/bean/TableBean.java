package com.namaaz.webapp.bean;

import com.namaaz.webapp.client.ReservationClient;
import com.namaaz.webapp.dto.RestaurantTableDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class TableBean implements Serializable {
    
    @Inject
    private ReservationClient reservationClient;
    
    private List<RestaurantTableDTO> tables;
    private RestaurantTableDTO selectedTable;
    private RestaurantTableDTO newTable;
    private String statusFilter;
    
    @PostConstruct
    public void init() {
        loadTables();
        newTable = new RestaurantTableDTO();
        newTable.setStatus("FREE");
    }
    
    public void loadTables() {
        try {
            if (statusFilter != null && !statusFilter.isEmpty()) {
                tables = reservationClient.getTablesByStatus(statusFilter);
            } else {
                tables = reservationClient.getAllTables();
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de charger les tables");
        }
    }
    
    public void createTable() {
        try {
            reservationClient.createTable(newTable);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Table créée avec succès");
            newTable = new RestaurantTableDTO();
            newTable.setStatus("FREE");
            loadTables();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de créer la table");
        }
    }
    
    public void updateTable() {
        try {
            reservationClient.updateTable(selectedTable.getId(), selectedTable);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Table modifiée avec succès");
            loadTables();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de modifier la table");
        }
    }
    
    public void deleteTable(String id) {
        try {
            reservationClient.deleteTable(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Table supprimée avec succès");
            loadTables();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer la table");
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
    
    public String getStatusBadgeClass(String status) {
        switch (status) {
            case "FREE": return "bg-green-500";
            case "RESERVED": return "bg-blue-500";
            case "OCCUPIED": return "bg-red-600";
            case "OUT_OF_SERVICE": return "bg-gray-500";
            default: return "bg-gray-400";
        }
    }
    
    // Getters and Setters
    public List<RestaurantTableDTO> getTables() { return tables; }
    public void setTables(List<RestaurantTableDTO> tables) { this.tables = tables; }
    
    public RestaurantTableDTO getSelectedTable() { return selectedTable; }
    public void setSelectedTable(RestaurantTableDTO selectedTable) { this.selectedTable = selectedTable; }
    
    public RestaurantTableDTO getNewTable() { return newTable; }
    public void setNewTable(RestaurantTableDTO newTable) { this.newTable = newTable; }
    
    public String getStatusFilter() { return statusFilter; }
    public void setStatusFilter(String statusFilter) { 
        this.statusFilter = statusFilter;
        loadTables();
    }
}
