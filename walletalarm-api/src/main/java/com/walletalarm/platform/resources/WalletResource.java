package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletalarm.platform.ApiConfiguration;
import com.walletalarm.platform.cache.CoinMarketCapTokenCache;
import com.walletalarm.platform.core.*;
import com.walletalarm.platform.db.dao.WalletDAO;
import com.walletalarm.platform.db.dao.WalletTransactionDAO;
import com.walletalarm.platform.handlers.TransactionHandler;
import io.swagger.annotations.Api;
import org.web3j.protocol.Web3j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("/wallets")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/wallets")
public class WalletResource {
    private static final String ETH_SYMBOL = "ETH";
    private static final String ETH_NAME = "Ethereum";
    private final WalletDAO walletDAO;
    private final WalletTransactionDAO walletTransactionDAO;
    private ApiConfiguration apiConfiguration;
    private Web3j web3j;
    private static final String COINMARKETCAP_TICKER_URL = "https://api.coinmarketcap.com/v2/ticker/%d/?convert=%s";

    public WalletResource(WalletDAO walletDAO, WalletTransactionDAO walletTransactionDAO, ApiConfiguration apiConfiguration,
                          Web3j web3j) {
        this.walletDAO = walletDAO;
        this.walletTransactionDAO = walletTransactionDAO;
        this.apiConfiguration = apiConfiguration;
        this.web3j = web3j;
    }

    @GET
    @Timed
    @Path("/{walletId}")
    public Response getWalletByID(@PathParam("walletId") int walletId) {
        try {
            return Response.status(Response.Status.OK).entity(walletDAO.findById(walletId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Timed
    public Response getList(@DefaultValue("0") @QueryParam("userId") int userId,
                            @DefaultValue("0") @QueryParam("portfolioId") int portfolioId,
                            @DefaultValue("0") @QueryParam("offset") int offset,
                            @DefaultValue("10") @QueryParam("limit") int limit) {
        try {
            List<Wallet> walletList;
            if (portfolioId > 0) {
                walletList = walletDAO.getList(offset, limit, userId, portfolioId);
            } else {
                walletList = walletDAO.getList(offset, limit, userId);
            }
            return Response.status(Response.Status.OK).entity(walletList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/syncTransactions")
    public Response syncTransactions(SyncTransactionInput syncTransactionInput) {
        try {
            if (syncTransactionInput.getBlockchainType() == BlockchainType.ETH) {
                TransactionHandler.handleSyncTransactions(syncTransactionInput, walletTransactionDAO,
                        apiConfiguration.getEtherscanApiKey(), web3j);
            }
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/tokens")
    public Response getTokens(TokenPayload tokenPayload) {
        try {
            List<Token> tokenList = null;
            if (tokenPayload.getBlockchainType() == BlockchainType.ETH) {
                tokenList = parsePayload(tokenPayload);
            }
            return Response.status(Response.Status.OK).entity(tokenList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    //Sample ETH payload - https://api.ethplorer.io/getAddressInfo/0xb555a3a32c3484abc8b1944613b8b455fa43495b?apiKey=freekey
    private List<Token> parsePayload(TokenPayload tokenPayload) throws IOException {
        List<Token> tokenList = new ArrayList<>();
        //Parse ETH
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(tokenPayload.getPayload());
        JsonNode eth = root.get("ETH");
        BigDecimal ethBalance = new BigDecimal(eth.get("balance").toString());

        Token ethToken = new Token();
        CoinMarketCapToken cmcEthToken = CoinMarketCapTokenCache.getInstance().getCoinMarketCapToken(ETH_SYMBOL);
        if (cmcEthToken != null) {
            ethToken.setCmcUrl(String.format(COINMARKETCAP_TICKER_URL, cmcEthToken.getId(), tokenPayload.getBaseCurrency()));
        }
        ethToken.setBalance(ethBalance);
        ethToken.setName(ETH_NAME);
        ethToken.setSymbol(ETH_SYMBOL);
        tokenList.add(ethToken);

        JsonNode tokenArrayNode = root.get("tokens");
        for (int i = 0; i < tokenArrayNode.size(); i++) {
            try {
                JsonNode tokenNode = tokenArrayNode.get(i);
                BigDecimal balance = new BigDecimal(tokenNode.get("balance").toString());
                JsonNode tokenInfoNode = tokenNode.get("tokenInfo");
                String symbol = tokenInfoNode.get("symbol").textValue();
                String name = tokenInfoNode.get("name").textValue();
                String decimals = null;
                JsonNode decimalNode = tokenInfoNode.get("decimals"); //Mix of node types (int and string). Dude !
                if (decimalNode.isTextual()) {
                    decimals = decimalNode.textValue();
                } else if (decimalNode.isInt()) {
                    decimals = decimalNode.toString();
                }

                Token token = new Token();
                token.setName(name);
                token.setSymbol(symbol);
                token.setBalance(balance.divide(BigDecimal.TEN.pow(Integer.parseInt(decimals))));
                CoinMarketCapToken coinMarketCapToken = CoinMarketCapTokenCache.getInstance().getCoinMarketCapToken(symbol);
                if (coinMarketCapToken != null) {
                    token.setCmcUrl(String.format(COINMARKETCAP_TICKER_URL, coinMarketCapToken.getId(),
                            tokenPayload.getBaseCurrency()));
                    token.setName(coinMarketCapToken.getName());
                }
                tokenList.add(token);
            } catch (Exception ex) {
                //Skip item
            }
        }
        return tokenList;
    }

    @POST
    public Response createWallet(Wallet wallet) {
        try {
            wallet.setAddress(wallet.getAddress().toLowerCase());
            int walletId = walletDAO.create(wallet);
            String output = "{\"walletId\":" + walletId + "}";
            return Response.status(Response.Status.CREATED).entity(output).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response updateWallet(Wallet wallet) {
        try {
            walletDAO.update(wallet);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{walletId}")
    public Response deleteWallet(@PathParam("walletId") int walletId) {
        try {
            walletDAO.delete(walletId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}