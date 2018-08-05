package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.Login;
import com.walletalarm.platform.db.dao.LoginDAO;
import io.swagger.annotations.Api;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/logins")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/logins")
public class LoginResource {
    private final LoginDAO loginDAO;

    public LoginResource(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    @GET
    @Timed
    @Path("/{loginId}")
    public Response getLoginByID(@NotNull @Min(1) @PathParam("loginId") int loginId) {
        try {
            return Response.status(Response.Status.OK).entity(loginDAO.findById(loginId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createLogin(Login login) {
        try {
            String ipAddress = login.getIpAddress();
            login.setIpAddress(ipAddress.substring(0, ipAddress.lastIndexOf('.'))); //Truncate the last octet
            int loginId = loginDAO.create(login);
            String output = "{\"loginId\":" + loginId + "}";
            return Response.status(Response.Status.CREATED).entity(output).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}