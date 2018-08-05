package com.walletalarm.platform;

import com.google.api.client.http.HttpStatusCodes;
import com.walletalarm.platform.core.*;
import com.walletalarm.platform.core.alerts.AddressActivityAlert;
import com.walletalarm.platform.core.alerts.JsonMessage;
import com.walletalarm.platform.db.dao.*;
import com.walletalarm.platform.handlers.FirebaseAlertSender;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("ApiConfiguration.local.test.yml");
    @ClassRule
    public static final DropwizardAppRule<ApiConfiguration> RULE = new DropwizardAppRule<>(
            ApiApplication.class,
            CONFIG_PATH);

    private static Client client;
    private static UserDAO userDAO;
    private static WalletDAO walletDAO;
    private static PortfolioDAO portfolioDAO;
    private static TransactionDAO transactionDAO;
    private static WalletTransactionDAO walletTransactionDAO;
    private static ContractDAO contractDAO;
    private static BlockBatchDAO blockBatchDAO;
    private static NotificationDAO notificationDAO;
    private static JobDAO jobDAO;
    private static LoginDAO loginDAO;
    private static FeedbackDAO feedbackDAO;
    private static SettingDAO settingDAO;
    private static DefaultSettingDAO defaultSettingDAO;
    private static CoinMarketCapDAO coinMarketCapDAO;

    @BeforeClass
    public static void setUp() {
        client = ClientBuilder.newClient();
        DBIFactory dbiFactory = new DBIFactory();
        DBI jdbi = dbiFactory
                .build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "mysql");
        userDAO = jdbi.onDemand(UserDAO.class);
        walletDAO = jdbi.onDemand(WalletDAO.class);
        portfolioDAO = jdbi.onDemand(PortfolioDAO.class);
        transactionDAO = jdbi.onDemand(TransactionDAO.class);
        walletTransactionDAO = jdbi.onDemand(WalletTransactionDAO.class);
        contractDAO = jdbi.onDemand(ContractDAO.class);
        blockBatchDAO = jdbi.onDemand(BlockBatchDAO.class);
        notificationDAO = jdbi.onDemand(NotificationDAO.class);
        jobDAO = jdbi.onDemand(JobDAO.class);
        loginDAO = jdbi.onDemand(LoginDAO.class);
        feedbackDAO = jdbi.onDemand(FeedbackDAO.class);
        settingDAO = jdbi.onDemand(SettingDAO.class);
        defaultSettingDAO = jdbi.onDemand(DefaultSettingDAO.class);
        coinMarketCapDAO = jdbi.onDemand(CoinMarketCapDAO.class);

