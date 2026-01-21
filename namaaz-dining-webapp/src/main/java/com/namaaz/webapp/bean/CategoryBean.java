package com.namaaz.webapp.bean;

import com.namaaz.webapp.client.MenuClient;
import com.namaaz.webapp.dto.CategoryDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CategoryBean implements Serializable {
    
    @Inject
    private MenuClient menuClient;
    
    private List<CategoryDTO> categories;
    private CategoryDTO selectedCategory;
    private CategoryDTO newCategory;
    
    @PostConstruct
    public void init() {
        loadCategories();
        newCategory = new CategoryDTO();
    }
    
    public void loadCategories() {
        try {
            categories = menuClient.getAllCategories();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de charger les catégories");
        }
    }
    
    public void createCategory() {
        try {
            menuClient.createCategory(newCategory);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Catégorie créée avec succès");
            newCategory = new CategoryDTO();
            loadCategories();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de créer la catégorie");
        }
    }
    
    public void updateCategory() {
        try {
            menuClient.updateCategory(selectedCategory.getId(), selectedCategory);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Catégorie modifiée avec succès");
            loadCategories();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de modifier la catégorie");
        }
    }
    
    public void deleteCategory(String id) {
        try {
            menuClient.deleteCategory(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Catégorie supprimée avec succès");
            loadCategories();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer la catégorie");
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
    public List<CategoryDTO> getCategories() { return categories; }
    public void setCategories(List<CategoryDTO> categories) { this.categories = categories; }
    
    public CategoryDTO getSelectedCategory() { return selectedCategory; }
    public void setSelectedCategory(CategoryDTO selectedCategory) { this.selectedCategory = selectedCategory; }
    
    public CategoryDTO getNewCategory() { return newCategory; }
    public void setNewCategory(CategoryDTO newCategory) { this.newCategory = newCategory; }
}
