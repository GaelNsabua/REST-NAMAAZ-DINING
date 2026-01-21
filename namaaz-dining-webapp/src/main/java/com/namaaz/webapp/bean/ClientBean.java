package com.namaaz.webapp.bean;

import com.namaaz.webapp.client.OrderClient;
import com.namaaz.webapp.dto.ClientDTO;
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
public class ClientBean implements Serializable {
    
    @Inject
    private OrderClient orderClient;
    
    private List<ClientDTO> clients;
    private ClientDTO selectedClient;
    private ClientDTO newClient;
    
    @PostConstruct
    public void init() {
        loadClients();
        newClient = new ClientDTO();
    }
    
    public void loadClients() {
        try {
            clients = orderClient.getAllClients();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de charger les clients");
        }
    }
    
    public void createClient() {
        try {
            orderClient.createClient(newClient);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Client créé avec succès");
            newClient = new ClientDTO();
            loadClients();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de créer le client");
        }
    }
    
    public void prepareEdit(ClientDTO client) {
        this.selectedClient = new ClientDTO();
        this.selectedClient.setId(client.getId());
        this.selectedClient.setFirstName(client.getFirstName());
        this.selectedClient.setLastName(client.getLastName());
        this.selectedClient.setEmail(client.getEmail());
        this.selectedClient.setPhone(client.getPhone());
        this.selectedClient.setAddress(client.getAddress());
    }
    
    public void updateClient() {
        try {
            orderClient.updateClient(selectedClient.getId(), selectedClient);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Client modifié avec succès");
            selectedClient = null;
            loadClients();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de modifier le client");
        }
    }
    
    public void cancelEdit() {
        selectedClient = null;
    }
    
    public void deleteClient(String id) {
        try {
            orderClient.deleteClient(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Client supprimé avec succès");
            loadClients();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer le client");
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
    public List<ClientDTO> getClients() { return clients; }
    public void setClients(List<ClientDTO> clients) { this.clients = clients; }
    
    public ClientDTO getSelectedClient() { return selectedClient; }
    public void setSelectedClient(ClientDTO selectedClient) { this.selectedClient = selectedClient; }
    
    public ClientDTO getNewClient() { return newClient; }
    public void setNewClient(ClientDTO newClient) { this.newClient = newClient; }
}
