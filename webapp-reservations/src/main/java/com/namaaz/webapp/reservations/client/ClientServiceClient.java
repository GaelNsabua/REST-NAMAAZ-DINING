package com.namaaz.webapp.reservations.client;

import com.namaaz.webapp.reservations.dto.ClientDTO;
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
public class ClientServiceClient {

    private static final Logger LOGGER = Logger.getLogger(ClientServiceClient.class.getName());
    private static final String BASE_URL = "http://localhost:8080/service-clients-orders/api";
    
    private final Client client;
    private final Jsonb jsonb;

    public ClientServiceClient() {
        this.client = ClientBuilder.newClient();
        this.jsonb = JsonbBuilder.create();
    }

    public List<ClientDTO> getAllClients() {
        try {
            Response response = client.target(BASE_URL)
                    .path("/clients")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<ClientDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching clients", e);
            return List.of();
        }
    }

    public ClientDTO getClientById(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/clients/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ClientDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching client " + id, e);
            return null;
        }
    }
}
