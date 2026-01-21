package com.namaaz.service.clients.orders.repository;

import com.namaaz.service.clients.orders.entities.Client;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des clients
 */
@Stateless
public class ClientRepository {
    
    @PersistenceContext(unitName = "ClientsOrdersPU")
    private EntityManager em;
    
    /**
     * Sauvegarder un client
     */
    public Client save(Client client) {
        if (client.getId() == null) {
            em.persist(client);
            return client;
        } else {
            return em.merge(client);
        }
    }
    
    /**
     * Trouver un client par ID
     */
    public Optional<Client> findById(UUID id) {
        Client client = em.find(Client.class, id);
        return Optional.ofNullable(client);
    }
    
    /**
     * Trouver tous les clients
     */
    public List<Client> findAll() {
        TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c ORDER BY c.lastName, c.firstName", Client.class);
        return query.getResultList();
    }
    
    /**
     * Trouver un client par email
     */
    public Optional<Client> findByEmail(String email) {
        TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c WHERE c.email = :email", Client.class);
        query.setParameter("email", email);
        List<Client> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    /**
     * Rechercher des clients par nom
     */
    public List<Client> searchByName(String searchTerm) {
        TypedQuery<Client> query = em.createQuery(
            "SELECT c FROM Client c WHERE LOWER(c.firstName) LIKE LOWER(:term) OR LOWER(c.lastName) LIKE LOWER(:term)",
            Client.class
        );
        query.setParameter("term", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    /**
     * Supprimer un client
     */
    public boolean delete(UUID id) {
        Optional<Client> client = findById(id);
        if (client.isPresent()) {
            em.remove(client.get());
            return true;
        }
        return false;
    }
    
    /**
     * Compter le nombre total de clients
     */
    public long count() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Client c", Long.class);
        return query.getSingleResult();
    }
}
