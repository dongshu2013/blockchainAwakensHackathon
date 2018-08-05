package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import io.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/versions")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/versions")
public class VersionResource {

    public VersionResource() {
    }

    @GET
    @Timed
    public Response getSupportedVersion(@QueryParam("currentVersion") String currentVersion) {
        try {
            if (Strings.isNullOrEmpty(currentVersion) || isOldVersion(currentVersion)) {
                //426 UPGRADE REQUIRED if deployed app version is no more supported
                return Response.status(426).build();
            }
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private boolean isOldVersion(String currentVersion) {
        try {
            return false;
            //return Integer.parseInt(currentVersion.split("\\.")[0]) < 2;
        } catch (Exception ex) {
            return true; // Safety mechanism to return true and have the app running atleast if something goes wrong
        }
    }
}