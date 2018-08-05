package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.UrlOutput;
import com.walletalarm.platform.core.UrlType;
import io.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/urls")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/urls")
public class UrlResource {

    private static final String ETHPLORER_TOKEN_API = "https://api.ethplorer.io/getAddressInfo/address?apiKey=freekey";

    public UrlResource() {
    }

    @GET
    @Timed
    public Response getList() {
        try {
            List<UrlOutput> urlList = new ArrayList<>();
            urlList.add(new UrlOutput(UrlType.TOKENS, ETHPLORER_TOKEN_API));
            return Response.status(Response.Status.OK).entity(urlList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}