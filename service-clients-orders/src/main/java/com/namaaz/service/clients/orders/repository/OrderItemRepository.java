package com.namaaz.service.clients.orders.repository;

import com.namaaz.service.clients.orders.entities.OrderItem;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des articles de commande
 */
@Stateless
public class OrderItemRepository {
    
    @PersistenceContext(unitName = "ClientsOrdersPU")
    private EntityManager em;
    
    /**
     * Sauvegarder un article
     */
    public OrderItem save(OrderItem item) {
        if (item.getId() == null) {
            em.persist(item);
            return item;
        } else {
            return em.merge(item);
        }
    }
    
    /**
     * Trouver un article par ID
     */
    public Optional<OrderItem> findById(UUID id) {
        OrderItem item = em.find(OrderItem.class, id);
        return Optional.ofNullable(item);
    }
    
    /**
     * Trouver les articles d'une commande
     */
    public List<OrderItem> findByOrderId(UUID orderId) {
        TypedQuery<OrderItem> query = em.createQuery(
            "SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId",
            OrderItem.class
        );
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
    
    /**
     * Trouver tous les articles contenant un menu item spécifique
     */
    public List<OrderItem> findByMenuItemId(UUID menuItemId) {
        TypedQuery<OrderItem> query = em.createQuery(
            "SELECT oi FROM OrderItem oi WHERE oi.menuItemId = :menuItemId",
            OrderItem.class
        );
        query.setParameter("menuItemId", menuItemId);
        return query.getResultList();
    }
    
    /**
     * Supprimer un article
     */
    public boolean delete(UUID id) {
        Optional<OrderItem> item = findById(id);
        if (item.isPresent()) {
            em.remove(item.get());
            return true;
        }
        return false;
    }
}
