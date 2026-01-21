/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bean;

/**
 *
 * @author WAITING
 */
import business.MenuService;
import com.namaaz.service.menu.entities.Category;
import entities.MenuItem;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class MenuItemBean implements Serializable {

    @Inject
    private MenuService menuService;

    private List<MenuItem> menuItems;
    private List<Category> categories;
    private MenuItem newItem = new MenuItem();
    private String selectedCategoryId; // Pour récupérer l'ID depuis le menu déroulant

    @PostConstruct
    public void init() {
        loadData();
    }

    public void loadData() {
        menuItems = menuService.getAllMenuItems();
        categories = menuService.getAllCategories();
    }

    public void save() {
        if (selectedCategoryId != null && !selectedCategoryId.isEmpty()) {
            menuService.getCategoryById(UUID.fromString(selectedCategoryId))
                       .ifPresent(cat -> {
                           newItem.setCategory(cat);
                           menuService.createMenuItem(newItem);
                           newItem = new MenuItem(); // Reset
                           selectedCategoryId = null;
                           loadData();
                       });
        }
    }

    public void delete(MenuItem item) {
        menuService.deleteMenuItem(item.getId());
        loadData();
    }

    // Getters et Setters
    public List<MenuItem> getMenuItems() { return menuItems; }
    public List<Category> getCategories() { return categories; }
    public MenuItem getNewItem() { return newItem; }
    public void setNewItem(MenuItem newItem) { this.newItem = newItem; }
    public String getSelectedCategoryId() { return selectedCategoryId; }
    public void setSelectedCategoryId(String selectedCategoryId) { this.selectedCategoryId = selectedCategoryId; }
}