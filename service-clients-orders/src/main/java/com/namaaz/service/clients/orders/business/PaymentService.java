package com.namaaz.service.clients.orders.business;

import com.namaaz.service.clients.orders.entities.Payment;
import com.namaaz.service.clients.orders.entities.PaymentStatus;
import com.namaaz.service.clients.orders.repository.OrderRepository;
import com.namaaz.service.clients.orders.repository.PaymentRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service métier pour la gestion des paiements
 */
@Stateless
public class PaymentService {
    
    @EJB
    private PaymentRepository paymentRepository;
    
    @EJB
    private OrderRepository orderRepository;
    
    /**
     * Créer un nouveau paiement
     */
    @Transactional
    public Payment createPayment(Payment payment) {
        // Vérifier que la commande existe
        if (!orderRepository.findById(payment.getOrderId()).isPresent()) {
            throw new IllegalArgumentException("Order not found with ID: " + payment.getOrderId());
        }
        
        // Initialiser le statut si non défini
        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Mettre à jour un paiement
     */
    @Transactional
    public Optional<Payment> updatePayment(UUID id, Payment updatedPayment) {
        Optional<Payment> existing = paymentRepository.findById(id);
        if (existing.isPresent()) {
            Payment payment = existing.get();
            payment.setStatus(updatedPayment.getStatus());
            payment.setTransactionRef(updatedPayment.getTransactionRef());
            
            return Optional.of(paymentRepository.save(payment));
        }
        return Optional.empty();
    }
    
    /**
     * Mettre à jour le statut d'un paiement
     */
    @Transactional
    public Optional<Payment> updatePaymentStatus(UUID id, PaymentStatus newStatus) {
        Optional<Payment> existing = paymentRepository.findById(id);
        if (existing.isPresent()) {
            Payment payment = existing.get();
            payment.setStatus(newStatus);
            return Optional.of(paymentRepository.save(payment));
        }
        return Optional.empty();
    }
    
    /**
     * Confirmer un paiement (marquer comme OK)
     */
    @Transactional
    public Optional<Payment> confirmPayment(UUID id, String transactionRef) {
        Optional<Payment> existing = paymentRepository.findById(id);
        if (existing.isPresent()) {
            Payment payment = existing.get();
            payment.setStatus(PaymentStatus.OK);
            if (transactionRef != null && !transactionRef.isEmpty()) {
                payment.setTransactionRef(transactionRef);
            }
            return Optional.of(paymentRepository.save(payment));
        }
        return Optional.empty();
    }
    
    /**
     * Marquer un paiement comme échoué
     */
    @Transactional
    public Optional<Payment> failPayment(UUID id) {
        return updatePaymentStatus(id, PaymentStatus.FAILED);
    }
    
    /**
     * Récupérer un paiement par ID
     */
    public Optional<Payment> getPaymentById(UUID id) {
        return paymentRepository.findById(id);
    }
    
    /**
     * Récupérer tous les paiements
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    /**
     * Récupérer les paiements d'une commande
     */
    public List<Payment> getPaymentsByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    /**
     * Récupérer les paiements par statut
     */
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }
    
    /**
     * Récupérer un paiement par référence de transaction
     */
    public Optional<Payment> getPaymentByTransactionRef(String transactionRef) {
        return paymentRepository.findByTransactionRef(transactionRef);
    }
    
    /**
     * Supprimer un paiement
     */
    @Transactional
    public boolean deletePayment(UUID id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        // Ne peut supprimer que les paiements en attente ou échoués
        if (payment.isPresent() && payment.get().getStatus() != PaymentStatus.OK) {
            return paymentRepository.delete(id);
        }
        return false;
    }
    
    /**
     * Compter le nombre de paiements
     */
    public long countPayments() {
        return paymentRepository.count();
    }
}
