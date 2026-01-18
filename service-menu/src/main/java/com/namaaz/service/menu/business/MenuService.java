package com.namaaz.service.menu.business;


import com.namaaz.service.menu.entities.Category;
import com.namaaz.service.menu.entities.MenuItem;
import com.namaaz.service.menu.repository.CategoryRepository;
import com.namaaz.service.menu.repository.MenuItemRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class MenuService {
    
    @Inject
    private CategoryRepository categoryRepository;
    
    @Inject
    private MenuItemRepository menuItemRepository;
    
    // === CATEGORY OPERATIONS ===
    
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    public Optional<Category> getCategoryById(UUID id) {
        return categoryRepository.findById(id);
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Transactional
    public Optional<Category> updateCategory(UUID id, Category updatedCategory) {
        Optional<Category> existing = categoryRepository.findById(id);
        if (existing.isPresent()) {
            Category category = existing.get();
            category.setName(updatedCategory.getName());
            category.setDescription(updatedCategory.getDescription());
            return Optional.of(categoryRepository.save(category));
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean deleteCategory(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            categoryRepository.delete(category.get());
            return true;
        }
        return false;
    }
    
    // === MENU ITEM OPERATIONS ===
    
    @Transactional
    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }
    
    public Optional<MenuItem> getMenuItemById(UUID id) {
        return menuItemRepository.findById(id);
    }
    
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }
    
    public List<MenuItem> getMenuItemsByCategory(UUID categoryId) {
        return menuItemRepository.findByCategory(categoryId);
    }
    
    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByAvailability(true);
    }
    
    @Transactional
    public Optional<MenuItem> updateMenuItem(UUID id, MenuItem updatedMenuItem) {
        Optional<MenuItem> existing = menuItemRepository.findById(id);
        if (existing.isPresent()) {
            MenuItem menuItem = existing.get();
            menuItem.setName(updatedMenuItem.getName());
            menuItem.setDescription(updatedMenuItem.getDescription());
            menuItem.setPrice(updatedMenuItem.getPrice());
            menuItem.setCategory(updatedMenuItem.getCategory());
            menuItem.setAvailable(updatedMenuItem.getAvailable());
            menuItem.setPrepTime(updatedMenuItem.getPrepTime());
            return Optional.of(menuItemRepository.save(menuItem));
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean deleteMenuItem(UUID id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        if (menuItem.isPresent()) {
            menuItemRepository.delete(menuItem.get());
            return true;
        }
        return false;
    }
}
