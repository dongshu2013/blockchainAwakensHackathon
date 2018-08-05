package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.DefaultSetting;
import com.walletalarm.platform.core.SettingCategory;
import com.walletalarm.platform.core.SettingType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultSettingMapper implements ResultSetMapper<DefaultSetting> {
    public DefaultSetting map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new DefaultSetting(r.getInt("default_setting_id"),
                SettingType.valueOf(r.getString("type")),
                SettingCategory.valueOf(r.getString("category")),
                r.getString("text"),
                r.getBoolean("value"),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time"),
                r.getBoolean("is_active")
        );
    }
}