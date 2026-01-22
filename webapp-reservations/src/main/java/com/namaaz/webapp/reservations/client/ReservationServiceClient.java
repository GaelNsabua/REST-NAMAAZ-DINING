package com.namaaz.webapp.reservations.client;

import com.namaaz.webapp.reservations.dto.RestaurantTableDTO;
import com.namaaz.webapp.reservations.dto.ReservationDTO;
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

@ApplicationScoped
public class ReservationServiceClient {

    private static final Logger LOGGER = Logger.getLogger(ReservationServiceClient.class.getName());
    private static final String BASE_URL = "http://localhost:8080/service-reservations-1.0/api";
    
    private final Client client;
    private final Jsonb jsonb;

    public ReservationServiceClient() {
        this.client = ClientBuilder.newClient();
        this.jsonb = JsonbBuilder.create();
    }

    // ========== TABLES ==========

    public List<RestaurantTableDTO> getAllTables() {
        try {
            Response response = client.target(BASE_URL)
                    .path("/tables")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<RestaurantTableDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching tables", e);
            return List.of();
        }
    }

    public List<RestaurantTableDTO> getTablesByStatus(String status) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/tables/status/" + status)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<RestaurantTableDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching tables by status", e);
            return List.of();
        }
    }

    public RestaurantTableDTO getTableById(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/tables/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(RestaurantTableDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching table " + id, e);
            return null;
        }
    }

    public RestaurantTableDTO createTable(RestaurantTableDTO table) {
        try {
            String json = jsonb.toJson(table);
            Response response = client.target(BASE_URL)
                    .path("/tables")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 201) {
                return response.readEntity(RestaurantTableDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating table", e);
            return null;
        }
    }

    public RestaurantTableDTO updateTable(String id, RestaurantTableDTO table) {
        try {
            String json = jsonb.toJson(table);
            Response response = client.target(BASE_URL)
                    .path("/tables/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 200) {
                return response.readEntity(RestaurantTableDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating table " + id, e);
            return null;
        }
    }

    public boolean deleteTable(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/tables/" + id)
                    .request()
                    .delete();
            
            return response.getStatus() == 204;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting table " + id, e);
            return false;
        }
    }

    public boolean updateTableStatus(String id, String status) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/tables/" + id + "/status")
                    .queryParam("status", status)
                    .request()
                    .put(Entity.json(""));
            
            return response.getStatus() == 200;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating table status " + id, e);
            return false;
        }
    }

    // ========== RESERVATIONS ==========

    public List<ReservationDTO> getAllReservations() {
        try {
            Response response = client.target(BASE_URL)
                    .path("/reservations")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<ReservationDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching reservations", e);
            return List.of();
        }
    }

    public List<ReservationDTO> getReservationsByStatus(String status) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/reservations/status/" + status)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<ReservationDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching reservations by status", e);
            return List.of();
        }
    }

    public ReservationDTO getReservationById(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/reservations/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ReservationDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching reservation " + id, e);
            return null;
        }
    }

    public ReservationDTO createReservation(ReservationDTO reservation) {
        try {
            String json = jsonb.toJson(reservation);
            Response response = client.target(BASE_URL)
                    .path("/reservations")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 201) {
                return response.readEntity(ReservationDTO.class);
            }
            LOGGER.log(Level.WARNING, "Failed to create reservation: {0}", response.getStatus());
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating reservation", e);
            return null;
        }
    }

    public ReservationDTO updateReservation(String id, ReservationDTO reservation) {
        try {
            String json = jsonb.toJson(reservation);
            Response response = client.target(BASE_URL)
                    .path("/reservations/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 200) {
                return response.readEntity(ReservationDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating reservation " + id, e);
            return null;
        }
    }

    public boolean deleteReservation(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/reservations/" + id)
                    .request()
                    .delete();
            
            return response.getStatus() == 204;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting reservation " + id, e);
            return false;
        }
    }

    public boolean confirmReservation(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/reservations/" + id + "/confirm")
                    .request()
                    .put(Entity.json(""));
            
            return response.getStatus() == 200;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error confirming reservation " + id, e);
            return false;
        }
    }

    public boolean cancelReservation(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/reservations/" + id + "/cancel")
                    .request()
                    .put(Entity.json(""));
            
            return response.getStatus() == 200;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling reservation " + id, e);
            return false;
        }
    }
}
