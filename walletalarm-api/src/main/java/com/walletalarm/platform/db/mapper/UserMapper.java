package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.OS;
import com.walletalarm.platform.core.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User> {
    public User map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new User(r.getInt("user_id"),
                r.getString("device_id"),
                r.getString("timezone"),
                r.getString("notification_code"),
                OS.valueOf(r.getString("os")),
                r.getString("version"),
                r.getString("comment"),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time"),
                r.getBoolean("is_active")
        );
    }
}
