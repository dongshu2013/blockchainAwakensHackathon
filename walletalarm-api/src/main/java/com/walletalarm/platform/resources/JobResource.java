package com.walletalarm.platform.resources;

import com.walletalarm.platform.ApiConfiguration;
import com.walletalarm.platform.db.dao.*;
import com.walletalarm.platform.handlers.BlockBatchMaintainerJobHandler;
import com.walletalarm.platform.handlers.BlockBatchScanningJobHandler;
import com.walletalarm.platform.handlers.CoinMarketCapJobHandler;
import com.walletalarm.platform.handlers.NotificationJobHandler;
import io.swagger.annotations.Api;
import org.web3j.protocol.Web3j;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/jobs")
public class JobResource {
    private final NotificationDAO notificationDAO;
    private final WalletDAO walletDAO;
    private final ContractDAO contractDAO;
    private final BlockBatchDAO blockBatchDAO;
    private final TransactionDAO transactionDAO;
    private final WalletTransactionDAO walletTransactionDAO;
    private final CoinMarketCapDAO coinMarketCapDAO;
    private final String server;
    private final JobDAO jobDAO;
    private final ApiConfiguration apiConfiguration;
    private final Web3j web3j;

    public JobResource(WalletDAO walletDAO, JobDAO jobDAO, BlockBatchDAO blockBatchDAO, NotificationDAO notificationDAO,
                       ContractDAO contractDAO, TransactionDAO transactionDAO, WalletTransactionDAO walletTransactionDAO,
                       CoinMarketCapDAO coinMarketCapDAO, String server, ApiConfiguration apiConfiguration, Web3j web3j) {
        this.walletDAO = walletDAO;
        this.blockBatchDAO = blockBatchDAO;
        this.notificationDAO = notificationDAO;
        this.jobDAO = jobDAO;
        this.contractDAO = contractDAO;
        this.transactionDAO = transactionDAO;
        this.walletTransactionDAO = walletTransactionDAO;
        this.coinMarketCapDAO = coinMarketCapDAO;
        this.server = server;
        this.apiConfiguration = apiConfiguration;
        this.web3j = web3j;
    }

    @POST
    @Path("/notificationJob")
    public Response createNotificationJob() {
        try {
            NotificationJobHandler.doNotificationJob(blockBatchDAO, notificationDAO, transactionDAO,
                    server, apiConfiguration.getFcmToken());
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/blockBatchScanningJob")
    public Response createBlockScrappingJob() {
        try {
            BlockBatchScanningJobHandler.doBlockBatchScanningJob(blockBatchDAO, walletDAO,
                    contractDAO, transactionDAO, walletTransactionDAO, server, web3j);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/blockBatchMaintainerJob")
    public Response createBlockBatchMaintainerJob() {
        try {
            BlockBatchMaintainerJobHandler.doBlockBatchMaintenance(blockBatchDAO, transactionDAO, web3j,
                    apiConfiguration.getEthMaxBlockNumber(), apiConfiguration.getEthLatestBlockNumber(), apiConfiguration.isTestMode());
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/coinMarketCapJob")
    public Response createCoinMarketCapJob() {
        try {
            int jobId = CoinMarketCapJobHandler.doCoinMarketCapJob(jobDAO, coinMarketCapDAO);
            String output = "{\"jobId\":" + jobId + "}";
            return Response.status(Response.Status.CREATED).entity(output).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}