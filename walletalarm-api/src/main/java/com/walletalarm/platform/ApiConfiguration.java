package com.walletalarm.platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.spinscale.dropwizard.jobs.JobConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class ApiConfiguration extends Configuration implements JobConfiguration {
    @JsonProperty("quartz")
    public Map<String, String> quartz;

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    private String fcmToken;

    @Valid
    @NotNull
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    @Valid
    @NotNull
    private String infuraApiKey;

    @Valid
    @NotNull
    private String etherscanApiKey;

    @Valid
    @NotNull
    private long ethMaxBlockNumber;

    @Valid
    @NotNull
    private long ethLatestBlockNumber;

    @Valid
    @NotNull
    private boolean testMode;

    @JsonProperty("infura.apikey")
    public String getInfuraApiKey() {
        return infuraApiKey;
    }

    public void setInfuraApiKey(String infuraApiKey) {
        this.infuraApiKey = infuraApiKey;
    }

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swaggerBundleConfiguration;
    }

    public void setSwaggerBundleConfiguration(SwaggerBundleConfiguration swaggerBundleConfiguration) {
        this.swaggerBundleConfiguration = swaggerBundleConfiguration;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    @JsonProperty("fcmToken")
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public Map<String, String> getQuartzConfiguration() {
        return quartz;
    }

    @JsonProperty("eth.maxBlockNumber")
    public long getEthMaxBlockNumber() {
        return ethMaxBlockNumber;
    }

    public void setEthMaxBlockNumber(long ethMaxBlockNumber) {
        this.ethMaxBlockNumber = ethMaxBlockNumber;
    }

    @JsonProperty("eth.latestBlockNumber")
    public long getEthLatestBlockNumber() {
        return ethLatestBlockNumber;
    }

    public void setEthLatestBlockNumber(long ethLatestBlockNumber) {
        this.ethLatestBlockNumber = ethLatestBlockNumber;
    }

    @JsonProperty("test.mode")
    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    @JsonProperty("etherscan.apikey")
    public String getEtherscanApiKey() {
        return etherscanApiKey;
    }

    public void setEtherscanApiKey(String etherscanApiKey) {
        this.etherscanApiKey = etherscanApiKey;
    }
}
