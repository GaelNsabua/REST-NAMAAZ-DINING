package com.namaaz.service.clients.orders.repository;

import com.namaaz.service.clients.orders.entities.Payment;
import com.namaaz.service.clients.orders.entities.PaymentStatus;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des paiements
 */
@Stateless
public class PaymentRepository {
    
    @PersistenceContext(unitName = "ClientsOrdersPU")
    private EntityManager em;
    
    /**
     * Sauvegarder un paiement
     */
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            em.persist(payment);
            return payment;
        } else {
            return em.merge(payment);
        }
    }
    
    /**
     * Trouver un paiement par ID
     */
    public Optional<Payment> findById(UUID id) {
        Payment payment = em.find(Payment.class, id);
        return Optional.ofNullable(payment);
    }
    
    /**
     * Trouver tous les paiements
     */
    public List<Payment> findAll() {
        TypedQuery<Payment> query = em.createQuery("SELECT p FROM Payment p ORDER BY p.createdAt DESC", Payment.class);
        return query.getResultList();
    }
    
    /**
     * Trouver les paiements d'une commande
     */
    public List<Payment> findByOrderId(UUID orderId) {
        TypedQuery<Payment> query = em.createQuery(
            "SELECT p FROM Payment p WHERE p.orderId = :orderId ORDER BY p.createdAt DESC",
            Payment.class
        );
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
    
    /**
     * Trouver les paiements par statut
     */
    public List<Payment> findByStatus(PaymentStatus status) {
        TypedQuery<Payment> query = em.createQuery(
            "SELECT p FROM Payment p WHERE p.status = :status ORDER BY p.createdAt DESC",
            Payment.class
        );
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    /**
     * Trouver un paiement par référence de transaction
     */
    public Optional<Payment> findByTransactionRef(String transactionRef) {
        TypedQuery<Payment> query = em.createQuery(
            "SELECT p FROM Payment p WHERE p.transactionRef = :ref",
            Payment.class
        );
        query.setParameter("ref", transactionRef);
        List<Payment> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    /**
     * Supprimer un paiement
     */
    public boolean delete(UUID id) {
        Optional<Payment> payment = findById(id);
        if (payment.isPresent()) {
            em.remove(payment.get());
            return true;
        }
        return false;
    }
    
    /**
     * Compter le nombre total de paiements
     */
    public long count() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(p) FROM Payment p", Long.class);
        return query.getSingleResult();
    }
}
