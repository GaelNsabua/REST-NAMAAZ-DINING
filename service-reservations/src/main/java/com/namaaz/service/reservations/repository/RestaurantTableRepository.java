package com.namaaz.service.reservations.repository;

import com.namaaz.service.reservations.entities.RestaurantTable;
import com.namaaz.service.reservations.entities.TableStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RestaurantTableRepository {
    
    @PersistenceContext(unitName = "ReservationsPU")
    private EntityManager em;
    
    public RestaurantTable save(RestaurantTable table) {
        if (table.getId() == null) {
            em.persist(table);
            return table;
        } else {
            return em.merge(table);
        }
    }
    
    public Optional<RestaurantTable> findById(UUID id) {
        RestaurantTable table = em.find(RestaurantTable.class, id);
        return Optional.ofNullable(table);
    }
    
    public List<RestaurantTable> findAll() {
        TypedQuery<RestaurantTable> query = em.createQuery(
            "SELECT t FROM RestaurantTable t ORDER BY t.tableNumber", RestaurantTable.class);
        return query.getResultList();
    }
    
    public Optional<RestaurantTable> findByTableNumber(Integer tableNumber) {
        TypedQuery<RestaurantTable> query = em.createQuery(
            "SELECT t FROM RestaurantTable t WHERE t.tableNumber = :tableNumber", RestaurantTable.class);
        query.setParameter("tableNumber", tableNumber);
        return query.getResultStream().findFirst();
    }
    
    public List<RestaurantTable> findByStatus(TableStatus status) {
        TypedQuery<RestaurantTable> query = em.createQuery(
            "SELECT t FROM RestaurantTable t WHERE t.status = :status ORDER BY t.tableNumber", 
            RestaurantTable.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<RestaurantTable> findByMinSeats(Integer minSeats) {
        TypedQuery<RestaurantTable> query = em.createQuery(
            "SELECT t FROM RestaurantTable t WHERE t.seats >= :minSeats ORDER BY t.tableNumber", 
            RestaurantTable.class);
        query.setParameter("minSeats", minSeats);
        return query.getResultList();
    }
    
    public void delete(RestaurantTable table) {
        if (!em.contains(table)) {
            table = em.merge(table);
        }
        em.remove(table);
    }
}
