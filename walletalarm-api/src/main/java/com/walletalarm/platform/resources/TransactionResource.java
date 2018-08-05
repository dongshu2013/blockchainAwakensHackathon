package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.Transaction;
import com.walletalarm.platform.db.dao.TransactionDAO;
import io.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/transactions")
public class TransactionResource {
    private final TransactionDAO transactionDAO;

    public TransactionResource(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    @GET
    @Timed
    @Path("/{transactionId}")
    public Response getTransactionByID(@PathParam("transactionId") int transactionId) {
        try {
            return Response.status(Response.Status.OK).entity(transactionDAO.findById(transactionId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Timed
    public Response getList(@QueryParam("address") String address,
                            @DefaultValue("0") @QueryParam("offset") int offset,
                            @DefaultValue("10") @QueryParam("limit") int limit) {
        try {
            List<Transaction> transactionList = transactionDAO.getTransactionByAddress(address, offset, limit);
            return Response.status(Response.Status.OK).entity(transactionList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response updateTransaction(Transaction transaction) {
        try {
            transactionDAO.update(transaction);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}