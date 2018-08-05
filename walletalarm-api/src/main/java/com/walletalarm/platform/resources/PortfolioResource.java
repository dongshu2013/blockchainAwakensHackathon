package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.Portfolio;
import com.walletalarm.platform.db.dao.PortfolioDAO;
import io.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/portfolios")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/portfolios")
public class PortfolioResource {
    private final PortfolioDAO portfolioDAO;

    public PortfolioResource(PortfolioDAO portfolioDAO) {
        this.portfolioDAO = portfolioDAO;
    }

    @GET
    @Timed
    @Path("/{portfolioId}")
    public Response getPortfolioByID(@PathParam("portfolioId") int portfolioId) {
        try {
            return Response.status(Response.Status.OK).entity(portfolioDAO.findById(portfolioId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Timed
    public Response getList(@DefaultValue("0") @QueryParam("userId") int userId,
                            @DefaultValue("0") @QueryParam("offset") int offset,
                            @DefaultValue("10") @QueryParam("limit") int limit) {
        try {
            List<Portfolio> portfolioList = portfolioDAO.getList(offset, limit, userId);
            return Response.status(Response.Status.OK).entity(portfolioList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createPortfolio(Portfolio portfolio) {
        try {
            int portfolioId = portfolioDAO.create(portfolio);
            String output = "{\"portfolioId\":" + portfolioId + "}";
            return Response.status(Response.Status.CREATED).entity(output).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response updatePortfolio(Portfolio portfolio) {
        try {
            portfolioDAO.update(portfolio);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{portfolioId}")
    public Response deletePortfolio(@PathParam("portfolioId") int portfolioId) {
        try {
            portfolioDAO.delete(portfolioId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}