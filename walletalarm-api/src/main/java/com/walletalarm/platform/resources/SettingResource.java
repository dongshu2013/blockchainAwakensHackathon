package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.Setting;
import com.walletalarm.platform.db.dao.SettingDAO;
import io.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/settings")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/settings")
public class SettingResource {
    private final SettingDAO settingDAO;

    public SettingResource(SettingDAO settingDAO) {
        this.settingDAO = settingDAO;
    }

    @GET
    @Timed
    @Path("/{settingId}")
    public Response getSettingByID(@PathParam("settingId") int settingId) {
        try {
            return Response.status(Response.Status.OK).entity(settingDAO.findById(settingId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createSetting(Setting setting) {
        try {
            int settingId = settingDAO.createOrUpdate(setting);
            String output = "{\"settingId\":" + settingId + "}";
            return Response.status(Response.Status.CREATED).entity(output).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response updateSetting(Setting setting) {
        try {
            settingDAO.createOrUpdate(setting);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{settingId}")
    public Response deleteSetting(@PathParam("settingId") int settingId) {
        try {
            settingDAO.delete(settingId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Timed
    public Response getList(@QueryParam("userId") int userId) {
        try {
            List<Setting> settingList = settingDAO.findSettingsByUser(userId);
            return Response.status(Response.Status.OK).entity(settingList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}