package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.WalletTransaction;
import com.walletalarm.platform.db.dao.WalletTransactionDAO;
import io.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/walletTransactions")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/walletTransactions")
public class WalletTransactionResource {
    private final WalletTransactionDAO walletTransactionDAO;

    public WalletTransactionResource(WalletTransactionDAO walletTransactionDAO) {
        this.walletTransactionDAO = walletTransactionDAO;
    }

    @GET
    @Timed
    public Response getList(@QueryParam("address") String address,
                            @DefaultValue("0") @QueryParam("offset") int offset,
                            @DefaultValue("10") @QueryParam("limit") int limit) {
        try {
            List<WalletTransaction> transactionList = walletTransactionDAO.getTransactionByAddress(address, offset, limit);
            return Response.status(Response.Status.OK).entity(transactionList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}