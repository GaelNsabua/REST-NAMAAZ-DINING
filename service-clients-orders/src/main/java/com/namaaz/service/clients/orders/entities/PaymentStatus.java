package com.namaaz.service.clients.orders.entities;

/**
 * Statut d'un paiement
 */
public enum PaymentStatus {
    /**
     * Paiement en attente
     */
    PENDING,
    
    /**
     * Paiement réussi
     */
    OK,
    
    /**
     * Paiement échoué
     */
    FAILED
}
