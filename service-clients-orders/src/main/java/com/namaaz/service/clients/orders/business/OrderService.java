package com.namaaz.service.clients.orders.business;

import com.namaaz.service.clients.orders.entities.Order;
import com.namaaz.service.clients.orders.entities.OrderItem;
import com.namaaz.service.clients.orders.entities.OrderStatus;
import com.namaaz.service.clients.orders.repository.ClientRepository;
import com.namaaz.service.clients.orders.repository.OrderRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service métier pour la gestion des commandes
 */
@Stateless
public class OrderService {
    
    @EJB
    private OrderRepository orderRepository;
    
    @EJB
    private ClientRepository clientRepository;
    
    /**
     * Créer une nouvelle commande avec des articles
     */
    @Transactional
    public Order createOrder(Order order, List<OrderItem> items) {
        // Vérifier que le client existe
        if (!clientRepository.findById(order.getClientId()).isPresent()) {
            throw new IllegalArgumentException("Client not found with ID: " + order.getClientId());
        }
        
        // Ajouter les articles à la commande
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                order.addItem(item);
            }
        }
        
        // Calculer le montant total
        order.calculateTotalAmount();
        
        // Initialiser le statut
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.NEW);
        }
        
        return orderRepository.save(order);
    }
    
    /**
     * Mettre à jour une commande
     */
    @Transactional
    public Optional<Order> updateOrder(UUID id, Order updatedOrder) {
        Optional<Order> existing = orderRepository.findById(id);
        if (existing.isPresent()) {
            Order order = existing.get();
            order.setStatus(updatedOrder.getStatus());
            order.setReservationId(updatedOrder.getReservationId());
            order.setTableId(updatedOrder.getTableId());
            
            return Optional.of(orderRepository.save(order));
        }
        return Optional.empty();
    }
    
    /**
     * Mettre à jour le statut d'une commande
     */
    @Transactional
    public Optional<Order> updateOrderStatus(UUID id, OrderStatus newStatus) {
        Optional<Order> existing = orderRepository.findById(id);
        if (existing.isPresent()) {
            Order order = existing.get();
            order.setStatus(newStatus);
            return Optional.of(orderRepository.save(order));
        }
        return Optional.empty();
    }
    
    /**
     * Ajouter un article à une commande existante
     */
    @Transactional
    public Optional<Order> addItemToOrder(UUID orderId, OrderItem item) {
        Optional<Order> existing = orderRepository.findById(orderId);
        if (existing.isPresent()) {
            Order order = existing.get();
            
            // Ne peut pas modifier une commande terminée ou annulée
            if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
                throw new IllegalStateException("Cannot modify a " + order.getStatus() + " order");
            }
            
            order.addItem(item);
            order.calculateTotalAmount();
            
            return Optional.of(orderRepository.save(order));
        }
        return Optional.empty();
    }
    
    /**
     * Marquer une commande comme en cours
     */
    @Transactional
    public Optional<Order> markOrderInProgress(UUID id) {
        return updateOrderStatus(id, OrderStatus.IN_PROGRESS);
    }
    
    /**
     * Marquer une commande comme terminée
     */
    @Transactional
    public Optional<Order> completeOrder(UUID id) {
        return updateOrderStatus(id, OrderStatus.COMPLETED);
    }
    
    /**
     * Annuler une commande
     */
    @Transactional
    public Optional<Order> cancelOrder(UUID id) {
        Optional<Order> existing = orderRepository.findById(id);
        if (existing.isPresent()) {
            Order order = existing.get();
            
            // Ne peut pas annuler une commande déjà terminée
            if (order.getStatus() == OrderStatus.COMPLETED) {
                throw new IllegalStateException("Cannot cancel a completed order");
            }
            
            order.setStatus(OrderStatus.CANCELLED);
            return Optional.of(orderRepository.save(order));
        }
        return Optional.empty();
    }
    
    /**
     * Récupérer une commande par ID
     */
    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }
    
    /**
     * Récupérer toutes les commandes
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    /**
     * Récupérer les commandes d'un client
     */
    public List<Order> getOrdersByClientId(UUID clientId) {
        return orderRepository.findByClientId(clientId);
    }
    
    /**
     * Récupérer les commandes par statut
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    /**
     * Récupérer les commandes liées à une réservation
     */
    public List<Order> getOrdersByReservationId(UUID reservationId) {
        return orderRepository.findByReservationId(reservationId);
    }
    
    /**
     * Supprimer une commande
     */
    @Transactional
    public boolean deleteOrder(UUID id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getStatus() != OrderStatus.COMPLETED) {
            return orderRepository.delete(id);
        }
        return false;
    }
    
    /**
     * Calculer le total d'une commande
     */
    public BigDecimal calculateOrderTotal(Order order) {
        return order.getItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Compter le nombre de commandes
     */
    public long countOrders() {
        return orderRepository.count();
    }
}
