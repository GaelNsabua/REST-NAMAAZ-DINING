/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

/**
 *
 * @author WAITING
 */
import business.MenuService;
import entities.MenuItem;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/menu-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MenuResource {
    
    @Inject
    private MenuService menuService;
    
    @GET
    public Response getAllMenuItems(
            @QueryParam("categoryId") String categoryId,
            @QueryParam("available") Boolean available) {
        
        List<MenuItem> menuItems;
        
        if (categoryId != null) {
            try {
                UUID categoryUuid = UUID.fromString(categoryId);
                menuItems = menuService.getMenuItemsByCategory(categoryUuid);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid category UUID format\"}").build();
            }
        } else if (available != null && available) {
            menuItems = menuService.getAvailableMenuItems();
        } else {
            menuItems = menuService.getAllMenuItems();
        }
        
        return Response.ok(menuItems).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getMenuItemById(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return menuService.getMenuItemById(uuid)
                .map(menuItem -> Response.ok(menuItem).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @POST
    public Response createMenuItem(@Valid MenuItem menuItem) {
        try {
            MenuItem created = menuService.createMenuItem(menuItem);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateMenuItem(@PathParam("id") String id, @Valid MenuItem menuItem) {
        try {
            UUID uuid = UUID.fromString(id);
            return menuService.updateMenuItem(uuid, menuItem)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteMenuItem(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            boolean deleted = menuService.deleteMenuItem(uuid);
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
