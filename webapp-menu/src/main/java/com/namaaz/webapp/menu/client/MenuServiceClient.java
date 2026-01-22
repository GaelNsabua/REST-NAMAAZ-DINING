package com.namaaz.webapp.menu.client;

import com.namaaz.webapp.menu.dto.CategoryDTO;
import com.namaaz.webapp.menu.dto.MenuItemDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST Client to consume service-menu API
 */
@ApplicationScoped
public class MenuServiceClient {

    private static final Logger LOGGER = Logger.getLogger(MenuServiceClient.class.getName());
    private static final String BASE_URL = "http://localhost:8080/service-menu-1.0/api";
    
    private final Client client;
    private final Jsonb jsonb;

    public MenuServiceClient() {
        this.client = ClientBuilder.newClient();
        this.jsonb = JsonbBuilder.create();
    }

    // ========== CATEGORIES ==========

    public List<CategoryDTO> getAllCategories() {
        try {
            Response response = client.target(BASE_URL)
                    .path("/categories")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<CategoryDTO>>() {}.getType());
            }
            LOGGER.log(Level.WARNING, "Failed to fetch categories: {0}", response.getStatus());
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching categories", e);
            return List.of();
        }
    }

    public CategoryDTO getCategoryById(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/categories/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(CategoryDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching category " + id, e);
            return null;
        }
    }

    public CategoryDTO createCategory(CategoryDTO category) {
        try {
            String json = jsonb.toJson(category);
            Response response = client.target(BASE_URL)
                    .path("/categories")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 201) {
                return response.readEntity(CategoryDTO.class);
            }
            LOGGER.log(Level.WARNING, "Failed to create category: {0}", response.getStatus());
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating category", e);
            return null;
        }
    }

    public CategoryDTO updateCategory(String id, CategoryDTO category) {
        try {
            String json = jsonb.toJson(category);
            Response response = client.target(BASE_URL)
                    .path("/categories/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 200) {
                return response.readEntity(CategoryDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating category " + id, e);
            return null;
        }
    }

    public boolean deleteCategory(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/categories/" + id)
                    .request()
                    .delete();
            
            return response.getStatus() == 204;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting category " + id, e);
            return false;
        }
    }

    // ========== MENU ITEMS ==========

    public List<MenuItemDTO> getAllMenuItems() {
        try {
            Response response = client.target(BASE_URL)
                    .path("/menu")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<MenuItemDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching menu items", e);
            return List.of();
        }
    }

    public List<MenuItemDTO> getMenuItemsByCategory(String categoryId) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/menu/category/" + categoryId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<MenuItemDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching menu items for category " + categoryId, e);
            return List.of();
        }
    }

    public List<MenuItemDTO> getAvailableMenuItems() {
        try {
            Response response = client.target(BASE_URL)
                    .path("/menu/available")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<MenuItemDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching available menu items", e);
            return List.of();
        }
    }

    public MenuItemDTO getMenuItemById(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/menu/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(MenuItemDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching menu item " + id, e);
            return null;
        }
    }

    public MenuItemDTO createMenuItem(MenuItemDTO menuItem) {
        try {
            String json = jsonb.toJson(menuItem);
            Response response = client.target(BASE_URL)
                    .path("/menu")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 201) {
                return response.readEntity(MenuItemDTO.class);
            }
            LOGGER.log(Level.WARNING, "Failed to create menu item: {0}", response.getStatus());
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating menu item", e);
            return null;
        }
    }

    public MenuItemDTO updateMenuItem(String id, MenuItemDTO menuItem) {
        try {
            String json = jsonb.toJson(menuItem);
            Response response = client.target(BASE_URL)
                    .path("/menu/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 200) {
                return response.readEntity(MenuItemDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating menu item " + id, e);
            return null;
        }
    }

    public boolean deleteMenuItem(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/menu/" + id)
                    .request()
                    .delete();
            
            return response.getStatus() == 204;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting menu item " + id, e);
            return false;
        }
    }

    public boolean updateMenuItemAvailability(String id, boolean available) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/menu/" + id + "/availability")
                    .queryParam("available", available)
                    .request()
                    .put(Entity.json(""));
            
            return response.getStatus() == 200;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating availability for menu item " + id, e);
            return false;
        }
    }
}
