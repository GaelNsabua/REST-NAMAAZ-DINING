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
import com.namaaz.service.menu.entities.Category;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
    
    @Inject
    private MenuService menuService;
    
    @GET
    public Response getAllCategories() {
        List<Category> categories = menuService.getAllCategories();
        return Response.ok(categories).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return menuService.getCategoryById(uuid)
                .map(category -> Response.ok(category).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @POST
    public Response createCategory(@Valid Category category) {
        Category created = menuService.createCategory(category);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") String id, @Valid Category category) {
        try {
            UUID uuid = UUID.fromString(id);
            return menuService.updateCategory(uuid, category)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            boolean deleted = menuService.deleteCategory(uuid);
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
