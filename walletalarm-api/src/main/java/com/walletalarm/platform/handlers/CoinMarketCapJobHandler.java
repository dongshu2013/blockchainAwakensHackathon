package com.walletalarm.platform.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpStatusCodes;
import com.walletalarm.platform.cache.CoinMarketCapTokenCache;
import com.walletalarm.platform.core.CoinMarketCapToken;
import com.walletalarm.platform.core.Job;
import com.walletalarm.platform.core.JobStatus;
import com.walletalarm.platform.core.JobType;
import com.walletalarm.platform.db.dao.CoinMarketCapDAO;
import com.walletalarm.platform.db.dao.JobDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CoinMarketCapJobHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoinMarketCapJobHandler.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String COINMARKETCAP_API_BASE_URL = "https://api.coinmarketcap.com/v2/listings/";

    CoinMarketCapJobHandler() {
        FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static int doCoinMarketCapJob(JobDAO jobDAO, CoinMarketCapDAO coinMarketCapDAO) {
        //Create job info
        Job job = new Job();
        job.setStatus(JobStatus.STARTED);
        job.setType(JobType.NOTIFICATION);
        int jobId = jobDAO.create(job);

        LOGGER.info("Starting Notification Job. Start time - " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));

        getLatestCoinMarketCapData(coinMarketCapDAO);

        LOGGER.info("Finished Notification Job. End time - " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));

        //Update job info
        job.setJobId(jobId);
        job.setStatus(JobStatus.FINISHED);
        jobDAO.update(job);

        return jobId;
    }

    public static void getLatestCoinMarketCapData(CoinMarketCapDAO coinMarketCapDAO) {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(COINMARKETCAP_API_BASE_URL)
                    .request()
                    .get();
            if (response.getStatus() != HttpStatusCodes.STATUS_CODE_OK) {
                LOGGER.error("Error calling CoinMarketCap API - ", response.getStatus());
                return;
            }
            String jsonString = response.readEntity(String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonString);
            JsonNode data = root.get("data");

            Map<String /*symbol*/, CoinMarketCapToken> coinMarketCapTokenMap = new HashMap<>();
            List<String> symbolList = new ArrayList<>(data.size());
            List<String> nameList = new ArrayList<>(data.size());
            List<Integer> idList = new ArrayList<>(data.size());
            for (int i = 0; i < data.size(); i++) {
                try {
                    CoinMarketCapToken coinMarketCapToken = new CoinMarketCapToken();
                    JsonNode jsonNode = data.get(i);
                    int id = jsonNode.get("id").intValue();
                    String name = jsonNode.get("name").textValue().trim();
                    String symbol = jsonNode.get("symbol").textValue().trim();
                    coinMarketCapToken.setId(id);
                    coinMarketCapToken.setName(name);
                    coinMarketCapToken.setSymbol(symbol);
                    idList.add(id);
                    symbolList.add(symbol);
                    nameList.add(name);
                    coinMarketCapTokenMap.put(symbol, coinMarketCapToken);
                } catch (Exception ex) {
                    //Skip item
                }
            }

            // Assign cache a new map
            CoinMarketCapTokenCache.getInstance().assignMap(coinMarketCapTokenMap);

            // Wipe out current CoinMarketCap table and get latest
            if (idList.size() > 0) {
                coinMarketCapDAO.deleteAll();
                coinMarketCapDAO.batchInsert(idList, nameList, symbolList);
            }
        } catch (Exception ex) {
            LOGGER.error("Error scrapping CoinMarketCap. ", ex);
        }
    }
}
