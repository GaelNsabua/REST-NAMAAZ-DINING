package com.namaaz.service.reservations.repository;

import com.namaaz.service.reservations.entities.Reservation;
import com.namaaz.service.reservations.entities.ReservationStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ReservationRepository {
    
    @PersistenceContext(unitName = "ReservationsPU")
    private EntityManager em;
    
    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            em.persist(reservation);
            return reservation;
        } else {
            return em.merge(reservation);
        }
    }
    
    public Optional<Reservation> findById(UUID id) {
        Reservation reservation = em.find(Reservation.class, id);
        return Optional.ofNullable(reservation);
    }
    
    public List<Reservation> findAll() {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r ORDER BY r.startTime DESC", Reservation.class);
        return query.getResultList();
    }
    
    public List<Reservation> findByClientId(UUID clientId) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.clientId = :clientId ORDER BY r.startTime DESC", 
            Reservation.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }
    
    public List<Reservation> findByStatus(ReservationStatus status) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.status = :status ORDER BY r.startTime", 
            Reservation.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Reservation> findByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.startTime BETWEEN :startDate AND :endDate ORDER BY r.startTime", 
            Reservation.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Reservation> findUpcoming() {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.startTime > :now AND r.status IN ('PENDING', 'CONFIRMED') ORDER BY r.startTime", 
            Reservation.class);
        query.setParameter("now", OffsetDateTime.now());
        return query.getResultList();
    }
    
    public void delete(Reservation reservation) {
        if (!em.contains(reservation)) {
            reservation = em.merge(reservation);
        }
        em.remove(reservation);
    }
}
