package com.namaaz.service.menu.repository;

import com.namaaz.service.menu.entities.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CategoryRepository {
    
    @PersistenceContext(unitName = "MenuPU")
    private EntityManager em;
    
    public Category save(Category category) {
        if (category.getId() == null) {
            em.persist(category);
            return category;
        } else {
            return em.merge(category);
        }
    }
    
    public Optional<Category> findById(UUID id) {
        Category category = em.find(Category.class, id);
        return Optional.ofNullable(category);
    }
    
    public List<Category> findAll() {
        TypedQuery<Category> query = em.createQuery(
            "SELECT c FROM Category c ORDER BY c.name", Category.class);
        return query.getResultList();
    }
    
    public Optional<Category> findByName(String name) {
        TypedQuery<Category> query = em.createQuery(
            "SELECT c FROM Category c WHERE c.name = :name", Category.class);
        query.setParameter("name", name);
        return query.getResultStream().findFirst();
    }
    
    public void delete(Category category) {
        if (!em.contains(category)) {
            category = em.merge(category);
        }
        em.remove(category);
    }
}