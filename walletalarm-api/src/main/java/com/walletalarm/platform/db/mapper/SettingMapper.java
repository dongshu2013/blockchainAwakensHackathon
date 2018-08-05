package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.Setting;
import com.walletalarm.platform.core.SettingCategory;
import com.walletalarm.platform.core.SettingType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingMapper implements ResultSetMapper<Setting> {
    public Setting map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Setting(r.getInt("setting_id"),
                r.getInt("default_setting_id"),
                r.getInt("user_id"),
                SettingType.valueOf(r.getString("type")),
                SettingCategory.valueOf(r.getString("category")),
                r.getString("text"),
                r.getBoolean("value")
        );
    }
}