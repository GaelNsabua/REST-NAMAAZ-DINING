package com.namaaz.webapp.clients.orders.client;

import com.namaaz.webapp.clients.orders.dto.ClientDTO;
import com.namaaz.webapp.clients.orders.dto.OrderDTO;
import com.namaaz.webapp.clients.orders.dto.PaymentDTO;
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
public class ClientOrderServiceClient {

    private static final Logger LOGGER = Logger.getLogger(ClientOrderServiceClient.class.getName());
    private static final String BASE_URL = "http://localhost:8080/service-clients-orders-1.0/api";
    
    private final Client client;
    private final Jsonb jsonb;

    public ClientOrderServiceClient() {
        this.client = ClientBuilder.newClient();
        this.jsonb = JsonbBuilder.create();
    }

    // ========== CLIENTS ==========

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

    public ClientDTO createClient(ClientDTO clientDTO) {
        try {
            String json = jsonb.toJson(clientDTO);
            Response response = client.target(BASE_URL)
                    .path("/clients")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 201) {
                return response.readEntity(ClientDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating client", e);
            return null;
        }
    }

    public ClientDTO updateClient(String id, ClientDTO clientDTO) {
        try {
            String json = jsonb.toJson(clientDTO);
            Response response = client.target(BASE_URL)
                    .path("/clients/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 200) {
                return response.readEntity(ClientDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating client " + id, e);
            return null;
        }
    }

    public boolean deleteClient(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/clients/" + id)
                    .request()
                    .delete();
            
            return response.getStatus() == 204;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting client " + id, e);
            return false;
        }
    }

    // ========== ORDERS ==========

    public List<OrderDTO> getAllOrders() {
        try {
            Response response = client.target(BASE_URL)
                    .path("/orders")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<OrderDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching orders", e);
            return List.of();
        }
    }

    public List<OrderDTO> getOrdersByStatus(String status) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/orders/status/" + status)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<OrderDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching orders by status", e);
            return List.of();
        }
    }

    public OrderDTO getOrderById(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/orders/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(OrderDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching order " + id, e);
            return null;
        }
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            String json = jsonb.toJson(orderDTO);
            Response response = client.target(BASE_URL)
                    .path("/orders")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 201) {
                return response.readEntity(OrderDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating order", e);
            return null;
        }
    }

    public boolean deleteOrder(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/orders/" + id)
                    .request()
                    .delete();
            
            return response.getStatus() == 204;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting order " + id, e);
            return false;
        }
    }

    public boolean completeOrder(String id) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/orders/" + id + "/complete")
                    .request()
                    .put(Entity.json(""));
            
            return response.getStatus() == 200;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error completing order " + id, e);
            return false;
        }
    }

    // ========== PAYMENTS ==========

    public List<PaymentDTO> getAllPayments() {
        try {
            Response response = client.target(BASE_URL)
                    .path("/payments")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return jsonb.fromJson(json, new GenericType<List<PaymentDTO>>() {}.getType());
            }
            return List.of();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching payments", e);
            return List.of();
        }
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        try {
            String json = jsonb.toJson(paymentDTO);
            Response response = client.target(BASE_URL)
                    .path("/payments")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 201) {
                return response.readEntity(PaymentDTO.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating payment", e);
            return null;
        }
    }
}
