package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.User;
import com.walletalarm.platform.db.mapper.UserMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface UserDAO {
    @SqlUpdate("insert into user (device_id, timezone, notification_code, os, version, comment) values " +
            "(:deviceId, :timezone, :notificationCode, :os, :version, :comment) " +
            "ON DUPLICATE KEY UPDATE is_active = 1")
    @GetGeneratedKeys
    int create(@BindBean User user);

    @SqlUpdate("update user set device_id = coalesce(:deviceId, device_id), "
            + "timezone = coalesce(:timezone, timezone), "
            + "notification_code = coalesce(:notificationCode, notification_code), "
            + "os = coalesce(:os, os), "
            + "version = coalesce(:version, version), "
            + "comment = coalesce(:comment, comment), "
            + "is_active = coalesce(:isActive, is_active) "
            + "where user_id = :userId")
    void update(@BindBean User user);

    @SqlUpdate("update user set is_active = 0 where user_id = :userId")
    void delete(@Bind("userId") int userId);

    @SqlUpdate("delete from user where user_id = :userId")
    void deleteHard(@Bind("userId") int userId);

    @SqlQuery("select * from user where user_id = :userId and is_active = 1")
    @Mapper(UserMapper.class)
    User findById(@Bind("userId") int userId);

    @SqlQuery("select * from user where device_id = :deviceId and is_active = 1")
    @Mapper(UserMapper.class)
    User findByDeviceId(@Bind("deviceId") String deviceId);

    @SqlQuery("select * from user where notification_code = :notificationCode and is_active = 1")
    @Mapper(UserMapper.class)
    User findByNotificationCode(@Bind("notificationCode") String notificationCode);
}
