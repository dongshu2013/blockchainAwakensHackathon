package com.walletalarm.platform;

import com.walletalarm.platform.cache.CacheManager;
import com.walletalarm.platform.cache.ContractCache;
import com.walletalarm.platform.db.dao.*;
import com.walletalarm.platform.handlers.CoinMarketCapJobHandler;
import com.walletalarm.platform.jobs.*;
import com.walletalarm.platform.resources.*;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.UUID;

public class ApiApplication extends Application<ApiConfiguration> {

    MyJobsBundle jobsBundle;
    private static final String INFURA_API_BASE_URL = "https://mainnet.infura.io/";

    public static void main(final String[] args) throws Exception {
        new ApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "walletalarm apis";
    }

    @Override
    public void initialize(final Bootstrap<ApiConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        //Swagger
        bootstrap.addBundle(new SwaggerBundle<ApiConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ApiConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });

        //Job Scheduler
        jobsBundle = new MyJobsBundle(new NotificationJob(), new BlockScanningJob(), new BlockBatchMaintainerJob(),
                new CoinMarketCapJob());
        bootstrap.addBundle(jobsBundle);
    }

    @Override
    public void run(final ApiConfiguration apiConfiguration,
                    final Environment environment) throws Exception {
        final String server = UUID.randomUUID().toString();
        final Web3j web3j = Web3j.build(new HttpService(INFURA_API_BASE_URL + apiConfiguration.getInfuraApiKey()));

        final DBIFactory dbiFactory = new DBIFactory();
        final DBI jdbi = dbiFactory.build(environment, apiConfiguration.getDataSourceFactory(), "mysql");
        final PersonDAO personDAO = jdbi.onDemand(PersonDAO.class);
        final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
        final WalletDAO walletDAO = jdbi.onDemand(WalletDAO.class);
        final PortfolioDAO portfolioDAO = jdbi.onDemand(PortfolioDAO.class);
        final NotificationDAO notificationDAO = jdbi.onDemand(NotificationDAO.class);
        final BlockBatchDAO blockBatchDAO = jdbi.onDemand(BlockBatchDAO.class);
        final ContractDAO contractDAO = jdbi.onDemand(ContractDAO.class);
        final WalletTransactionDAO walletTransactionDAO = jdbi.onDemand(WalletTransactionDAO.class);
        final TransactionDAO transactionDAO = jdbi.onDemand(TransactionDAO.class);
        final CoinMarketCapDAO coinMarketCapDAO = jdbi.onDemand(CoinMarketCapDAO.class);
        final JobDAO jobDAO = jdbi.onDemand(JobDAO.class);
        final LoginDAO loginDAO = jdbi.onDemand(LoginDAO.class);
        final FeedbackDAO feedbackDAO = jdbi.onDemand(FeedbackDAO.class);
        final SettingDAO settingDAO = jdbi.onDemand(SettingDAO.class);

        final CacheManager cacheManager = new CacheManager(contractDAO, web3j);
        ContractCache.getInstance().initialize(cacheManager);

        final PersonResource personResource = new PersonResource(personDAO);
        final UserResource userResource = new UserResource(userDAO);
        final NotificationResource notificationResource = new NotificationResource(notificationDAO);
        final JobResource jobResource = new JobResource(walletDAO, jobDAO, blockBatchDAO, notificationDAO,
                contractDAO, transactionDAO, walletTransactionDAO, coinMarketCapDAO, server, apiConfiguration, web3j);
        final LoginResource loginResource = new LoginResource(loginDAO);
        final FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO);
        final VersionResource versionResource = new VersionResource();
        final SettingResource settingResource = new SettingResource(settingDAO);
        final PortfolioResource portfolioResource = new PortfolioResource(portfolioDAO);
        final WalletResource walletResource = new WalletResource(walletDAO, walletTransactionDAO, apiConfiguration, web3j);
        final TransactionResource transactionResource = new TransactionResource(transactionDAO);
        final WalletTransactionResource walletTransactionResource = new WalletTransactionResource(walletTransactionDAO);
        final UrlResource urlResource = new UrlResource();

        environment.jersey().register(personResource);
        environment.jersey().register(userResource);
        environment.jersey().register(walletResource);
        environment.jersey().register(notificationResource);
        environment.jersey().register(jobResource);
        environment.jersey().register(loginResource);
        environment.jersey().register(feedbackResource);
        environment.jersey().register(versionResource);
        environment.jersey().register(settingResource);
        environment.jersey().register(portfolioResource);
        environment.jersey().register(transactionResource);
        environment.jersey().register(walletTransactionResource);
        environment.jersey().register(urlResource);

        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "Authorization, X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        //Get Latest Coin Market Cap Data and update the coin market cap token cache
        CoinMarketCapJobHandler.getLatestCoinMarketCapData(coinMarketCapDAO);

        // Set job context
        jobsBundle.run(apiConfiguration, environment);
        jobsBundle.getJobManager().start();
        jobsBundle.getScheduler().getContext().put("WalletDAO", walletDAO);
        jobsBundle.getScheduler().getContext().put("BlockBatchDAO", blockBatchDAO);
        jobsBundle.getScheduler().getContext().put("ContractDAO", contractDAO);
        jobsBundle.getScheduler().getContext().put("TransactionDAO", transactionDAO);
        jobsBundle.getScheduler().getContext().put("WalletTransactionDAO", walletTransactionDAO);
        jobsBundle.getScheduler().getContext().put("CoinMarketCapDAO", coinMarketCapDAO);
        jobsBundle.getScheduler().getContext().put("NotificationDAO", notificationDAO);
        jobsBundle.getScheduler().getContext().put("JobDAO", jobDAO);
        jobsBundle.getScheduler().getContext().put("ApiConfiguration", apiConfiguration);
        jobsBundle.getScheduler().getContext().put("Web3j", web3j);
        jobsBundle.getScheduler().getContext().put("Server", server);
    }
}