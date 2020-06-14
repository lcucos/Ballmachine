package com.webproject.resources;

import com.webproject.core.User;

import io.dropwizard.auth.Auth;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
public class ProtectedResource {

    @PermitAll
    @GET
    public String showSecret(@Auth User user) {
        return String.format("All good. You know the secret! %s", user.getName());
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("admin")
    public String showAdminSecret(@Auth User user) {
        return String.format("Hi there admin. %s.", user.getName());
    }
}
