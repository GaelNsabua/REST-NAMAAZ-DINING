package com.namaaz.webapp.clients.orders.client;

import com.namaaz.webapp.clients.orders.dto.MenuItemDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class MenuServiceClient {

    private static final Logger LOGGER = Logger.getLogger(MenuServiceClient.class.getName());
    private static final String BASE_URL = "http://10.184.254.196:8080/service-menu/api";
    
    private final Client client;
    private final Jsonb jsonb;

    public MenuServiceClient() {
        this.client = ClientBuilder.newClient();
        this.jsonb = JsonbBuilder.create();
    }

    public List<MenuItemDTO> getAvailableMenuItems() {
        try {
            Response response = client.target(BASE_URL)
                    .path("/menu-items")
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
                    .path("/menu-items/" + id)
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
}
