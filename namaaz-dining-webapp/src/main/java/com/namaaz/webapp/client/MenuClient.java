package com.namaaz.webapp.client;

import com.namaaz.webapp.dto.CategoryDTO;
import com.namaaz.webapp.dto.MenuItemDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
public class MenuClient {
    private static final String BASE_URL = "http://localhost:8080/service-menu/api";
    private final Client client;
    
    public MenuClient() {
        this.client = ClientBuilder.newClient();
    }
    
    // Categories
    public List<CategoryDTO> getAllCategories() {
        return client.target(BASE_URL + "/categories")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<CategoryDTO>>() {});
    }
    
    public CategoryDTO getCategoryById(String id) {
        return client.target(BASE_URL + "/categories/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get(CategoryDTO.class);
    }
    
    public CategoryDTO createCategory(CategoryDTO category) {
        Response response = client.target(BASE_URL + "/categories")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(category));
        return response.readEntity(CategoryDTO.class);
    }
    
    public void updateCategory(String id, CategoryDTO category) {
        client.target(BASE_URL + "/categories/" + id)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(category));
    }
    
    public void deleteCategory(String id) {
        client.target(BASE_URL + "/categories/" + id)
                .request()
                .delete();
    }
    
    // Menu Items
    public List<MenuItemDTO> getAllMenuItems() {
        return client.target(BASE_URL + "/menu-items")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<MenuItemDTO>>() {});
    }
    
    public MenuItemDTO getMenuItemById(String id) {
        return client.target(BASE_URL + "/menu-items/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get(MenuItemDTO.class);
    }
    
    public List<MenuItemDTO> getMenuItemsByCategory(String categoryId) {
        return client.target(BASE_URL + "/menu-items/category/" + categoryId)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<MenuItemDTO>>() {});
    }
    
    public MenuItemDTO createMenuItem(MenuItemDTO menuItem) {
        Response response = client.target(BASE_URL + "/menu-items")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(menuItem));
        return response.readEntity(MenuItemDTO.class);
    }
    
    public void updateMenuItem(String id, MenuItemDTO menuItem) {
        client.target(BASE_URL + "/menu-items/" + id)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(menuItem));
    }
    
    public void deleteMenuItem(String id) {
        client.target(BASE_URL + "/menu-items/" + id)
                .request()
                .delete();
    }
}
