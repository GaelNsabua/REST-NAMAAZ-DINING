package com.namaaz.service.clients.orders.rest;

import com.namaaz.service.clients.orders.business.PaymentService;
import com.namaaz.service.clients.orders.entities.Payment;
import com.namaaz.service.clients.orders.entities.PaymentMethod;
import com.namaaz.service.clients.orders.entities.PaymentStatus;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * API REST pour la gestion des paiements
 */
@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {
    
    @EJB
    private PaymentService paymentService;
    
    /**
     * Créer un nouveau paiement
     * POST /api/payments
     */
    @POST
    public Response createPayment(@Valid CreatePaymentRequest request) {
        try {
            Payment payment = new Payment(request.orderId, request.amount, request.method);
            Payment created = paymentService.createPayment(payment);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error creating payment")).build();
        }
    }
    
    /**
     * Récupérer tous les paiements
     * GET /api/payments
     */
    @GET
    public Response getAllPayments(@QueryParam("orderId") String orderIdString,
                                    @QueryParam("status") String statusString) {
        try {
            List<Payment> payments;
            
            if (orderIdString != null && !orderIdString.isEmpty()) {
                UUID orderId = UUID.fromString(orderIdString);
                payments = paymentService.getPaymentsByOrderId(orderId);
            } else if (statusString != null && !statusString.isEmpty()) {
                PaymentStatus status = PaymentStatus.valueOf(statusString.toUpperCase());
                payments = paymentService.getPaymentsByStatus(status);
            } else {
                payments = paymentService.getAllPayments();
            }
            
            return Response.ok(payments).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid parameter format")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving payments")).build();
        }
    }
    
    /**
     * Récupérer un paiement par ID
     * GET /api/payments/{id}
     */
    @GET
    @Path("/{id}")
    public Response getPaymentById(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            Optional<Payment> payment = paymentService.getPaymentById(id);
            
            if (payment.isPresent()) {
                return Response.ok(payment.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Payment not found")).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid UUID format")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving payment")).build();
        }
    }
    
    /**
     * Récupérer un paiement par référence de transaction
     * GET /api/payments/transaction/{ref}
     */
    @GET
    @Path("/transaction/{ref}")
    public Response getPaymentByTransactionRef(@PathParam("ref") String transactionRef) {
        try {
            Optional<Payment> payment = paymentService.getPaymentByTransactionRef(transactionRef);
            
            if (payment.isPresent()) {
                return Response.ok(payment.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Payment not found")).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving payment")).build();
        }
    }
    
    /**
     * Confirmer un paiement
     * POST /api/payments/{id}/confirm
     */
    @POST
    @Path("/{id}/confirm")
    public Response confirmPayment(@PathParam("id") String idString, ConfirmPaymentRequest request) {
        try {
            UUID id = UUID.fromString(idString);
            String transactionRef = request != null ? request.transactionRef : null;
            Optional<Payment> updated = paymentService.confirmPayment(id, transactionRef);
            
            if (updated.isPresent()) {
                return Response.ok(updated.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Payment not found")).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error confirming payment")).build();
        }
    }
    
    /**
     * Marquer un paiement comme échoué
     * POST /api/payments/{id}/fail
     */
    @POST
    @Path("/{id}/fail")
    public Response failPayment(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            Optional<Payment> updated = paymentService.failPayment(id);
            
            if (updated.isPresent()) {
                return Response.ok(updated.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Payment not found")).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error updating payment status")).build();
        }
    }
    
    /**
     * Mettre à jour le statut d'un paiement
     * PUT /api/payments/{id}/status
     */
    @PUT
    @Path("/{id}/status")
    public Response updatePaymentStatus(@PathParam("id") String idString, UpdateStatusRequest request) {
        try {
            UUID id = UUID.fromString(idString);
            PaymentStatus status = PaymentStatus.valueOf(request.status.toUpperCase());
            Optional<Payment> updated = paymentService.updatePaymentStatus(id, status);
            
            if (updated.isPresent()) {
                return Response.ok(updated.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Payment not found")).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid status format")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error updating payment status")).build();
        }
    }
    
    /**
     * Supprimer un paiement
     * DELETE /api/payments/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deletePayment(@PathParam("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            boolean deleted = paymentService.deletePayment(id);
            
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Payment not found or cannot be deleted")).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid UUID format")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error deleting payment")).build();
        }
    }
    
    // Classes DTO pour les requêtes
    public static class CreatePaymentRequest {
        public UUID orderId;
        public BigDecimal amount;
        public PaymentMethod method;
    }
    
    public static class ConfirmPaymentRequest {
        public String transactionRef;
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