//        //Initialize BlockBatch
//        BlockBatch blockBatch = createBlockBatch(6026763, 6026763, BlockBatchStatus.CLOSED,
//                BlockBatchStatus.CLOSED, "xyz", "xyz", Timestamp.valueOf("2017-09-11 00:00:00.0"),
//                Timestamp.valueOf("2017-09-11 01:00:00.0"), null, Timestamp.valueOf("2017-09-11 00:00:00.0"),
//                Timestamp.valueOf("2017-09-11 00:00:00.0"), null, 100);
//        assertThat(blockBatch).isNotNull();
//        BLOCK_BATCH_ID = blockBatch.getBlockBatchId();
//        assertThat(BLOCK_BATCH_ID).isGreaterThan(0);
    }

    @AfterClass
    public static void tearDown() {
        client.close();
    }

    private static Wallet createWallet(int userId, int portfolioId, String address, String name,
                                       BlockchainType blockchainType) {
        final Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setPortfolioId(portfolioId);
        wallet.setAddress(address);
        wallet.setName(name);
        wallet.setBlockchainType(blockchainType);
        Wallet storedWallet = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets")
                .request()
                .post(Entity.entity(wallet, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Wallet.class);
        int walletId = storedWallet.getWalletId();
        assertThat(walletId).isGreaterThan(0);

        Wallet getWallet = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets/" + walletId)
                .request()
                .get(Wallet.class);
        assertThat(getWallet.getWalletId()).isEqualTo(walletId);
        return getWallet;
    }

    private static Portfolio createPortfolio(int userId, String name) {
        final Portfolio portfolio = new Portfolio();
        portfolio.setUserId(userId);
        portfolio.setName(name);
        Portfolio storedPortfolio = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios")
                .request()
                .post(Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Portfolio.class);
        int portfolioId = storedPortfolio.getPortfolioId();
        assertThat(portfolioId).isGreaterThan(0);

        Portfolio getPortfolio = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios/" + portfolioId)
                .request()
                .get(Portfolio.class);
        assertThat(getPortfolio.getPortfolioId()).isEqualTo(portfolioId);
        return getPortfolio;
    }

    private static Transaction createTransaction(String hash, long blockBatchId, BlockchainType blockchainType,
                                                 String matchingAddress, String from, String to, TransactionStatus status,
                                                 Timestamp time, BigDecimal fee, BigDecimal value, int contractId) {
        final Transaction transaction = new Transaction();
        transaction.setTo(to);
        transaction.setFrom(from);
        transaction.setHash(hash);
        transaction.setBlockBatchId(blockBatchId);
        transaction.setMatchingAddress(matchingAddress);
        transaction.setFee(fee);
        transaction.setStatus(status);
        transaction.setTime(time);
        transaction.setBlockchainType(blockchainType);
        transaction.setValue(value);
        transaction.setContractId(contractId);

        int transactionId = transactionDAO.create(transaction);
        assertThat(transactionId).isGreaterThan(0);

        Transaction getTransaction = client.target("http://localhost:" + RULE.getLocalPort() + "/transactions/" + transactionId)
                .request()
                .get(Transaction.class);
        assertThat(getTransaction.getTransactionId()).isEqualTo(transactionId);
        return getTransaction;
    }

    private static BlockBatch createBlockBatch(long startBlockNumber, long endBlockNumber, BlockBatchStatus scanStatus,
                                               BlockBatchStatus notifyStatus, String scanServer, String notifyServer,
                                               Timestamp scanStartTime, Timestamp scanEndTime, Timestamp scanResetTime,
                                               Timestamp notifyStartTime, Timestamp notifyEndTime, Timestamp notifyResetTime,
                                               int totalTransactions) {
        final BlockBatch blockBatch = new BlockBatch();
        blockBatch.setStartBlockNumber(startBlockNumber);
        blockBatch.setEndBlockNumber(endBlockNumber);
        blockBatch.setScanServer(scanServer);
        blockBatch.setNotifyServer(notifyServer);
        blockBatch.setScanStatus(scanStatus);
        blockBatch.setNotifyStatus(notifyStatus);
        blockBatch.setScanStartTime(scanStartTime);
        blockBatch.setScanEndTime(scanEndTime);
        blockBatch.setScanResetTime(scanResetTime);
        blockBatch.setNotifyStartTime(notifyStartTime);
        blockBatch.setNotifyEndTime(notifyEndTime);
        blockBatch.setNotifyResetTime(notifyResetTime);
        blockBatch.setTotalTransactions(totalTransactions);
        long blockBatchId = blockBatchDAO.create(blockBatch);
        assertThat(blockBatchId).isGreaterThan(0);
        BlockBatch getBlockBatch = blockBatchDAO.findById(blockBatchId);
        assertThat(getBlockBatch.getBlockBatchId()).isEqualTo(blockBatchId);
        return getBlockBatch;
    }

    private static Contract createContract(String address, String name, String symbol, int decimals, BlockchainType blockchainType) {
        final Contract contract = new Contract();
        contract.setAddress(address);
        contract.setName(name);
        contract.setSymbol(symbol);
        contract.setDecimals(decimals);
        contract.setBlockchainType(blockchainType);
        int contractId = contractDAO.create(contract);
        assertThat(contractId).isGreaterThan(0);
        Contract getContract = contractDAO.findById(contractId);
        assertThat(getContract.getContractId()).isEqualTo(contractId);
        return getContract;
    }

    private static Setting createSetting(int defaultSettingId, int userId, boolean value) {
        Setting setting = new Setting();
        setting.setDefaultSettingId(defaultSettingId);
        setting.setUserId(userId);
        setting.setValue(value);
        Setting storedSetting = client.target("http://localhost:" + RULE.getLocalPort() + "/settings")
                .request()
                .post(Entity.entity(setting, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Setting.class);
        int settingId = storedSetting.getSettingId();
        assertThat(settingId).isGreaterThan(0);

        Setting getSetting = client.target("http://localhost:" + RULE.getLocalPort() + "/settings/" + settingId)
                .request()
                .get(Setting.class);
        assertThat(getSetting.getSettingId()).isEqualTo(settingId);
        return getSetting;
    }

    private static User createUser(String deviceId, String timezone, String notificationCode, OS os, String version) {
        final User user = new User();
        user.setDeviceId(deviceId);
        user.setTimezone(timezone);
        user.setNotificationCode(notificationCode);
        user.setOs(os);
        user.setVersion(version);
        User storedUser = client.target("http://localhost:" + RULE.getLocalPort() + "/users")
                .request()
                .post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(User.class);
        int userId = storedUser.getUserId();
        assertThat(userId).isGreaterThan(0);

        User getUser = client.target("http://localhost:" + RULE.getLocalPort() + "/users/" + userId)
                .request()
                .get(User.class);
        assertThat(getUser.getUserId()).isEqualTo(userId);
        return getUser;
    }

    private static Login createLogin(int userId, String ipAddress) {
        final Login login = new Login();
        login.setUserId(userId);
        login.setIpAddress(ipAddress);
        Login storedLogin = client.target("http://localhost:" + RULE.getLocalPort() + "/logins")
                .request()
                .post(Entity.entity(login, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Login.class);
        int loginId = storedLogin.getLoginId();
        assertThat(loginId).isGreaterThan(0);

        Login getLogin = client.target("http://localhost:" + RULE.getLocalPort() + "/logins/" + loginId)
                .request()
                .get(Login.class);
        assertThat(getLogin.getLoginId()).isEqualTo(loginId);
        return getLogin;
    }

    private static Notification createNotification(int userId, String message, String data, NotificationType type) {
        final Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setData(data);
        notification.setMessage(message);
        notification.setNotificationType(type);
        Notification storedNotification = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications")
                .request()
                .post(Entity.entity(notification, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Notification.class);
        int notificationId = storedNotification.getNotificationId();
        assertThat(notificationId).isGreaterThan(0);

        Notification getNotification = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/" + notificationId)
                .request()
                .get(Notification.class);
        assertThat(getNotification.getNotificationId()).isEqualTo(notificationId);
        return getNotification;
    }

    private static Feedback createFeedback(int userId, String message) {
        final Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setMessage(message);
        Feedback storedFeedback = client.target("http://localhost:" + RULE.getLocalPort() + "/feedbacks")
                .request()
                .post(Entity.entity(feedback, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Feedback.class);
        int feedbackId = storedFeedback.getFeedbackId();
        assertThat(feedbackId).isGreaterThan(0);

        Feedback getFeedback = client.target("http://localhost:" + RULE.getLocalPort() + "/feedbacks/" + feedbackId)
                .request()
                .get(Feedback.class);
        assertThat(getFeedback.getFeedbackId()).isEqualTo(feedbackId);
        return getFeedback;
    }

    private static int createSampleUser() {
        String deviceId = "abcd-efgh";
        String timezone = "UTC+10:30";
        String notificationCode = "pqr";
        OS os = OS.ANDROID;
        String version = "0.1";
        User user = createUser(deviceId, timezone, notificationCode, os, version);
        return user.getUserId();
    }

    @Test
    public void testVersions() {
        //GET
        Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/versions?currentVersion=2.0.15")
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void testLogins() {
        int loginId = 0;
        int userId = 0;
        try {
            userId = createSampleUser();

            String ipAddress = "24.23.221.102";
            Login login = createLogin(userId, ipAddress);
            loginId = login.getLoginId();

            //GET
            Login getLogin = client.target("http://localhost:" + RULE.getLocalPort() + "/logins/" + loginId)
                    .request()
                    .get(Login.class);
            assertThat(getLogin.getLoginId()).isEqualTo(loginId);
            assertThat(getLogin.getIpAddress()).isEqualTo("24.23.221"); //Truncated ip
            assertThat(getLogin.getLoginTime()).isNotNull();
        } finally {
            //Hard delete
            loginDAO.deleteHard(loginId);
            userDAO.deleteHard(userId);
        }
    }

    @Test
    public void testUsers() {
        int userId = 0;
        int newUserId = 0;
        try {
            String deviceId = "abcd-efgh";
            String timezone = "UTC+10:30";
            String notificationCode = "xyz";
            OS os = OS.ANDROID;
            String version = "0.1";
            User user = createUser(deviceId, timezone, notificationCode, os, version);
            userId = user.getUserId();

            //GET
            User getUser = client.target("http://localhost:" + RULE.getLocalPort() + "/users/" + userId)
                    .request()
                    .get(User.class);
            assertThat(getUser.getUserId()).isEqualTo(userId);
            assertThat(getUser.getDeviceId()).isEqualTo(deviceId);
            assertThat(getUser.getTimezone()).isEqualTo(timezone);
            assertThat(getUser.getNotificationCode()).isEqualTo(notificationCode);
            assertThat(getUser.getOs()).isEqualTo(os);
            assertThat(getUser.getVersion()).isEqualTo(version);
            assertThat(getUser.getIsActive()).isTrue();

            //GET by device id
            User getUserByDeviceId = client.target("http://localhost:" + RULE.getLocalPort() + "/users?deviceId=" + deviceId)
                    .request()
                    .get(User.class);
            assertThat(getUserByDeviceId.getUserId()).isEqualTo(userId);
            assertThat(getUserByDeviceId.getDeviceId()).isEqualTo(deviceId);
            assertThat(getUserByDeviceId.getNotificationCode()).isEqualTo(notificationCode);
            assertThat(getUserByDeviceId.getTimezone()).isEqualTo(timezone);
            assertThat(getUserByDeviceId.getOs()).isEqualTo(os);
            assertThat(getUserByDeviceId.getVersion()).isEqualTo(version);
            assertThat(getUserByDeviceId.getIsActive()).isTrue();

            //GET by notificationCode
            User getUserByNotificationCode = client.target("http://localhost:" + RULE.getLocalPort() + "/users/notificationCode/" + notificationCode)
                    .request()
                    .get(User.class);
            assertThat(getUserByNotificationCode.getUserId()).isEqualTo(userId);
            assertThat(getUserByNotificationCode.getDeviceId()).isEqualTo(deviceId);
            assertThat(getUserByNotificationCode.getNotificationCode()).isEqualTo(notificationCode);
            assertThat(getUserByNotificationCode.getTimezone()).isEqualTo(timezone);
            assertThat(getUserByNotificationCode.getOs()).isEqualTo(os);
            assertThat(getUserByNotificationCode.getVersion()).isEqualTo(version);
            assertThat(getUserByNotificationCode.getIsActive()).isTrue();

            //PUT
            deviceId = "pqrs-tuvw";
            timezone = "UTC-05:00";
            notificationCode = "cAgdza0gvdQ:APA91bHav_PgQg_cUOF14zPdSkcU-SouQiUY2PBPmqSyi-cq8teoxBR-NaL" +
                    "TjbQUYRpwL3UR1EKevJkx6Bee5Fv2NF2JkbicxA4NZ1SDo567zalHgGCsc-QQmd1g9-RVEkpVUb6sjP15";
            os = OS.IOS;
            version = "0.2";
            getUser.setOs(os);
            getUser.setVersion(version);
            getUser.setDeviceId(deviceId);
            getUser.setTimezone(timezone);
            getUser.setNotificationCode(notificationCode);

            Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/users")
                    .request()
                    .put(Entity.entity(getUser, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatus()).isEqualTo(204);

            //GET
            getUser = client.target("http://localhost:" + RULE.getLocalPort() + "/users/" + userId)
                    .request()
                    .get(User.class);
            assertThat(getUser.getUserId()).isEqualTo(userId);
            assertThat(getUser.getDeviceId()).isEqualTo(deviceId);
            assertThat(getUser.getTimezone()).isEqualTo(timezone);
            assertThat(getUser.getNotificationCode()).isEqualTo(notificationCode);
            assertThat(getUser.getOs()).isEqualTo(os);
            assertThat(getUser.getVersion()).isEqualTo(version);
            assertThat(getUser.getIsActive()).isTrue();

            //Replace device
            String newDeviceId = "newDeviceId";
            timezone = "UTC+10:30";
            notificationCode = "xyz";
            os = OS.ANDROID;
            version = "0.1";
            User newUser = createUser(newDeviceId, timezone, notificationCode, os, version);
            newUserId = newUser.getUserId();

            ReplaceDevice replaceDevice = new ReplaceDevice(getUser.getDeviceId(), newDeviceId);
            response = client.target("http://localhost:" + RULE.getLocalPort() + "/users/replaceDevice")
                    .request()
                    .put(Entity.entity(replaceDevice, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatus()).isEqualTo(204);

            //GET
            User getNewUser = client.target("http://localhost:" + RULE.getLocalPort() + "/users/" + newUserId)
                    .request()
                    .get(User.class);
            assertThat(getNewUser).isNull();

            User getOldUser = client.target("http://localhost:" + RULE.getLocalPort() + "/users/" + getUser.getUserId())
                    .request()
                    .get(User.class);
            assertThat(getOldUser).isNotNull();
            assertThat(getOldUser.getDeviceId()).isEqualTo(newDeviceId);

            //DELETE
            Response response1 = client.target("http://localhost:" + RULE.getLocalPort() + "/users/" + userId)
                    .request()
                    .delete();
            assertThat(response1.getStatus()).isEqualTo(204);

            //GET
            getUser = client.target("http://localhost:" + RULE.getLocalPort() + "/users/" + userId)
                    .request()
                    .get(User.class);
            assertThat(getUser).isNull();
        } finally {
            //Hard delete
            userDAO.deleteHard(userId);
            userDAO.deleteHard(newUserId);
        }
    }

    @Test
    public void testFeedbacks() {
        int feedbackId = 0;
        int userId = 0;
        try {
            userId = createSampleUser();

            String message = "Add icon blockchain";
            Feedback feedback = createFeedback(userId, message);
            feedbackId = feedback.getFeedbackId();

            //GET
            Feedback getFeedback = client.target("http://localhost:" + RULE.getLocalPort() + "/feedbacks/" + feedbackId)
                    .request()
                    .get(Feedback.class);
            assertThat(getFeedback.getFeedbackId()).isEqualTo(feedbackId);
            assertThat(getFeedback.getMessage()).isEqualTo(message);
            assertThat(getFeedback.getComment()).isNull();
            assertThat(getFeedback.getFeedbackStatus()).isEqualTo(FeedbackStatus.RECEIVED);
            assertThat(getFeedback.isActive()).isTrue();

            //PUT
            String comment = "We have added this feature";
            getFeedback.setFeedbackStatus(FeedbackStatus.FIXED);
            getFeedback.setComment(comment);

            Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/feedbacks")
                    .request()
                    .put(Entity.entity(getFeedback, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatus()).isEqualTo(204);

            //GET
            getFeedback = client.target("http://localhost:" + RULE.getLocalPort() + "/feedbacks/" + feedbackId)
                    .request()
                    .get(Feedback.class);
            assertThat(getFeedback.getFeedbackId()).isEqualTo(feedbackId);
            assertThat(getFeedback.getMessage()).isEqualTo(message);
            assertThat(getFeedback.getComment()).isEqualTo(comment);
            assertThat(getFeedback.getFeedbackStatus()).isEqualTo(FeedbackStatus.FIXED);
            assertThat(getFeedback.isActive()).isTrue();
        } finally {
            //Hard delete
            feedbackDAO.deleteHard(feedbackId);
            userDAO.deleteHard(userId);
        }
    }

    @Test
    public void testSettings() {
        int defaultSettingId1 = 0;
        int settingId1 = 0;
        int userId = 0;
        try {
            //Create default settings
            DefaultSetting defaultSetting = new DefaultSetting();
            defaultSetting.setType(SettingType.NOTIFICATION_SOUND);
            defaultSetting.setCategory(SettingCategory.General);
            defaultSetting.setText("Notification Sound");
            defaultSetting.setSortOrder(1);
            defaultSetting.setValue(true);
            defaultSettingId1 = defaultSettingDAO.create(defaultSetting);
            assertThat(defaultSettingId1).isGreaterThan(0);
            DefaultSetting defaultSetting1 = defaultSettingDAO.findById(defaultSettingId1);

            userId = createSampleUser();

            //CREATE
            boolean value = false;
            Setting setting = createSetting(defaultSettingId1, userId, value);
            settingId1 = setting.getSettingId();

            //GET
            Setting getSetting = client.target("http://localhost:" + RULE.getLocalPort() + "/settings/" + settingId1)
                    .request()
                    .get(Setting.class);
            assertThat(getSetting.getSettingId()).isEqualTo(settingId1);
            assertThat(getSetting.getText()).isEqualTo(defaultSetting1.getText());
            assertThat(getSetting.getType()).isEqualTo(defaultSetting1.getType());
            assertThat(getSetting.getCategory()).isEqualTo(defaultSetting1.getCategory());
            assertThat(getSetting.getValue()).isEqualTo(value);
            assertThat(getSetting.getDefaultSettingId()).isEqualTo(defaultSettingId1);
            assertThat(getSetting.getUserId()).isEqualTo(userId);

            //PUT
            value = true;
            getSetting.setValue(value);
            Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/settings")
                    .request()
                    .put(Entity.entity(getSetting, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatus()).isEqualTo(204);

            //GET
            getSetting = client.target("http://localhost:" + RULE.getLocalPort() + "/settings/" + settingId1)
                    .request()
                    .get(Setting.class);
            assertThat(getSetting.getSettingId()).isEqualTo(settingId1);
            assertThat(getSetting.getText()).isEqualTo(defaultSetting1.getText());
            assertThat(getSetting.getType()).isEqualTo(defaultSetting1.getType());
            assertThat(getSetting.getCategory()).isEqualTo(defaultSetting1.getCategory());
            assertThat(getSetting.getValue()).isEqualTo(value);
            assertThat(getSetting.getDefaultSettingId()).isEqualTo(defaultSettingId1);
            assertThat(getSetting.getUserId()).isEqualTo(userId);

            List<Setting> settingList = client.target("http://localhost:" + RULE.getLocalPort() + "/settings?" +
                    "userId=" + userId)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<Setting>>() {
                    });
            assertThat(settingList).isNotNull();
            assertThat(settingList.size()).isEqualTo(1);
            assertThat(settingList.get(0).getValue()).isTrue();

            //DELETE
            Response response1 = client.target("http://localhost:" + RULE.getLocalPort() + "/settings/" + settingId1)
                    .request()
                    .delete();
            assertThat(response1.getStatus()).isEqualTo(204);

            //GET
            getSetting = client.target("http://localhost:" + RULE.getLocalPort() + "/settings/" + settingId1)
                    .request()
                    .get(Setting.class);
            assertThat(getSetting).isNull();
        } finally {
            //Hard delete
            settingDAO.deleteHard(settingId1);
            defaultSettingDAO.deleteHard(defaultSettingId1);
            userDAO.deleteHard(userId);
        }
    }

    @Test
    public void testPortfolios() {
        int userId = 0;
        List<Integer> deletePortfolioIdList = new ArrayList<>();
        try {
            userId = createSampleUser();

            String name1 = "portfolio1";
            Portfolio portfolio = createPortfolio(userId, name1);
            int portfolioId = portfolio.getPortfolioId();
            deletePortfolioIdList.add(portfolioId);

            //GET
            Portfolio getPortfolio = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios/" + portfolioId)
                    .request()
                    .get(Portfolio.class);
            assertThat(getPortfolio.getPortfolioId()).isEqualTo(portfolioId);
            assertThat(getPortfolio.getUserId()).isEqualTo(userId);
            assertThat(getPortfolio.getName()).isEqualTo(name1);
            assertThat(getPortfolio.getIsActive()).isTrue();

            //GET LIST
            int offset = 0;
            int limit = 10;
            List<Portfolio> portfolioList = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios?" +
                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<Portfolio>>() {
                    });
            assertThat(portfolioList).isNotNull();
            assertThat(portfolioList.size()).isEqualTo(1);
            assertThat(portfolioList.get(0)).isNotNull();
            assertThat(portfolioList.get(0).getName()).isEqualTo(name1);
            assertThat(portfolioList.get(0).getUserId()).isEqualTo(userId);
            assertThat(portfolioList.get(0).getPortfolioId()).isEqualTo(portfolioId);
            assertThat(portfolioList.get(0).getIsActive()).isEqualTo(true);

            //PUT
            name1 = "updatedPortfolio1";
            getPortfolio.setName(name1);
            Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios")
                    .request()
                    .put(Entity.entity(getPortfolio, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatus()).isEqualTo(204);

            //GET
            getPortfolio = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios/" + portfolioId)
                    .request()
                    .get(Portfolio.class);
            assertThat(getPortfolio.getPortfolioId()).isEqualTo(portfolioId);
            assertThat(getPortfolio.getUserId()).isEqualTo(userId);
            assertThat(getPortfolio.getName()).isEqualTo(name1);
            assertThat(getPortfolio.getIsActive()).isTrue();

            //GET LIST
            portfolioList = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios?" +
                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<Portfolio>>() {
                    });
            assertThat(portfolioList).isNotNull();
            assertThat(portfolioList.size()).isEqualTo(1);
            assertThat(portfolioList.get(0)).isNotNull();
            assertThat(portfolioList.get(0).getName()).isEqualTo(name1);
            assertThat(portfolioList.get(0).getUserId()).isEqualTo(userId);
            assertThat(portfolioList.get(0).getPortfolioId()).isEqualTo(portfolioId);
            assertThat(portfolioList.get(0).getIsActive()).isEqualTo(true);

            String name2 = "portfolio2";
            Portfolio portfolio2 = createPortfolio(userId, name2);
            int portfolioId2 = portfolio2.getPortfolioId();
            deletePortfolioIdList.add(portfolioId2);

            //GET
            getPortfolio = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios/" + portfolioId2)
                    .request()
                    .get(Portfolio.class);
            assertThat(getPortfolio.getPortfolioId()).isEqualTo(portfolioId2);
            assertThat(getPortfolio.getUserId()).isEqualTo(userId);
            assertThat(getPortfolio.getName()).isEqualTo(name2);
            assertThat(getPortfolio.getIsActive()).isTrue();

            //GET LIST
            portfolioList = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios?" +
                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<Portfolio>>() {
                    });
            assertThat(portfolioList).isNotNull();
            assertThat(portfolioList.size()).isEqualTo(2);
            assertThat(portfolioList.get(0)).isNotNull();
            assertThat(portfolioList.get(0).getName()).isEqualTo(name2);
            assertThat(portfolioList.get(0).getUserId()).isEqualTo(userId);
            assertThat(portfolioList.get(0).getPortfolioId()).isEqualTo(portfolioId2);
            assertThat(portfolioList.get(0).getIsActive()).isEqualTo(true);
            assertThat(portfolioList.get(1)).isNotNull();
            assertThat(portfolioList.get(1).getName()).isEqualTo(name1);
            assertThat(portfolioList.get(1).getUserId()).isEqualTo(userId);
            assertThat(portfolioList.get(1).getPortfolioId()).isEqualTo(portfolioId);
            assertThat(portfolioList.get(1).getIsActive()).isEqualTo(true);

            //DELETE
            Response response1 = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios/" + portfolioId)
                    .request()
                    .delete();
            assertThat(response1.getStatus()).isEqualTo(204);

            //GET
            getPortfolio = client.target("http://localhost:" + RULE.getLocalPort() + "/portfolios/" + portfolioId)
                    .request()
                    .get(Portfolio.class);
            assertThat(getPortfolio).isNull();
        } finally {
            //Hard delete
            for (int deletePortfolioId : deletePortfolioIdList) {
                portfolioDAO.deleteHard(deletePortfolioId);
            }
            userDAO.deleteHard(userId);
        }
    }

    @Test
    public void testWallets() {
        int userId = 0;
        int portfolioId = 0;
        List<Integer> deleteWalletIdList = new ArrayList<>();
        try {
            userId = createSampleUser();

            Portfolio portfolio = createPortfolio(userId, "portfolio1");
            portfolioId = portfolio.getPortfolioId();

            String name1 = "bwallet1";
            String address1 = "0x873375ac5181D80404A330c97f08704273b3b865".toLowerCase();
            Wallet wallet = createWallet(userId, portfolioId, address1, name1, BlockchainType.ETH);
            int walletId = wallet.getWalletId();
            deleteWalletIdList.add(walletId);

            //GET
            Wallet getWallet = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets/" + walletId)
                    .request()
                    .get(Wallet.class);
            assertThat(getWallet.getWalletId()).isEqualTo(walletId);
            assertThat(getWallet.getUserId()).isEqualTo(userId);
            assertThat(getWallet.getName()).isEqualTo(name1);
            assertThat(getWallet.getAddress()).isEqualTo(address1);
            assertThat(getWallet.getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(getWallet.getPortfolioId()).isEqualTo(portfolioId);
            assertThat(getWallet.getIsActive()).isTrue();

            //GET LIST
            int offset = 0;
            int limit = 10;
            List<Wallet> walletList = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets?" +
                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<Wallet>>() {
                    });
            assertThat(walletList).isNotNull();
            assertThat(walletList.size()).isEqualTo(1);
            assertThat(walletList.get(0)).isNotNull();
            assertThat(walletList.get(0).getWalletId()).isEqualTo(walletId);
            assertThat(walletList.get(0).getUserId()).isEqualTo(userId);
            assertThat(walletList.get(0).getName()).isEqualTo(name1);
            assertThat(walletList.get(0).getAddress()).isEqualTo(address1);
            assertThat(walletList.get(0).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(walletList.get(0).getPortfolioId()).isEqualTo(portfolioId);
            assertThat(walletList.get(0).getIsActive()).isEqualTo(true);

            //PUT
            name1 = "updatedWallet1";
            getWallet.setName(name1);
            Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets")
                    .request()
                    .put(Entity.entity(getWallet, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatus()).isEqualTo(204);

            //GET
            getWallet = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets/" + walletId)
                    .request()
                    .get(Wallet.class);
            assertThat(getWallet.getWalletId()).isEqualTo(walletId);
            assertThat(getWallet.getUserId()).isEqualTo(userId);
            assertThat(getWallet.getName()).isEqualTo(name1);
            assertThat(getWallet.getAddress()).isEqualTo(address1);
            assertThat(getWallet.getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(getWallet.getPortfolioId()).isEqualTo(portfolioId);
            assertThat(getWallet.getIsActive()).isTrue();

            //GET LIST
            walletList = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets?" +
                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<Wallet>>() {
                    });
            assertThat(walletList).isNotNull();
            assertThat(walletList.size()).isEqualTo(1);
            assertThat(walletList.get(0)).isNotNull();
            assertThat(walletList.get(0).getWalletId()).isEqualTo(walletId);
            assertThat(walletList.get(0).getUserId()).isEqualTo(userId);
            assertThat(walletList.get(0).getName()).isEqualTo(name1);
            assertThat(walletList.get(0).getAddress()).isEqualTo(address1);
            assertThat(walletList.get(0).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(walletList.get(0).getPortfolioId()).isEqualTo(portfolioId);
            assertThat(walletList.get(0).getIsActive()).isEqualTo(true);

            String name2 = "awallet2";
            String address2 = "0x6A13cbB3A3ecD7e8D74636F79c4A09AcB1f85606".toLowerCase();
            Wallet wallet2 = createWallet(userId, portfolioId, address2, name2, BlockchainType.ETH);
            int walletId2 = wallet2.getWalletId();
            deleteWalletIdList.add(walletId2);

            //GET
            getWallet = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets/" + walletId2)
                    .request()
                    .get(Wallet.class);
            assertThat(getWallet.getWalletId()).isEqualTo(walletId2);
            assertThat(getWallet.getUserId()).isEqualTo(userId);
            assertThat(getWallet.getName()).isEqualTo(name2);
            assertThat(getWallet.getAddress()).isEqualTo(address2);
            assertThat(getWallet.getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(getWallet.getPortfolioId()).isEqualTo(portfolioId);
            assertThat(getWallet.getIsActive()).isTrue();

            //GET LIST
            walletList = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets?" +
                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<Wallet>>() {
                    });
            assertThat(walletList).isNotNull();
            assertThat(walletList.size()).isEqualTo(2);
            assertThat(walletList.get(0)).isNotNull();
            assertThat(walletList.get(0).getWalletId()).isEqualTo(walletId2);
            assertThat(walletList.get(0).getUserId()).isEqualTo(userId);
            assertThat(walletList.get(0).getName()).isEqualTo(name2);
            assertThat(walletList.get(0).getAddress()).isEqualTo(address2);
            assertThat(walletList.get(0).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(walletList.get(0).getPortfolioId()).isEqualTo(portfolioId);
            assertThat(walletList.get(0).getIsActive()).isEqualTo(true);
            assertThat(walletList.get(1)).isNotNull();
            assertThat(walletList.get(1).getWalletId()).isEqualTo(walletId);
            assertThat(walletList.get(1).getUserId()).isEqualTo(userId);
            assertThat(walletList.get(1).getName()).isEqualTo(name1);
            assertThat(walletList.get(1).getAddress()).isEqualTo(address1);
            assertThat(walletList.get(1).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(walletList.get(1).getPortfolioId()).isEqualTo(portfolioId);
            assertThat(walletList.get(1).getIsActive()).isEqualTo(true);

            //GET LIST BY PORTFOLIO
            walletList = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets?" +
                    "userId=" + userId + "&portfolioId=" + portfolioId + "&offset=" + offset + "&limit=" + limit)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<Wallet>>() {
                    });
            assertThat(walletList).isNotNull();
            assertThat(walletList.size()).isEqualTo(2);
            assertThat(walletList.get(0)).isNotNull();
            assertThat(walletList.get(0).getWalletId()).isEqualTo(walletId2);
            assertThat(walletList.get(0).getUserId()).isEqualTo(userId);
            assertThat(walletList.get(0).getName()).isEqualTo(name2);
            assertThat(walletList.get(0).getAddress()).isEqualTo(address2);
            assertThat(walletList.get(0).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(walletList.get(0).getPortfolioId()).isEqualTo(portfolioId);
            assertThat(walletList.get(0).getIsActive()).isEqualTo(true);
            assertThat(walletList.get(1)).isNotNull();
            assertThat(walletList.get(1).getWalletId()).isEqualTo(walletId);
            assertThat(walletList.get(1).getUserId()).isEqualTo(userId);
            assertThat(walletList.get(1).getName()).isEqualTo(name1);
            assertThat(walletList.get(1).getAddress()).isEqualTo(address1);
            assertThat(walletList.get(1).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(walletList.get(1).getPortfolioId()).isEqualTo(portfolioId);
            assertThat(walletList.get(1).getIsActive()).isEqualTo(true);

            //DELETE
            Response response1 = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets/" + walletId)
                    .request()
                    .delete();
            assertThat(response1.getStatus()).isEqualTo(204);

            //GET
            getWallet = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets/" + walletId)
                    .request()
                    .get(Wallet.class);
            assertThat(getWallet).isNull();
        } finally {
            //Hard delete
            for (int deleteWalletId : deleteWalletIdList) {
                walletDAO.deleteHard(deleteWalletId);
            }
            portfolioDAO.deleteHard(portfolioId);
            userDAO.deleteHard(userId);
        }
    }

    @Test
    public void testTransactions() {
        long blockBatchId = 0;
        int contractId = 0;
        List<Integer> deleteTransactionIdList = new ArrayList<>();
        try {
            String to = "0x6A13cbB3A3ecD7e8D74636F79c4A09AcB1f85606";
            String from = "0x0bfd35af0c7ec9db3265aa77ef938e6fd5a58cb5";
            String hash = "0x138fce42cc81f4e13148533e1fc0b6189ce9c5659cba0175de93ca716eeb3bc1";
            BigDecimal fee = new BigDecimal(12);
            BigDecimal value = new BigDecimal(1002);
            Timestamp time = Timestamp.valueOf("2017-09-11 00:00:00.0");
            contractId = createSampleContract();
            blockBatchId = createSampleBlockBatch();

            Transaction transaction = createTransaction(hash, blockBatchId, BlockchainType.ETH, to, from, to,
                    TransactionStatus.SUCCESS, time, fee, value, contractId);
            int transactionId = transaction.getTransactionId();
            deleteTransactionIdList.add(transactionId);

            //GET
            Transaction getTransaction = client.target("http://localhost:" + RULE.getLocalPort() + "/transactions/" + transactionId)
                    .request()
                    .get(Transaction.class);
            assertThat(getTransaction.getTransactionId()).isEqualTo(transactionId);
            assertThat(getTransaction.getBlockBatchId()).isEqualTo(blockBatchId);
            assertThat(getTransaction.getContractId()).isEqualTo(contractId);
            assertThat(getTransaction.getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(getTransaction.getFee().stripTrailingZeros()).isEqualTo(fee);
            assertThat(getTransaction.getTo()).isEqualTo(to);
            assertThat(getTransaction.getFrom()).isEqualTo(from);
            assertThat(getTransaction.getMatchingAddress()).isEqualTo(to);
            assertThat(getTransaction.getValue().stripTrailingZeros()).isEqualTo(value);
            assertThat(getTransaction.getNote()).isNullOrEmpty();
            assertThat(getTransaction.getTime()).isEqualTo(time);
            assertThat(getTransaction.getStatus()).isEqualTo(TransactionStatus.SUCCESS);

            //PUT
            String note = "Shipchain ICO funds";
            getTransaction.setNote(note);
            Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/transactions")
                    .request()
                    .put(Entity.entity(getTransaction, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatus()).isEqualTo(204);

            //GET
            getTransaction = client.target("http://localhost:" + RULE.getLocalPort() + "/transactions/" + transactionId)
                    .request()
                    .get(Transaction.class);
            assertThat(getTransaction.getTransactionId()).isEqualTo(transactionId);
            assertThat(getTransaction.getBlockBatchId()).isEqualTo(blockBatchId);
            assertThat(getTransaction.getContractId()).isEqualTo(contractId);
            assertThat(getTransaction.getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(getTransaction.getFee().stripTrailingZeros()).isEqualTo(fee);
            assertThat(getTransaction.getTo()).isEqualTo(to);
            assertThat(getTransaction.getFrom()).isEqualTo(from);
            assertThat(getTransaction.getMatchingAddress()).isEqualTo(to);
            assertThat(getTransaction.getValue().stripTrailingZeros()).isEqualTo(value);
            assertThat(getTransaction.getNote()).isEqualTo(note);
            assertThat(getTransaction.getTime()).isEqualTo(time);
            assertThat(getTransaction.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        } finally {
            //Hard delete
            for (int deleteTransactionId : deleteTransactionIdList) {
                transactionDAO.deleteHard(deleteTransactionId);
            }
            blockBatchDAO.deleteHard(blockBatchId);
            contractDAO.deleteHard(contractId);
        }
    }

    @Test
    public void testWalletTransactions() {
        List<WalletTransaction> walletTransactionList = null;
        try {
            SyncTransactionInput syncTransactionInput = new SyncTransactionInput();
            String address = "0xce9450f366bb61874f90484d05f30cc44c1d72dd";
            syncTransactionInput.setAddress(address);
            syncTransactionInput.setBlockchainType(BlockchainType.ETH);

            Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets/syncTransactions")
                    .request()
                    .post(Entity.entity(syncTransactionInput, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo(201);

            //GET LIST
            int offset = 0;
            int limit = 10;
            walletTransactionList = client.target("http://localhost:" + RULE.getLocalPort() + "/walletTransactions?" +
                    "address=" + address + "&offset=" + offset + "&limit=" + limit)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<WalletTransaction>>() {
                    });
            assertThat(walletTransactionList).isNotNull();
            assertThat(walletTransactionList.size()).isEqualTo(10);
            WalletTransaction walletTransaction = walletTransactionList.get(limit - 1);
            assertThat(walletTransaction).isNotNull();
            assertThat(walletTransaction.getWalletTransactionId()).isGreaterThan(0);
            assertThat(walletTransaction.getHash()).isNotNull();
            assertThat(walletTransaction.getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(walletTransaction.getFee().stripTrailingZeros()).isNotEqualTo(BigDecimal.ZERO);
            assertThat(walletTransaction.getTo()).isNotNull();
            assertThat(walletTransaction.getFrom()).isNotNull();
            assertThat(walletTransaction.getNote()).isNullOrEmpty();
            assertThat(walletTransaction.getTime()).isNotNull();
        } finally {
            //Hard delete
            walletTransactionDAO.deleteHardAll();
            contractDAO.deleteHardAll();
        }
    }

    private int createSamplePortfolio(int userId) {
        return createPortfolio(userId, "MyPortfolio").getPortfolioId();
    }

    private int createSampleContract() {
        Contract contract = createContract("0x6A13cbB3A3ecD7e8D74636F79c4A09AcB1f85606", "SHIPCHAIN",
                "SHIP", 18, BlockchainType.ETH);
        return contract.getContractId();
    }

    private long createSampleBlockBatch() {
        BlockBatch blockBatch = createBlockBatch(100, 200, BlockBatchStatus.CLOSED,
                BlockBatchStatus.CLOSED, "xyz", "xyz", null, null,
                null, null, null, null, 1000);
        return blockBatch.getBlockBatchId();
    }

    @Test
    public void testCoinMarketCapJob() {
        try {
            //GET LIST
            List<UrlOutput> urlList = client.target("http://localhost:" + RULE.getLocalPort() + "/urls")
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<UrlOutput>>() {
                    });
            assertThat(urlList).isNotNull();
            assertThat(urlList.size()).isEqualTo(1);
            assertThat(urlList.get(0)).isNotNull();
            assertThat(urlList.get(0).getUrlType()).isEqualTo(UrlType.TOKENS);
            assertThat(urlList.get(0).getUrl()).isNotNull();
            String url = urlList.get(0).getUrl().replace("address", "0xce9450f366bb61874f90484d05f30cc44c1d72dd");

            //Call api
            Client client = ClientBuilder.newClient();
            Response response = client.target(url)
                    .request()
                    .get();
            assertThat(response.getStatus()).isEqualTo(HttpStatusCodes.STATUS_CODE_OK);
            String payload = response.readEntity(String.class);
            assertThat(payload).isNotNull();

            TokenPayload tokenPayload = new TokenPayload(null, payload, "USD", BlockchainType.ETH);
            List<Token> tokenList = client.target("http://localhost:" + RULE.getLocalPort() + "/wallets/tokens")
                    .request()
                    .post(Entity.entity(tokenPayload, MediaType.APPLICATION_JSON_TYPE))
                    .readEntity(new GenericType<List<Token>>() {
                    });
            assertThat(tokenList).isNotNull();
            assertThat(tokenList.size()).isGreaterThan(0);
            boolean isOMGAllRight = false;
            for (Token token : tokenList) {
                if (token.getSymbol().equals("OMG")) {
                    assertThat(token.getBalance()).isNotEqualTo(BigDecimal.ZERO);
                    assertThat(token.getName()).isEqualTo("OmiseGO");
                    assertThat(token.getCmcUrl()).isEqualTo("https://api.coinmarketcap.com/v2/ticker/1808/?convert=USD");
                    isOMGAllRight = true;
                    break;
                }
            }
            assertThat(isOMGAllRight).isTrue();
        } finally {
            //Hard delete
            coinMarketCapDAO.deleteAll();
        }
    }

    //@Test
    public void testFirebaseAlert() throws Exception {
        try {
            String notificationCode = "e51_tAU730I:APA91bEYm3UFicvRG5LjxMwlTCU0zeHTNfdrK5UtosINDIf_oQJlVtwJzqDDevV73SHq" +
                    "x7WSzDUYfLvdUeOa6fQ8UcM3iwog47vmIf3lQqinNPI-XAb1kkP6pUJXBkFNsAnv-ZBF_sCK";
            String fcmKey = "AAAA1sg-Dic:APA91bEBAFPccQQclYbZv7aviQTpOLJ4D3NJnp_q0AOw-DgGGrS65DU0IO84GRSOQest0NC9fQmk" +
                    "kw5Me64oeIG4of3-oBlkC6t3-uQcZrfvL5FDsR0Tfr1qhE3SxhsITWI1zurPQ1u1";
            FirebaseAlertSender firebaseAlertSender = new FirebaseAlertSender(fcmKey);
            AddressActivityAlert alert = new AddressActivityAlert(123, notificationCode, OS.IOS, "SHIPCHAIN",
                    "SHIP", 123, "Address 1", "sent from", TransactionStatus.FAIL);
            alert.setOs(OS.IOS);
            String message = JsonMessage.getJsonMessage(alert);
            firebaseAlertSender.sendMessage(message);
        } finally {
        }
    }

    //@Test
    public void testJobs() throws InterruptedException {
        int userId = 0;
        int portfolioId = 0;
        int jobId = 0;
        List<Long> deleteBlockBatchIdList = new ArrayList<>();
        List<Integer> deleteWalletIdList = new ArrayList<>();
        List<Integer> deleteContractIdList = new ArrayList<>();
        List<Transaction> transactionList = new ArrayList<>();
        List<Notification> notificationList = null;
        try {
            // ------- test data -------

            //Block to test - blockId

            // Token -
            // From 0x34991b19119026b6b9699ed26988d92143cff5c6, OMG - 559, contract - 0xd26114cd6ee289accf82350c8d8487fedb8a0c07, https://etherscan.io/tx/0x758144a36c2473b3a03db8c3979d3f335edef0874ad759bc84b95ebfc2abad62
            // TO - 0xd9849ca6a3baf80d2799188abcdb22f6084fc940, VEN - 2, contract -  0xd850942ef8811f2a866692a623011bde52a462c1, https://etherscan.io/tx/0xbe64ef28bb5cc4bb795f9dcdff89b3b2706291b1c09487c9ec922206e2a5b6ee

            // ETH -
            // From - 0xbaa24475498056a6caee415769d544590633e686, ETH - 3.46165447, https://etherscan.io/tx/0x053a0a36621ac04b2839a727a5159dcd185abbedebc95826ee665279395a16c6
            // To - 0x52cbe628f9edf3b470d9b06cce586699b2de26b3, ETH - 0.0001936, https://etherscan.io/tx/0xced0ed648626a34746e59b01aa820dbde3962d76720cb97d5d368408e25c811b

            // ------- end test data -------

            //Create user (Anilesh Android)
            //String notificationCode = "cpIMM2ASmvs:APA91bEfr8vjXJe286tR_gR75dUDfBEACCQb08_Xu7By6CpnMvdGlyePQ4-oBPkUg9T33m_cHw4LCaMuUXpVskI-Q40O-qDcDt6jWVMC_8d8PIbll4HHplFMOTFNy4qO1eUSC4UFGjelWR_D-8HGIG6hSq9ZdNBokQ";
            String notificationCode = "abcd";
            String deviceId = "abcd-efgh";
            String timezone = "UTC+10:30";
            OS os = OS.ANDROID;
            String version = "0.1";
            User user = createUser(deviceId, timezone, notificationCode, os, version);
            userId = user.getUserId();

            //Create portfolio
            portfolioId = createSamplePortfolio(userId);

            //Create wallets
            Wallet omgFromWallet = createWallet(userId, portfolioId, "0x34991b19119026b6b9699ed26988d92143cff5c6",
                    "icxFromWallet", BlockchainType.ETH);
            deleteWalletIdList.add(omgFromWallet.getWalletId());
            Wallet venToWallet = createWallet(userId, portfolioId, "0xd9849ca6a3baf80d2799188abcdb22f6084fc940",
                    "wprToWallet", BlockchainType.ETH);
            deleteWalletIdList.add(venToWallet.getWalletId());
            Wallet ethFromWallet = createWallet(userId, portfolioId, "0xbaa24475498056a6caee415769d544590633e686",
                    "ethFromWallet", BlockchainType.ETH);
            deleteWalletIdList.add(ethFromWallet.getWalletId());
            Wallet ethToWallet = createWallet(userId, portfolioId, "0x52cbe628f9edf3b470d9b06cce586699b2de26b3",
                    "ethToWallet", BlockchainType.ETH);
            deleteWalletIdList.add(ethToWallet.getWalletId());

            //Create BlockBatch
//            BlockBatch blockBatch = createBlockBatch(6047007, 6047007, BlockBatchStatus.CLOSED, BlockBatchStatus.CLOSED,
//                    "xyz", "xyz", Timestamp.valueOf("2017-09-11 00:00:00.0"),
//                    Timestamp.valueOf("2017-09-11 01:00:00.0"), null, Timestamp.valueOf("2017-09-11 00:00:00.0"),
//                    Timestamp.valueOf("2017-09-11 00:00:00.0"), null, 100);
//            assertThat(blockBatch).isNotNull();
//            deleteBlockBatchIdList.add(blockBatch.getBlockBatchId());

            //Thread.sleep(1000); // Give some time for block batch maintainer job to run

            long blockId = 6047007;
            BlockBatch blockBatch = blockBatchDAO.findByStartBlockNumber(blockId);
            assertThat(blockBatch).isNull();

            //Trigger Job
            Job job = client.target("http://localhost:" + RULE.getLocalPort() + "/jobs/blockBatchMaintainerJob")
                    .request()
                    .post(null)
                    .readEntity(Job.class);
            assertThat(job).isNotNull();
            assertThat(job.getJobId()).isGreaterThan(0);
            jobDAO.deleteHard(job.getJobId());

            blockBatch = blockBatchDAO.findByStartBlockNumber(blockId);
            assertThat(blockBatch).isNotNull();
            assertThat(blockBatch.getBlockBatchId()).isGreaterThan(0);
            assertThat(blockBatch.getStartBlockNumber()).isEqualTo(blockId);
            assertThat(blockBatch.getEndBlockNumber()).isEqualTo(blockId);
            assertThat(blockBatch.getScanStatus()).isEqualTo(BlockBatchStatus.OPEN);
            assertThat(blockBatch.getScanServer()).isNull();
            assertThat(blockBatch.getScanStartTime()).isNull();
            assertThat(blockBatch.getScanEndTime()).isNull();
            assertThat(blockBatch.getNotifyStatus()).isEqualTo(BlockBatchStatus.OPEN);
            assertThat(blockBatch.getNotifyServer()).isNull();
            assertThat(blockBatch.getNotifyStartTime()).isNull();
            assertThat(blockBatch.getNotifyEndTime()).isNull();

            //Trigger Job
            job = client.target("http://localhost:" + RULE.getLocalPort() + "/jobs/blockBatchScanningJob")
                    .request()
                    .post(null)
                    .readEntity(Job.class);
            assertThat(job).isNotNull();
            assertThat(job.getJobId()).isGreaterThan(0);
            jobDAO.deleteHard(job.getJobId());

            blockBatch = blockBatchDAO.findByStartBlockNumber(blockId);
            assertThat(blockBatch).isNotNull();
            assertThat(blockBatch.getBlockBatchId()).isGreaterThan(0);
            deleteBlockBatchIdList.add(blockBatch.getBlockBatchId());
            assertThat(blockBatch.getNotifyStatus()).isEqualTo(BlockBatchStatus.OPEN);
            assertThat(blockBatch.getStartBlockNumber()).isEqualTo(blockId);
            assertThat(blockBatch.getEndBlockNumber()).isEqualTo(blockId);
            assertThat(blockBatch.getTotalTransactions()).isEqualTo(195);
            assertThat(blockBatch.getScanStatus()).isEqualTo(BlockBatchStatus.CLOSED);
            assertThat(blockBatch.getScanServer()).isNotNull();
            assertThat(blockBatch.getScanStartTime()).isNotNull();
            assertThat(blockBatch.getScanEndTime()).isNotNull();
            assertThat(blockBatch.getNotifyStatus()).isEqualTo(BlockBatchStatus.OPEN);
            assertThat(blockBatch.getNotifyServer()).isNull();
            assertThat(blockBatch.getNotifyStartTime()).isNull();
            assertThat(blockBatch.getNotifyEndTime()).isNull();

            transactionList = transactionDAO.getByBlockBatchId(blockBatch.getBlockBatchId());
            assertThat(transactionList).isNotNull();
            assertThat(transactionList.size()).isEqualTo(4);
            assertThat(transactionList.get(0).getBlockBatchId()).isEqualTo(blockBatch.getBlockBatchId());
            assertThat(transactionList.get(0).getMatchingAddress()).isEqualTo(venToWallet.getAddress());
            assertThat(transactionList.get(0).getStatus()).isEqualTo(TransactionStatus.SUCCESS);
            assertThat(transactionList.get(0).getTime()).isNotNull();
            assertThat(transactionList.get(0).getNote()).isNull();
            assertThat(transactionList.get(0).getValue().stripTrailingZeros()).isEqualTo(new BigDecimal(2));
            assertThat(transactionList.get(0).getTo()).isEqualTo(venToWallet.getAddress());
            assertThat(transactionList.get(0).getFrom()).isEqualTo("0xde16639c49f93b9119214caa99f3c902974292c0");
            assertThat(transactionList.get(0).getFee().stripTrailingZeros()).isEqualTo(new BigDecimal("0.000109414"));
            assertThat(transactionList.get(0).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(transactionList.get(0).getContractId()).isGreaterThan(0);
            deleteContractIdList.add(transactionList.get(0).getContractId());
            assertThat(transactionList.get(1).getBlockBatchId()).isEqualTo(blockBatch.getBlockBatchId());
            assertThat(transactionList.get(1).getMatchingAddress()).isEqualTo(ethToWallet.getAddress());
            assertThat(transactionList.get(1).getStatus()).isEqualTo(TransactionStatus.SUCCESS);
            assertThat(transactionList.get(1).getTime()).isNotNull();
            assertThat(transactionList.get(1).getNote()).isNull();
            assertThat(transactionList.get(1).getValue().stripTrailingZeros()).isEqualTo(new BigDecimal("0.0001936"));
            assertThat(transactionList.get(1).getTo()).isEqualTo(ethToWallet.getAddress());
            assertThat(transactionList.get(1).getFrom()).isEqualTo("0xe8f22cd2819fa3e68bf9fa59b1facb4d2c250151");
            assertThat(transactionList.get(1).getFee().stripTrailingZeros()).isEqualTo(new BigDecimal("0.00002121"));
            assertThat(transactionList.get(1).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(transactionList.get(1).getContractId()).isEqualTo(0);
            assertThat(transactionList.get(2).getBlockBatchId()).isEqualTo(blockBatch.getBlockBatchId());
            assertThat(transactionList.get(2).getMatchingAddress()).isEqualTo(omgFromWallet.getAddress());
            assertThat(transactionList.get(2).getStatus()).isEqualTo(TransactionStatus.SUCCESS);
            assertThat(transactionList.get(2).getTime()).isNotNull();
            assertThat(transactionList.get(2).getNote()).isNull();
            assertThat(transactionList.get(2).getValue().stripTrailingZeros()).isEqualTo(new BigDecimal("559"));
            assertThat(transactionList.get(2).getTo()).isEqualTo("0x5599b614078e862c67f5c522459a0c2d594314ee");
            assertThat(transactionList.get(2).getFrom()).isEqualTo(omgFromWallet.getAddress());
            assertThat(transactionList.get(2).getFee().stripTrailingZeros()).isEqualTo(new BigDecimal("0.00003734379"));
            assertThat(transactionList.get(2).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(transactionList.get(2).getContractId()).isGreaterThan(0);
            deleteContractIdList.add(transactionList.get(2).getContractId());
            assertThat(transactionList.get(3).getBlockBatchId()).isEqualTo(blockBatch.getBlockBatchId());
            assertThat(transactionList.get(3).getMatchingAddress()).isEqualTo(ethFromWallet.getAddress());
            assertThat(transactionList.get(3).getStatus()).isEqualTo(TransactionStatus.SUCCESS);
            assertThat(transactionList.get(3).getTime()).isNotNull();
            assertThat(transactionList.get(3).getNote()).isNull();
            assertThat(transactionList.get(3).getValue().stripTrailingZeros()).isEqualTo(new BigDecimal("3.46165447"));
            assertThat(transactionList.get(3).getTo()).isEqualTo("0x243bec9256c9a3469da22103891465b47583d9f1");
            assertThat(transactionList.get(3).getFrom()).isEqualTo(ethFromWallet.getAddress());
            assertThat(transactionList.get(3).getFee().stripTrailingZeros()).isEqualTo(new BigDecimal("0.000021"));
            assertThat(transactionList.get(3).getBlockchainType()).isEqualTo(BlockchainType.ETH);
            assertThat(transactionList.get(3).getContractId()).isEqualTo(0);

            //Trigger Job
            job = client.target("http://localhost:" + RULE.getLocalPort() + "/jobs/notificationJob")
                    .request()
                    .post(null)
                    .readEntity(Job.class);
            assertThat(job).isNotNull();
            assertThat(job.getJobId()).isGreaterThan(0);
            jobDAO.deleteHard(job.getJobId());

            //Validate notifications in notification table
            int offset = 0;
            int limit = 10;
            notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
                    .request()
                    .get(Response.class)
                    .readEntity(new GenericType<List<Notification>>() {
                    });
            assertThat(notificationList).isNotNull();
            assertThat(notificationList.size()).isEqualTo(4);
            assertThat(notificationList.get(0).getNotificationType()).isEqualTo(NotificationType.TRANSACTION);
            assertThat(notificationList.get(0).getNotificationStatus()).isEqualTo(NotificationStatus.UNREAD);
            //assertThat(notificationList.get(0).getMessage()).isEqualTo("ETH(ETH) activity found for ethFromWallet. Click for more information");

            //TODO: get transaction by txid

            //TODO: Notify job
        } finally {
            //Hard delete
            for (Notification notification : notificationList) {
                notificationDAO.deleteHard(notification.getNotificationId());
            }
            for (Transaction transaction : transactionList) {
                transactionDAO.deleteHard(transaction.getTransactionId());
            }
            for (int deleteWalletId : deleteWalletIdList) {
                walletDAO.deleteHard(deleteWalletId);
            }
            for (long deleteBlockBatchId : deleteBlockBatchIdList) {
                blockBatchDAO.deleteHard(deleteBlockBatchId);
            }
            for (int deleteContractId : deleteContractIdList) {
                contractDAO.deleteHard(deleteContractId);
            }
            portfolioDAO.deleteHard(portfolioId);
            userDAO.deleteHard(userId);
            jobDAO.deleteHard(jobId);
        }
    }

//    @Test
//    public void testNotifications() {
//        int notificationId1 = 0;
//        int notificationId2 = 0;
//        int notificationId3 = 0;
//        int icoId = 0;
//        int userId = 0;
//        try {
//            //Create user
//            userId = createSampleUser();
//
//            // Create notification 1
//            String message = "hello world !";
//            String data = Integer.toString(icoId);
//            Notification notification = createNotification(userId, message, icoId, data, NotificationType.ALARM);
//            notificationId1 = notification.getNotificationId();
//
//            //GET
//            Notification getNotification = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/" + notificationId1)
//                    .request()
//                    .get(Notification.class);
//            assertThat(getNotification.getNotificationId()).isEqualTo(notificationId1);
//            assertThat(getNotification.getUserId()).isEqualTo(userId);
//            assertThat(getNotification.getIcoId()).isEqualTo(icoId);
//            assertThat(getNotification.getData()).isEqualTo(data);
//            assertThat(getNotification.getMessage()).isEqualTo(message);
//            assertThat(getNotification.getNotificationStatus()).isEqualTo(NotificationStatus.UNREAD);
//            assertThat(getNotification.getNotificationType()).isEqualTo(NotificationType.ALARM);
//            assertThat(getNotification.getIsActive()).isTrue();
//
//            //PUT
//            getNotification.setNotificationStatus(NotificationStatus.READ);
//            Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications")
//                    .request()
//                    .put(Entity.entity(getNotification, MediaType.APPLICATION_JSON_TYPE));
//            assertThat(response.getStatus()).isEqualTo(204);
//
//            //GET
//            getNotification = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/" + notificationId1)
//                    .request()
//                    .get(Notification.class);
//            assertThat(getNotification.getNotificationId()).isEqualTo(notificationId1);
//            assertThat(getNotification.getUserId()).isEqualTo(userId);
//            assertThat(getNotification.getIcoId()).isEqualTo(icoId);
//            assertThat(getNotification.getData()).isEqualTo(data);
//            assertThat(getNotification.getMessage()).isEqualTo(message);
//            assertThat(getNotification.getNotificationStatus()).isEqualTo(NotificationStatus.READ);
//            assertThat(getNotification.getNotificationType()).isEqualTo(NotificationType.ALARM);
//            assertThat(getNotification.getIsActive()).isTrue();
//
//            // Create notification 2
//            message = "foo bar";
//            notification = createNotification(userId, message, icoId, data, NotificationType.GENERAL);
//            notificationId2 = notification.getNotificationId();
//
//            //GET LIST
//            int offset = 0;
//            int limit = 10;
//            List<Notification> notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
//                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(new GenericType<List<Notification>>() {
//                    });
//            assertThat(notificationList).isNotNull();
//            assertThat(notificationList.size()).isEqualTo(2);
//
//            //GET LIST
//            notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
//                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit + "&notificationType=" +
//                    NotificationType.ALARM)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(new GenericType<List<Notification>>() {
//                    });
//            assertThat(notificationList).isNotNull();
//            assertThat(notificationList.size()).isEqualTo(1);
//
//            //GET LIST
//            notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
//                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit + "&notificationType=" +
//                    NotificationType.GENERAL)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(new GenericType<List<Notification>>() {
//                    });
//            assertThat(notificationList).isNotNull();
//            assertThat(notificationList.size()).isEqualTo(1);
//
//            //GET LIST
//            notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
//                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit + "&notificationType=" +
//                    NotificationType.EXCHANGE)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(new GenericType<List<Notification>>() {
//                    });
//            assertThat(notificationList).isNotNull();
//            assertThat(notificationList.size()).isEqualTo(0);
//
//            //GET Unread Count
//            int count = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/count?" +
//                    "userId=" + userId)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(Integer.class);
//            assertThat(count).isEqualTo(1);
//
//            //GET Unread Count by type
//            NotificationCounts notificationCounts = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/countsByType?" +
//                    "userId=" + userId)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(NotificationCounts.class);
//            assertThat(notificationCounts).isNotNull();
//            assertThat(notificationCounts.getAlarmCount()).isEqualTo(0);
//            assertThat(notificationCounts.getExchangeCount()).isEqualTo(0);
//            assertThat(notificationCounts.getTransactionCount()).isEqualTo(1);
//            assertThat(notificationCounts.getNewsCount()).isEqualTo(0);
//
//            //Mark All Read
//            response = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/markAllRead")
//                    .request()
//                    .put(Entity.entity(userId, MediaType.APPLICATION_JSON_TYPE));
//            assertThat(response.getStatus()).isEqualTo(204);
//
//            //GET Unread Count
//            count = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/count?" +
//                    "userId=" + userId)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(Integer.class);
//            assertThat(count).isEqualTo(0);
//
//            //GET Unread Count by type
//            notificationCounts = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/countsByType?" +
//                    "userId=" + userId)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(NotificationCounts.class);
//            assertThat(notificationCounts).isNotNull();
//            assertThat(notificationCounts.getAlarmCount()).isEqualTo(0);
//            assertThat(notificationCounts.getExchangeCount()).isEqualTo(0);
//            assertThat(notificationCounts.getTransactionCount()).isEqualTo(0);
//            assertThat(notificationCounts.getNewsCount()).isEqualTo(0);
//
//            //DELETE
//            Response response1 = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/" + notificationId1)
//                    .request()
//                    .delete();
//            assertThat(response1.getStatus()).isEqualTo(204);
//
//            //GET
//            getNotification = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/" + notificationId1)
//                    .request()
//                    .get(Notification.class);
//            assertThat(getNotification).isNull();
//
//            // Create notification 3
//            message = "hello world 2 !";
//            notification = createNotification(userId, message, icoId, data, NotificationType.EXCHANGE);
//            notificationId3 = notification.getNotificationId();
//
//            //GET LIST
//            notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
//                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(new GenericType<List<Notification>>() {
//                    });
//            assertThat(notificationList).isNotNull();
//            assertThat(notificationList.size()).isEqualTo(2);
//
//            //GET LIST
//            notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
//                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit + "&notificationType=" +
//                    NotificationType.ALARM)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(new GenericType<List<Notification>>() {
//                    });
//            assertThat(notificationList).isNotNull();
//            assertThat(notificationList.size()).isEqualTo(0);
//
//            //GET LIST
//            notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
//                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit + "&notificationType=" +
//                    NotificationType.GENERAL)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(new GenericType<List<Notification>>() {
//                    });
//            assertThat(notificationList).isNotNull();
//            assertThat(notificationList.size()).isEqualTo(1);
//
//            //GET LIST
//            notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
//                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit + "&notificationType=" +
//                    NotificationType.EXCHANGE)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(new GenericType<List<Notification>>() {
//                    });
//            assertThat(notificationList).isNotNull();
//            assertThat(notificationList.size()).isEqualTo(1);
//
//            //GET Unread Count by type
//            notificationCounts = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/countsByType?" +
//                    "userId=" + userId)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(NotificationCounts.class);
//            assertThat(notificationCounts).isNotNull();
//            assertThat(notificationCounts.getAlarmCount()).isEqualTo(0);
//            assertThat(notificationCounts.getExchangeCount()).isEqualTo(1);
//            assertThat(notificationCounts.getTransactionCount()).isEqualTo(0);
//            assertThat(notificationCounts.getNewsCount()).isEqualTo(0);
//
//            //DELETE All by user
//            response1 = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications/deleteAll/" + userId)
//                    .request()
//                    .delete();
//            assertThat(response1.getStatus()).isEqualTo(204);
//
//            //GET LIST
//            notificationList = client.target("http://localhost:" + RULE.getLocalPort() + "/notifications?" +
//                    "userId=" + userId + "&offset=" + offset + "&limit=" + limit)
//                    .request()
//                    .get(Response.class)
//                    .readEntity(new GenericType<List<Notification>>() {
//                    });
//            assertThat(notificationList).isNotNull();
//            assertThat(notificationList.size()).isEqualTo(0);
//        } finally {
//            //Hard delete
//            notificationDAO.deleteHard(notificationId1);
//            notificationDAO.deleteHard(notificationId2);
//            notificationDAO.deleteHard(notificationId3);
//            userDAO.deleteHard(userId);
//        }
//    }
}
