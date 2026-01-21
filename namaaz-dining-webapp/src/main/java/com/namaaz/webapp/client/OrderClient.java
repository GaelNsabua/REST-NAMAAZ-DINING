package com.namaaz.webapp.client;

import com.namaaz.webapp.dto.ClientDTO;
import com.namaaz.webapp.dto.OrderDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
public class OrderClient {
    private static final String BASE_URL = "http://localhost:8080/service-clients-orders/api";
    private final Client client;
    
    public OrderClient() {
        this.client = ClientBuilder.newClient();
    }
    
    // Clients
    public List<ClientDTO> getAllClients() {
        return client.target(BASE_URL + "/clients")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ClientDTO>>() {});
    }
    
    public ClientDTO getClientById(String id) {
        return client.target(BASE_URL + "/clients/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get(ClientDTO.class);
    }
    
    public ClientDTO createClient(ClientDTO clientDTO) {
        Response response = client.target(BASE_URL + "/clients")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(clientDTO));
        return response.readEntity(ClientDTO.class);
    }
    
    public void updateClient(String id, ClientDTO clientDTO) {
        client.target(BASE_URL + "/clients/" + id)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(clientDTO));
    }
    
    public void deleteClient(String id) {
        client.target(BASE_URL + "/clients/" + id)
                .request()
                .delete();
    }
    
    // Orders
    public List<OrderDTO> getAllOrders() {
        return client.target(BASE_URL + "/orders")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<OrderDTO>>() {});
    }
    
    public OrderDTO getOrderById(String id) {
        return client.target(BASE_URL + "/orders/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get(OrderDTO.class);
    }
    
    public OrderDTO createOrder(OrderDTO order) {
        Response response = client.target(BASE_URL + "/orders")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(order));
        return response.readEntity(OrderDTO.class);
    }
    
    public void updateOrder(String id, OrderDTO order) {
        client.target(BASE_URL + "/orders/" + id)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(order));
    }
}
