package com.namaaz.webapp.clients.orders.bean;

import com.namaaz.webapp.clients.orders.client.ClientOrderServiceClient;
import com.namaaz.webapp.clients.orders.dto.ClientDTO;
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
    private ClientOrderServiceClient clientOrderServiceClient;

    private List<ClientDTO> clients;
    private ClientDTO selectedClient;
    private ClientDTO newClient;
    private boolean showDialog;

    @PostConstruct
    public void init() {
        loadClients();
        newClient = new ClientDTO();
    }

    public void loadClients() {
        clients = clientOrderServiceClient.getAllClients();
    }

    public void prepareNewClient() {
        newClient = new ClientDTO();
        showDialog = true;
    }

    public void prepareEditClient(ClientDTO client) {
        selectedClient = client;
        newClient = new ClientDTO();
        newClient.setId(client.getId());
        newClient.setFirstName(client.getFirstName());
        newClient.setLastName(client.getLastName());
        newClient.setEmail(client.getEmail());
        newClient.setPhone(client.getPhone());
        newClient.setAddress(client.getAddress());
        showDialog = true;
    }

    public void saveClient() {
        try {
            if (newClient.getId() == null || newClient.getId().isEmpty()) {
                ClientDTO created = clientOrderServiceClient.createClient(newClient);
                if (created != null) {
                    addMessage("Succès", "Client créé avec succès", FacesMessage.SEVERITY_INFO);
                    loadClients();
                } else {
                    addMessage("Erreur", "Impossible de créer le client", FacesMessage.SEVERITY_ERROR);
                }
            } else {
                ClientDTO updated = clientOrderServiceClient.updateClient(newClient.getId(), newClient);
                if (updated != null) {
                    addMessage("Succès", "Client mis à jour avec succès", FacesMessage.SEVERITY_INFO);
                    loadClients();
                } else {
                    addMessage("Erreur", "Impossible de mettre à jour le client", FacesMessage.SEVERITY_ERROR);
                }
            }
            showDialog = false;
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void deleteClient(String id) {
        try {
            boolean deleted = clientOrderServiceClient.deleteClient(id);
            if (deleted) {
                addMessage("Succès", "Client supprimé avec succès", FacesMessage.SEVERITY_INFO);
                loadClients();
            } else {
                addMessage("Erreur", "Impossible de supprimer le client", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void cancelDialog() {
        showDialog = false;
        newClient = new ClientDTO();
    }

    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<ClientDTO> getClients() {
        return clients;
    }

    public ClientDTO getSelectedClient() {
        return selectedClient;
    }

    public ClientDTO getNewClient() {
        return newClient;
    }

    public void setNewClient(ClientDTO newClient) {
        this.newClient = newClient;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }
}
