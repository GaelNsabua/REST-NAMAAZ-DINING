package com.namaaz.menu.rest;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/menu-items")
public class MenuResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String list() {
        return "[]"; // TODO: retourner la liste des menu items
    }
}
