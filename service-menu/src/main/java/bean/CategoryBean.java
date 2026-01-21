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
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CategoryBean implements Serializable {

    @Inject
    private MenuService menuService;

    private List<Category> categories;
    private Category newCategory = new Category();

    @PostConstruct
    public void init() {
        loadCategories();
    }

    public void loadCategories() {
        categories = menuService.getAllCategories();
    }

    public String save() {
        menuService.createCategory(newCategory);
        newCategory = new Category(); // Reset le formulaire
        loadCategories();
        return null; // Reste sur la même page
    }

    public void delete(Category category) {
        menuService.deleteCategory(category.getId());
        loadCategories();
    }

    // Getters et Setters
    public List<Category> getCategories() { return categories; }
    public Category getNewCategory() { return newCategory; }
    public void setNewCategory(Category newCategory) { this.newCategory = newCategory; }
}

