package com.namaaz.webapp.bean;

import com.namaaz.webapp.client.MenuClient;
import com.namaaz.webapp.dto.CategoryDTO;
import com.namaaz.webapp.dto.MenuItemDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Named
@ViewScoped
public class MenuBean implements Serializable {
    
    @Inject
    private MenuClient menuClient;
    
    private List<MenuItemDTO> menuItems;
    private List<CategoryDTO> categories;
    private MenuItemDTO selectedItem;
    private MenuItemDTO newItem;
    private String selectedCategoryId;
    
    @PostConstruct
    public void init() {
        loadCategories();
        loadMenuItems();
        newItem = new MenuItemDTO();
        newItem.setAvailable(true);
    }
    
    public void loadCategories() {
        try {
            categories = menuClient.getAllCategories();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de charger les catégories");
        }
    }
    
    public void loadMenuItems() {
        try {
            if (selectedCategoryId != null && !selectedCategoryId.isEmpty()) {
                menuItems = menuClient.getMenuItemsByCategory(selectedCategoryId);
            } else {
                menuItems = menuClient.getAllMenuItems();
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de charger le menu");
        }
    }
    
    public void createMenuItem() {
        try {
            menuClient.createMenuItem(newItem);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Article ajouté avec succès");
            newItem = new MenuItemDTO();
            newItem.setAvailable(true);
            loadMenuItems();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de créer l'article");
        }
    }
    
    public void updateMenuItem() {
        try {
            menuClient.updateMenuItem(selectedItem.getId(), selectedItem);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Article modifié avec succès");
            loadMenuItems();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de modifier l'article");
        }
    }
    
    public void deleteMenuItem(String id) {
        try {
            menuClient.deleteMenuItem(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Article supprimé avec succès");
            loadMenuItems();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer l'article");
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
    public List<MenuItemDTO> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItemDTO> menuItems) { this.menuItems = menuItems; }
    
    public List<CategoryDTO> getCategories() { return categories; }
    public void setCategories(List<CategoryDTO> categories) { this.categories = categories; }
    
    public MenuItemDTO getSelectedItem() { return selectedItem; }
    public void setSelectedItem(MenuItemDTO selectedItem) { this.selectedItem = selectedItem; }
    
    public MenuItemDTO getNewItem() { return newItem; }
    public void setNewItem(MenuItemDTO newItem) { this.newItem = newItem; }
    
    public String getSelectedCategoryId() { return selectedCategoryId; }
    public void setSelectedCategoryId(String selectedCategoryId) { 
        this.selectedCategoryId = selectedCategoryId;
        loadMenuItems();
    }
}
