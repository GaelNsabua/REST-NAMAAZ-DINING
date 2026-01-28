package com.namaaz.webapp.clients.orders.client;

import com.namaaz.webapp.clients.orders.dto.ReservationDTO;
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
public class ReservationServiceClient {

    private static final Logger LOGGER = Logger.getLogger(ReservationServiceClient.class.getName());
    private static final String BASE_URL = "http://localhost:8080/service-reservations/api";
    
    private final Client client;
    private final Jsonb jsonb;

    public ReservationServiceClient() {
        this.client = ClientBuilder.newClient();
        this.jsonb = JsonbBuilder.create();
    }

    public List<ReservationDTO> getReservationsByClient(String clientId) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/reservations/client/" + clientId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<ReservationDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching reservations for client " + clientId, e);
            return List.of();
        }
    }
}
