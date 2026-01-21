package com.namaaz.service.clients.orders.rest;

import com.namaaz.service.clients.orders.business.ClientService;
import com.namaaz.service.clients.orders.entities.Client;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * API REST pour la gestion des clients
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {
    
    @EJB
    private ClientService clientService;
    
    /**
     * Créer un nouveau client
     * POST /api/clients
     */
    @POST
    public Response createClient(@Valid Client client) {
        try {
            Client created = clientService.createClient(client);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error creating client")).build();
        }
    }
    
    /**
     * Récupérer tous les clients
     * GET /api/clients
     */
    @GET
    public Response getAllClients(@QueryParam("search") String searchTerm) {
        try {
            List<Client> clients;
            if (searchTerm != null && !searchTerm.isEmpty()) {
                clients = clientService.searchClientsByName(searchTerm);
            } else {
                clients = clientService.getAllClients();
            }
            return Response.ok(clients).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving clients")).build();
        }
    }
    
    /**
     * Récupérer un client par ID
     * GET /api/clients/{id}
     */
    @GET
    @Path("/{id}")
    public Response getClientById(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            Optional<Client> client = clientService.getClientById(id);
            
            if (client.isPresent()) {
                return Response.ok(client.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Client not found")).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid UUID format")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving client")).build();
        }
    }
    
    /**
     * Récupérer un client par email
     * GET /api/clients/email/{email}
     */
    @GET
    @Path("/email/{email}")
    public Response getClientByEmail(@PathParam("email") String email) {
        try {
            Optional<Client> client = clientService.getClientByEmail(email);
            
            if (client.isPresent()) {
                return Response.ok(client.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Client not found")).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving client")).build();
        }
    }
    
    /**
     * Mettre à jour un client
     * PUT /api/clients/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateClient(@PathParam("id") String idString, @Valid Client client) {
        try {
            UUID id = UUID.fromString(idString);
            Optional<Client> updated = clientService.updateClient(id, client);
            
            if (updated.isPresent()) {
                return Response.ok(updated.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Client not found")).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error updating client")).build();
        }
    }
    
    /**
     * Supprimer un client
     * DELETE /api/clients/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            boolean deleted = clientService.deleteClient(id);
            
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Client not found")).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid UUID format")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error deleting client")).build();
        }
    }
    
    /**
     * Compter le nombre de clients
     * GET /api/clients/count
     */
    @GET
    @Path("/count")
    public Response countClients() {
        try {
            long count = clientService.countClients();
            return Response.ok(new CountResponse(count)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error counting clients")).build();
        }
    }
    
    // Classes utilitaires pour les réponses
    public static class ErrorResponse {
        public String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
    
    public static class CountResponse {
        public long count;
        
        public CountResponse(long count) {
            this.count = count;
        }
    }
}
