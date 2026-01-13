package com.namaaz.jakarta.servicemenu.bean;

import com.namaaz.jakarta.servicemenu.entities.Category;
import com.namaaz.jakarta.servicemenu.business.CategoryBusiness;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Bean géré pour la manipulation des catégories au niveau de la vue ou du contrôleur.
 * * @author WAITING
 */
@Named(value = "categoryBean")
@RequestScoped
public class CategoryBean implements Serializable {

    @Inject
    private CategoryBusiness categoryBusiness;

    private Category category = new Category();
    private List<Category> categories;

    public CategoryBean() {
    }

    /**
     * Récupère la liste de toutes les catégories via la couche Business
     * @return liste des catégories
     */
    public List<Category> getCategories() {
        if (categories == null) {
            categories = categoryBusiness.lister();
        }
        return categories;
    }

    /**
     * Méthode pour enregistrer une nouvelle catégorie
     * @return un libellé de navigation ou null
     */
    public String save() {
        categoryBusiness.creer(category);
        category = new Category(); // Réinitialise l'objet après insertion
        categories = null; // Force le rafraîchissement de la liste
        return null;
    }

    // --- Getters et Setters ---

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}