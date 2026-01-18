package com.namaaz.service.reservations.rest;

import com.namaaz.service.reservations.business.ReservationService;
import com.namaaz.service.reservations.entities.RestaurantTable;
import com.namaaz.service.reservations.entities.TableStatus;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/tables")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TableResource {
    
    @Inject
    private ReservationService reservationService;
    
    @GET
    public Response getAllTables(@QueryParam("status") String status) {
        List<RestaurantTable> tables;
        
        if (status != null) {
            try {
                TableStatus tableStatus = TableStatus.valueOf(status.toUpperCase());
                tables = reservationService.getTablesByStatus(tableStatus);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid status. Valid values: FREE, RESERVED, OCCUPIED, OUT_OF_SERVICE\"}").build();
            }
        } else {
            tables = reservationService.getAllTables();
        }
        
        return Response.ok(tables).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getTableById(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.getTableById(uuid)
                .map(table -> Response.ok(table).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @GET
    @Path("/available")
    public Response getAvailableTables() {
        List<RestaurantTable> tables = reservationService.getAvailableTables();
        return Response.ok(tables).build();
    }
    
    @POST
    public Response createTable(@Valid RestaurantTable table) {
        try {
            RestaurantTable created = reservationService.createTable(table);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateTable(@PathParam("id") String id, @Valid RestaurantTable table) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.updateTable(uuid, table)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteTable(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            boolean deleted = reservationService.deleteTable(uuid);
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
