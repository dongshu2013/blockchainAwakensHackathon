package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Notification;
import com.walletalarm.platform.core.NotificationStatus;
import com.walletalarm.platform.core.NotificationType;
import com.walletalarm.platform.db.mapper.NotificationMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.IntegerMapper;

import java.util.List;

public interface NotificationDAO {
    @SqlUpdate("insert into notification (user_id, message, data, notification_type) values " +
            "(:userId, :message, :data, :notificationType) ")
    @GetGeneratedKeys
    int create(@BindBean Notification notification);

    @SqlBatch("insert into notification (user_id, message, data, notification_type) values " +
            "(:userId, :message, :data, :notificationType) ")
    void batchInsert(@Bind("userId") List<Integer> userIdList,
                     @Bind("message") List<String> messageList,
                     @Bind("data") List<String> data,
                     @Bind("notificationType") List<NotificationType> notificationType);

    @SqlUpdate("update notification set notification_status = coalesce(:notificationStatus, notification_status) "
            + "where notification_id = :notificationId")
    void update(@BindBean Notification notification);

    @SqlUpdate("update notification set notification_status = :notificationStatus "
            + "where user_id = :userId")
    void updateAllNotificationStatus(@Bind("notificationStatus") NotificationStatus notificationStatus, @Bind("userId") int userId);

    @SqlUpdate("update notification set is_active = 0 where notification_id = :notificationId")
    void delete(@Bind("notificationId") int notificationId);

    @SqlUpdate("update notification set is_active = 0 where user_id = :userId")
    void deleteAllByUser(@Bind("userId") int userId);

    @SqlUpdate("delete from notification where notification_id = :notificationId")
    void deleteHard(@Bind("notificationId") int notificationId);

    @SqlQuery("select * from notification where notification_id = :notificationId and is_active = 1")
    @Mapper(NotificationMapper.class)
    Notification findById(@Bind("notificationId") int notificationId);

    @SqlQuery("select count(*) from notification where user_id = :userId and is_active = 1 and notification_status = 'UNREAD'")
    @Mapper(IntegerMapper.class)
    int unreadCount(@Bind("userId") int userId);

    @SqlQuery("select count(*) from notification where user_id = :userId and is_active = 1 and " +
            "notification_status = 'UNREAD' and notification_type = :notificationType")
    @Mapper(IntegerMapper.class)
    int unreadCountByType(@Bind("notificationType") NotificationType notificationType, @Bind("userId") int userId);

    @SqlQuery("select * from notification where user_id = :userId and is_active = 1 order by notification_status asc, created_time desc " +
            "limit :offset, :limit")
    @Mapper(NotificationMapper.class)
    List<Notification> getList(@Bind("userId") int userId, @Bind("offset") int offset, @Bind("limit") int limit);

    @SqlQuery("select * from notification where user_id = :userId and is_active = 1 and notification_type = :notificationType " +
            "order by notification_status asc, created_time desc " +
            "limit :offset, :limit")
    @Mapper(NotificationMapper.class)
    List<Notification> getListByType(@Bind("userId") int userId, @Bind("notificationType") NotificationType notificationType,
                                     @Bind("offset") int offset, @Bind("limit") int limit);
}
