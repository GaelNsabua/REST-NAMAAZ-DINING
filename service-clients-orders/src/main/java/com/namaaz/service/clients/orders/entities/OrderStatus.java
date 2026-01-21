package com.namaaz.service.clients.orders.entities;

/**
 * Statut d'une commande
 */
public enum OrderStatus {
    /**
     * Nouvelle commande créée
     */
    NEW,
    
    /**
     * Commande en cours de préparation
     */
    IN_PROGRESS,
    
    /**
     * Commande terminée et servie
     */
    COMPLETED,
    
    /**
     * Commande annulée
     */
    CANCELLED
}
