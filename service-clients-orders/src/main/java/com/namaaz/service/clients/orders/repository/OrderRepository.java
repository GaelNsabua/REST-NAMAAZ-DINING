package com.namaaz.service.clients.orders.repository;

import com.namaaz.service.clients.orders.entities.Order;
import com.namaaz.service.clients.orders.entities.OrderStatus;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des commandes
 */
@Stateless
public class OrderRepository {
    
    @PersistenceContext(unitName = "ClientsOrdersPU")
    private EntityManager em;
    
    /**
     * Sauvegarder une commande
     */
    public Order save(Order order) {
        if (order.getId() == null) {
            em.persist(order);
            return order;
        } else {
            return em.merge(order);
        }
    }
    
    /**
     * Trouver une commande par ID avec ses items
     */
    public Optional<Order> findById(UUID id) {
        TypedQuery<Order> query = em.createQuery(
            "SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id",
            Order.class
        );
        query.setParameter("id", id);
        List<Order> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    /**
     * Trouver toutes les commandes
     */
    public List<Order> findAll() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o ORDER BY o.createdAt DESC", Order.class);
        return query.getResultList();
    }
    
    /**
     * Trouver les commandes d'un client
     */
    public List<Order> findByClientId(UUID clientId) {
        TypedQuery<Order> query = em.createQuery(
            "SELECT o FROM Order o WHERE o.clientId = :clientId ORDER BY o.createdAt DESC",
            Order.class
        );
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }
    
    /**
     * Trouver les commandes par statut
     */
    public List<Order> findByStatus(OrderStatus status) {
        TypedQuery<Order> query = em.createQuery(
            "SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC",
            Order.class
        );
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    /**
     * Trouver les commandes liées à une réservation
     */
    public List<Order> findByReservationId(UUID reservationId) {
        TypedQuery<Order> query = em.createQuery(
            "SELECT o FROM Order o WHERE o.reservationId = :reservationId ORDER BY o.createdAt DESC",
            Order.class
        );
        query.setParameter("reservationId", reservationId);
        return query.getResultList();
    }
    
    /**
     * Supprimer une commande
     */
    public boolean delete(UUID id) {
        Optional<Order> order = findById(id);
        if (order.isPresent()) {
            em.remove(order.get());
            return true;
        }
        return false;
    }
    
    /**
     * Compter le nombre total de commandes
     */
    public long count() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(o) FROM Order o", Long.class);
        return query.getSingleResult();
    }
}
