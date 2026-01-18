package com.namaaz.service.reservations.rest;

import com.namaaz.service.reservations.business.ReservationService;
import com.namaaz.service.reservations.entities.Reservation;
import com.namaaz.service.reservations.entities.ReservationStatus;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {
    
    @Inject
    private ReservationService reservationService;
    
    @GET
    public Response getAllReservations(
            @QueryParam("clientId") String clientId,
            @QueryParam("status") String status,
            @QueryParam("upcoming") Boolean upcoming) {
        
        List<Reservation> reservations;
        
        if (clientId != null) {
            try {
                UUID clientUuid = UUID.fromString(clientId);
                reservations = reservationService.getReservationsByClientId(clientUuid);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid client UUID format\"}").build();
            }
        } else if (status != null) {
            try {
                ReservationStatus resStatus = ReservationStatus.valueOf(status.toUpperCase());
                reservations = reservationService.getReservationsByStatus(resStatus);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid status. Valid values: PENDING, CONFIRMED, CANCELLED\"}").build();
            }
        } else if (upcoming != null && upcoming) {
            reservations = reservationService.getUpcomingReservations();
        } else {
            reservations = reservationService.getAllReservations();
        }
        
        return Response.ok(reservations).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getReservationById(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.getReservationById(uuid)
                .map(reservation -> Response.ok(reservation).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @POST
    public Response createReservation(@Valid Reservation reservation) {
        try {
            Reservation created = reservationService.createReservation(reservation);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateReservation(@PathParam("id") String id, @Valid Reservation reservation) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.updateReservation(uuid, reservation)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}/confirm")
    public Response confirmReservation(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.confirmReservation(uuid)
                .map(confirmed -> Response.ok(confirmed).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}/cancel")
    public Response cancelReservation(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.cancelReservation(uuid)
                .map(cancelled -> Response.ok(cancelled).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteReservation(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            boolean deleted = reservationService.deleteReservation(uuid);
            if (deleted) {
                return Response.noContent().build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
}
