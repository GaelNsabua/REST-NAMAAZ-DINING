/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.namaaz.jakarta.servicemenu.business;

import com.namaaz.jakarta.servicemenu.entities.Category;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author WAITING
 */
@Stateless
@LocalBean
public class CategoryBusiness {
    @PersistenceContext(unitName = "namaazPU")
    private EntityManager em;
    
    public void creer(Category category){
        em.persist(category);
    }
    
    public List<Category> lister(){
     return em.createQuery("SELECT c FROM Category c ORDER BY c.id DESC",Category.class).getResultList();
    }
    
    public Category trouverParId(int id){
        return em.find(Category.class, id);
    }
}
