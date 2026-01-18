package com.namaaz.service.menu.repository;

import com.namaaz.service.menu.entities.MenuItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MenuItemRepository {
    
    @PersistenceContext(unitName = "MenuPU")
    private EntityManager em;
    
    public MenuItem save(MenuItem menuItem) {
        if (menuItem.getId() == null) {
            em.persist(menuItem);
            return menuItem;
        } else {
            return em.merge(menuItem);
        }
    }
    
    public Optional<MenuItem> findById(UUID id) {
        MenuItem menuItem = em.find(MenuItem.class, id);
        return Optional.ofNullable(menuItem);
    }
    
    public List<MenuItem> findAll() {
        TypedQuery<MenuItem> query = em.createQuery(
            "SELECT m FROM MenuItem m ORDER BY m.name", MenuItem.class);
        return query.getResultList();
    }
    
    public List<MenuItem> findByCategory(UUID categoryId) {
        TypedQuery<MenuItem> query = em.createQuery(
            "SELECT m FROM MenuItem m WHERE m.category.id = :categoryId ORDER BY m.name", 
            MenuItem.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }
    
    public List<MenuItem> findByAvailability(Boolean available) {
        TypedQuery<MenuItem> query = em.createQuery(
            "SELECT m FROM MenuItem m WHERE m.available = :available ORDER BY m.name", 
            MenuItem.class);
        query.setParameter("available", available);
        return query.getResultList();
    }
    
    public void delete(MenuItem menuItem) {
        if (!em.contains(menuItem)) {
            menuItem = em.merge(menuItem);
        }
        em.remove(menuItem);
    }
}