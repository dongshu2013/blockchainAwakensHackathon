package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.Notification;
import com.walletalarm.platform.core.NotificationStatus;
import com.walletalarm.platform.core.NotificationType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationMapper implements ResultSetMapper<Notification> {
    public Notification map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Notification(r.getInt("notification_id"),
                r.getInt("user_id"),
                r.getString("message"),
                r.getString("data"),
                NotificationStatus.valueOf(r.getString("notification_status")),
                NotificationType.valueOf(r.getString("notification_type")),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time"),
                r.getBoolean("is_active")
        );
    }
}
