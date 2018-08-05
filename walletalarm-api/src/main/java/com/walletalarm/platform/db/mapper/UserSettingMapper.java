package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.OS;
import com.walletalarm.platform.core.UserSetting;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSettingMapper implements ResultSetMapper<UserSetting> {
    public UserSetting map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new UserSetting(r.getInt("user_id"),
                r.getString("notification_code"),
                r.getBoolean("value"),
                OS.valueOf(r.getString("os")));
    }
}
