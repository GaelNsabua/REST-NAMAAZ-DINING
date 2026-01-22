package com.namaaz.webapp.menu.bean;

import com.namaaz.webapp.menu.client.MenuServiceClient;
import com.namaaz.webapp.menu.dto.CategoryDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

/**
 * Managed Bean for Category management
 */
@Named
@ViewScoped
public class CategoryBean implements Serializable {

    @Inject
    private MenuServiceClient menuServiceClient;

    private List<CategoryDTO> categories;
    private CategoryDTO selectedCategory;
    private CategoryDTO newCategory;
    private boolean showDialog;

    @PostConstruct
    public void init() {
        loadCategories();
        newCategory = new CategoryDTO();
        newCategory.setActive(true);
    }

    public void loadCategories() {
        categories = menuServiceClient.getAllCategories();
    }

    public void prepareNewCategory() {
        newCategory = new CategoryDTO();
        newCategory.setActive(true);
        showDialog = true;
    }

    public void prepareEditCategory(CategoryDTO category) {
        selectedCategory = category;
        newCategory = new CategoryDTO();
        newCategory.setId(category.getId());
        newCategory.setName(category.getName());
        newCategory.setDescription(category.getDescription());
        newCategory.setActive(category.getActive());
        showDialog = true;
    }

    public void saveCategory() {
        try {
            if (newCategory.getId() == null || newCategory.getId().isEmpty()) {
                // Create
                CategoryDTO created = menuServiceClient.createCategory(newCategory);
                if (created != null) {
                    addMessage("Succès", "Catégorie créée avec succès", FacesMessage.SEVERITY_INFO);
                    loadCategories();
                } else {
                    addMessage("Erreur", "Impossible de créer la catégorie", FacesMessage.SEVERITY_ERROR);
                }
            } else {
                // Update
                CategoryDTO updated = menuServiceClient.updateCategory(newCategory.getId(), newCategory);
                if (updated != null) {
                    addMessage("Succès", "Catégorie mise à jour avec succès", FacesMessage.SEVERITY_INFO);
                    loadCategories();
                } else {
                    addMessage("Erreur", "Impossible de mettre à jour la catégorie", FacesMessage.SEVERITY_ERROR);
                }
            }
            showDialog = false;
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void deleteCategory(String id) {
        try {
            boolean deleted = menuServiceClient.deleteCategory(id);
            if (deleted) {
                addMessage("Succès", "Catégorie supprimée avec succès", FacesMessage.SEVERITY_INFO);
                loadCategories();
            } else {
                addMessage("Erreur", "Impossible de supprimer la catégorie", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void cancelDialog() {
        showDialog = false;
        newCategory = new CategoryDTO();
        newCategory.setActive(true);
    }

    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public CategoryDTO getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(CategoryDTO selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public CategoryDTO getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(CategoryDTO newCategory) {
        this.newCategory = newCategory;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }
}
