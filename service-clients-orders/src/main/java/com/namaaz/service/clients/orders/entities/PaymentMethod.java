package com.namaaz.service.clients.orders.entities;

/**
 * Méthode de paiement
 */
public enum PaymentMethod {
    /**
     * Paiement en espèces
     */
    CASH,
    
    /**
     * Paiement par carte bancaire
     */
    CARD,
    
    /**
     * Paiement en ligne
     */
    ONLINE
}
