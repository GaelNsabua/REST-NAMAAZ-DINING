package com.namaaz.service.clients.orders.business;

import com.namaaz.service.clients.orders.entities.Client;
import com.namaaz.service.clients.orders.repository.ClientRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service métier pour la gestion des clients
 */
@Stateless
public class ClientService {
    
    @EJB
    private ClientRepository clientRepository;
    
    /**
     * Créer un nouveau client
     */
    @Transactional
    public Client createClient(Client client) {
        // Vérifier que l'email n'existe pas déjà
        Optional<Client> existing = clientRepository.findByEmail(client.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A client with this email already exists");
        }
        return clientRepository.save(client);
    }
    
    /**
     * Mettre à jour un client
     */
    @Transactional
    public Optional<Client> updateClient(UUID id, Client updatedClient) {
        Optional<Client> existing = clientRepository.findById(id);
        if (existing.isPresent()) {
            Client client = existing.get();
            
            // Vérifier l'unicité de l'email si modifié
            if (!client.getEmail().equals(updatedClient.getEmail())) {
                Optional<Client> emailExists = clientRepository.findByEmail(updatedClient.getEmail());
                if (emailExists.isPresent()) {
                    throw new IllegalArgumentException("A client with this email already exists");
                }
            }
            
            client.setFirstName(updatedClient.getFirstName());
            client.setLastName(updatedClient.getLastName());
            client.setEmail(updatedClient.getEmail());
            client.setPhone(updatedClient.getPhone());
            client.setAddress(updatedClient.getAddress());
            
            return Optional.of(clientRepository.save(client));
        }
        return Optional.empty();
    }
    
    /**
     * Récupérer un client par ID
     */
    public Optional<Client> getClientById(UUID id) {
        return clientRepository.findById(id);
    }
    
    /**
     * Récupérer un client par email
     */
    public Optional<Client> getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
    
    /**
     * Récupérer tous les clients
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    /**
     * Rechercher des clients par nom
     */
    public List<Client> searchClientsByName(String searchTerm) {
        return clientRepository.searchByName(searchTerm);
    }
    
    /**
     * Supprimer un client
     */
    @Transactional
    public boolean deleteClient(UUID id) {
        return clientRepository.delete(id);
    }
    
    /**
     * Compter le nombre de clients
     */
    public long countClients() {
        return clientRepository.count();
    }
}
