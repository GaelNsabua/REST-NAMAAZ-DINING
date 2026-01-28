package com.namaaz.service.clients.orders.rest;

import com.namaaz.service.clients.orders.business.OrderService;
import com.namaaz.service.clients.orders.entities.Order;
import com.namaaz.service.clients.orders.entities.OrderItem;
import com.namaaz.service.clients.orders.entities.OrderStatus;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * API REST pour la gestion des commandes
 */
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    
    @EJB
    private OrderService orderService;
    
    /**
     * Créer une nouvelle commande
     * POST /api/orders
     */
    @POST
    public Response createOrder(@Valid CreateOrderRequest request) {
        try {
            Order order = new Order(request.clientId);
            order.setReservationId(request.reservationId);
            order.setTableId(request.tableId);
            
            // Convertir OrderItemRequest en OrderItem
            List<OrderItem> items = new ArrayList<>();
            if (request.items != null) {
                for (OrderItemRequest itemReq : request.items) {
                    OrderItem item = new OrderItem(
                        itemReq.menuItemId,
                        itemReq.quantity,
                        itemReq.unitPrice
                    );
                    items.add(item);
                }
            }
            
            Order created = orderService.createOrder(order, items);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error creating order: " + e.getMessage())).build();
        }
    }
    
    /**
     * Récupérer toutes les commandes
     * GET /api/orders
     */
    @GET
    public Response getAllOrders(@QueryParam("clientId") String clientIdString,
                                  @QueryParam("status") String statusString,
                                  @QueryParam("reservationId") String reservationIdString) {
        try {
            List<Order> orders;
            
            if (clientIdString != null && !clientIdString.isEmpty()) {
                UUID clientId = UUID.fromString(clientIdString);
                orders = orderService.getOrdersByClientId(clientId);
            } else if (statusString != null && !statusString.isEmpty()) {
                OrderStatus status = OrderStatus.valueOf(statusString.toUpperCase());
                orders = orderService.getOrdersByStatus(status);
            } else if (reservationIdString != null && !reservationIdString.isEmpty()) {
                UUID reservationId = UUID.fromString(reservationIdString);
                orders = orderService.getOrdersByReservationId(reservationId);
            } else {
                orders = orderService.getAllOrders();
            }
            
            return Response.ok(orders).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid parameter format")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving orders")).build();
        }
    }
    
    /**
     * Récupérer une commande par ID
     * GET /api/orders/{id}
     */
    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            Optional<Order> order = orderService.getOrderById(id);
            
            if (order.isPresent()) {
                return Response.ok(order.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Order not found")).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid UUID format")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving order")).build();
        }
    }
    
    /**
     * Mettre à jour le statut d'une commande
     * PUT /api/orders/{id}/status
     */
    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(@PathParam("id") String idString, UpdateStatusRequest request) {
        try {
            UUID id = UUID.fromString(idString);
            OrderStatus status = OrderStatus.valueOf(request.status.toUpperCase());
            Optional<Order> updated = orderService.updateOrderStatus(id, status);
            
            if (updated.isPresent()) {
                return Response.ok(updated.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Order not found")).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid parameter format")).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error updating order status")).build();
        }
    }
    
    /**
     * Marquer une commande comme en cours
     * POST /api/orders/{id}/start
     */
    @POST
    @Path("/{id}/start")
    public Response startOrder(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            Optional<Order> updated = orderService.markOrderInProgress(id);
            
            if (updated.isPresent()) {
                return Response.ok(updated.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Order not found")).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error starting order")).build();
        }
    }
    
    /**
     * Terminer une commande
     * POST /api/orders/{id}/complete
     */
    @POST
    @Path("/{id}/complete")
    public Response completeOrder(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            Optional<Order> updated = orderService.completeOrder(id);
            
            if (updated.isPresent()) {
                return Response.ok(updated.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Order not found")).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error completing order")).build();
        }
    }
    
    /**
     * Annuler une commande
     * POST /api/orders/{id}/cancel
     */
    @POST
    @Path("/{id}/cancel")
    public Response cancelOrder(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            Optional<Order> updated = orderService.cancelOrder(id);
            
            if (updated.isPresent()) {
                return Response.ok(updated.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Order not found")).build();
            }
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error cancelling order")).build();
        }
    }
    
    /**
     * Supprimer une commande
     * DELETE /api/orders/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteOrder(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            boolean deleted = orderService.deleteOrder(id);
            
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Order not found or cannot be deleted")).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid UUID format")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error deleting order")).build();
        }
    }
    
    // Classes DTO pour les requêtes
    public static class CreateOrderRequest {
        public UUID clientId;
        public UUID reservationId;
        public UUID tableId;
        public List<OrderItemRequest> items;
    }
    
    public static class OrderItemRequest {
        public UUID menuItemId;
        public Integer quantity;
        public BigDecimal unitPrice;
    }
    
    public static class UpdateStatusRequest {
        public String status;
    }
    
    public static class ErrorResponse {
        public String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
