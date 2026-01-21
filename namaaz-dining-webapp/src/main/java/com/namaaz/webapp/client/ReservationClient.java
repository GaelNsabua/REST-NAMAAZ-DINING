package com.namaaz.webapp.client;

import com.namaaz.webapp.dto.RestaurantTableDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
public class ReservationClient {
    private static final String BASE_URL = "http://localhost:8080/service-reservations/api";
    private final Client client;
    
    public ReservationClient() {
        this.client = ClientBuilder.newClient();
    }
    
    // Tables
    public List<RestaurantTableDTO> getAllTables() {
        return client.target(BASE_URL + "/tables")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<RestaurantTableDTO>>() {});
    }
    
    public RestaurantTableDTO getTableById(String id) {
        return client.target(BASE_URL + "/tables/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get(RestaurantTableDTO.class);
    }
    
    public List<RestaurantTableDTO> getTablesByStatus(String status) {
        return client.target(BASE_URL + "/tables/status/" + status)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<RestaurantTableDTO>>() {});
    }
    
    public RestaurantTableDTO createTable(RestaurantTableDTO table) {
        Response response = client.target(BASE_URL + "/tables")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(table));
        return response.readEntity(RestaurantTableDTO.class);
    }
    
    public void updateTable(String id, RestaurantTableDTO table) {
        client.target(BASE_URL + "/tables/" + id)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(table));
    }
    
    public void deleteTable(String id) {
        client.target(BASE_URL + "/tables/" + id)
                .request()
                .delete();
    }
}
