package com.walletalarm.platform.handlers;

import com.walletalarm.platform.core.*;
import com.walletalarm.platform.core.alerts.AddressActivityAlert;
import com.walletalarm.platform.core.alerts.JsonMessage;
import com.walletalarm.platform.db.dao.BlockBatchDAO;
import com.walletalarm.platform.db.dao.JobDAO;
import com.walletalarm.platform.db.dao.NotificationDAO;
import com.walletalarm.platform.db.dao.TransactionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by patankar on 9/29/17.
 */
public class NotificationJobHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationJobHandler.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    NotificationJobHandler() {
        FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static int doNotificationJob(JobDAO jobDAO, BlockBatchDAO blockBatchDAO, NotificationDAO notificationDAO,
                                        TransactionDAO transactionDAO, String server, String fcmToken) {
        //Create job info
        Job job = new Job();
        job.setStatus(JobStatus.STARTED);
        job.setType(JobType.NOTIFICATION);
        int jobId = jobDAO.create(job);

        LOGGER.info("Starting Notification Job. Start time - " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));

        notifyBlockBatch(blockBatchDAO, notificationDAO, transactionDAO, server, fcmToken);

        LOGGER.info("Finished Notification Job. End time - " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));

        //Update job info
        job.setJobId(jobId);
        job.setStatus(JobStatus.FINISHED);
        jobDAO.update(job);

        return jobId;
    }

    private static void notifyBlockBatch(BlockBatchDAO blockBatchDAO, NotificationDAO notificationDAO,
                                         TransactionDAO transactionDAO, String server, String fcmToken) {
        FirebaseAlertSender firebaseAlertSender = new FirebaseAlertSender(fcmToken);
        try {
            // Get block batch
            BlockBatch blockBatch = blockBatchDAO.getNextNotifyBlockBatch(server);

            if (blockBatch == null) { //If another server has picked up this batch move on the next available batch
                return;
            }

            //Send notifications by OS
            List<AddressActivityAlert> androidAlertList = transactionDAO.getAddressAlerts(blockBatch.getBlockBatchId(),
                    OS.ANDROID);
            for (AddressActivityAlert androidAlert : androidAlertList) {
                Notification notification = ConvertToNotification(androidAlert);
                notificationDAO.create(notification);
                firebaseAlertSender.sendMessage(JsonMessage.getJsonMessage(androidAlert));
            }

            List<AddressActivityAlert> iOSAlertList = transactionDAO.getAddressAlerts(blockBatch.getBlockBatchId(),
                    OS.IOS);
            for (AddressActivityAlert iosAlert : iOSAlertList) {
                Notification notification = ConvertToNotification(iosAlert);
                notificationDAO.create(notification);
                firebaseAlertSender.sendMessage(JsonMessage.getJsonMessage(iosAlert));
            }

            //Commit BlockBatch entry
            blockBatchDAO.commitBlockBatchNotify(blockBatch.getBlockBatchId(), server);
        } catch (Exception ex) {
            LOGGER.error("Error scrapping BlockBatch, ", ex);
        }
    }

    private static Notification ConvertToNotification(AddressActivityAlert addressActivityAlert) {
        Notification notification = new Notification();
        notification.setUserId(addressActivityAlert.getUserId());
        notification.setMessage(addressActivityAlert.getBody());
        notification.setData(addressActivityAlert.getIds());
        notification.setNotificationType(NotificationType.TRANSACTION);
        return notification;
    }
}