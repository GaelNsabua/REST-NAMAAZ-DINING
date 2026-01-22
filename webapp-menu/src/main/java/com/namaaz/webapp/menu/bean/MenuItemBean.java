package com.namaaz.webapp.menu.bean;

import com.namaaz.webapp.menu.client.MenuServiceClient;
import com.namaaz.webapp.menu.dto.CategoryDTO;
import com.namaaz.webapp.menu.dto.MenuItemDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed Bean for MenuItem management
 */
@Named
@ViewScoped
public class MenuItemBean implements Serializable {

    @Inject
    private MenuServiceClient menuServiceClient;

    private List<MenuItemDTO> menuItems;
    private List<CategoryDTO> categories;
    private MenuItemDTO selectedMenuItem;
    private MenuItemDTO newMenuItem;
    private boolean showDialog;
    
    // Filters
    private String filterCategoryId;
    private Boolean filterAvailable;

    @PostConstruct
    public void init() {
        loadCategories();
        loadMenuItems();
        newMenuItem = new MenuItemDTO();
        newMenuItem.setAvailable(true);
        newMenuItem.setPrice(BigDecimal.ZERO);
    }

    public void loadCategories() {
        categories = menuServiceClient.getAllCategories();
    }

    public void loadMenuItems() {
        if (filterCategoryId != null && !filterCategoryId.isEmpty() && !filterCategoryId.equals("ALL")) {
            menuItems = menuServiceClient.getMenuItemsByCategory(filterCategoryId);
        } else if (Boolean.TRUE.equals(filterAvailable)) {
            menuItems = menuServiceClient.getAvailableMenuItems();
        } else {
            menuItems = menuServiceClient.getAllMenuItems();
        }
        
        // Enrich with category names
        for (MenuItemDTO item : menuItems) {
            CategoryDTO category = categories.stream()
                    .filter(c -> c.getId().equals(item.getCategoryId()))
                    .findFirst()
                    .orElse(null);
            if (category != null) {
                item.setCategoryName(category.getName());
            }
        }
    }

    public void applyFilters() {
        loadMenuItems();
    }

    public void clearFilters() {
        filterCategoryId = null;
        filterAvailable = null;
        loadMenuItems();
    }

    public void prepareNewMenuItem() {
        newMenuItem = new MenuItemDTO();
        newMenuItem.setAvailable(true);
        newMenuItem.setPrice(BigDecimal.ZERO);
        showDialog = true;
    }

    public void prepareEditMenuItem(MenuItemDTO item) {
        selectedMenuItem = item;
        newMenuItem = new MenuItemDTO();
        newMenuItem.setId(item.getId());
        newMenuItem.setCategoryId(item.getCategoryId());
        newMenuItem.setName(item.getName());
        newMenuItem.setDescription(item.getDescription());
        newMenuItem.setPrice(item.getPrice());
        newMenuItem.setAvailable(item.getAvailable());
        showDialog = true;
    }

    public void saveMenuItem() {
        try {
            if (newMenuItem.getId() == null || newMenuItem.getId().isEmpty()) {
                // Create
                MenuItemDTO created = menuServiceClient.createMenuItem(newMenuItem);
                if (created != null) {
                    addMessage("Succès", "Plat créé avec succès", FacesMessage.SEVERITY_INFO);
                    loadMenuItems();
                } else {
                    addMessage("Erreur", "Impossible de créer le plat", FacesMessage.SEVERITY_ERROR);
                }
            } else {
                // Update
                MenuItemDTO updated = menuServiceClient.updateMenuItem(newMenuItem.getId(), newMenuItem);
                if (updated != null) {
                    addMessage("Succès", "Plat mis à jour avec succès", FacesMessage.SEVERITY_INFO);
                    loadMenuItems();
                } else {
                    addMessage("Erreur", "Impossible de mettre à jour le plat", FacesMessage.SEVERITY_ERROR);
                }
            }
            showDialog = false;
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void deleteMenuItem(String id) {
        try {
            boolean deleted = menuServiceClient.deleteMenuItem(id);
            if (deleted) {
                addMessage("Succès", "Plat supprimé avec succès", FacesMessage.SEVERITY_INFO);
                loadMenuItems();
            } else {
                addMessage("Erreur", "Impossible de supprimer le plat", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void toggleAvailability(String id, boolean currentAvailability) {
        try {
            boolean updated = menuServiceClient.updateMenuItemAvailability(id, !currentAvailability);
            if (updated) {
                String status = !currentAvailability ? "disponible" : "indisponible";
                addMessage("Succès", "Plat marqué comme " + status, FacesMessage.SEVERITY_INFO);
                loadMenuItems();
            } else {
                addMessage("Erreur", "Impossible de mettre à jour la disponibilité", FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Erreur", "Une erreur s'est produite: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void cancelDialog() {
        showDialog = false;
        newMenuItem = new MenuItemDTO();
        newMenuItem.setAvailable(true);
        newMenuItem.setPrice(BigDecimal.ZERO);
    }

    public List<SelectItem> getCategorySelectItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("", "-- Sélectionner une catégorie --"));
        for (CategoryDTO category : categories) {
            items.add(new SelectItem(category.getId(), category.getName()));
        }
        return items;
    }

    public List<SelectItem> getCategoryFilterItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("ALL", "Toutes les catégories"));
        for (CategoryDTO category : categories) {
            items.add(new SelectItem(category.getId(), category.getName()));
        }
        return items;
    }

    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<MenuItemDTO> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemDTO> menuItems) {
        this.menuItems = menuItems;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public MenuItemDTO getSelectedMenuItem() {
        return selectedMenuItem;
    }

    public void setSelectedMenuItem(MenuItemDTO selectedMenuItem) {
        this.selectedMenuItem = selectedMenuItem;
    }

    public MenuItemDTO getNewMenuItem() {
        return newMenuItem;
    }

    public void setNewMenuItem(MenuItemDTO newMenuItem) {
        this.newMenuItem = newMenuItem;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public String getFilterCategoryId() {
        return filterCategoryId;
    }

    public void setFilterCategoryId(String filterCategoryId) {
        this.filterCategoryId = filterCategoryId;
    }

    public Boolean getFilterAvailable() {
        return filterAvailable;
    }

    public void setFilterAvailable(Boolean filterAvailable) {
        this.filterAvailable = filterAvailable;
    }
}
