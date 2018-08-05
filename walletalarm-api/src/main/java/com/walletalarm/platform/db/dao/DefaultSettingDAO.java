package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.DefaultSetting;
import com.walletalarm.platform.db.mapper.DefaultSettingMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface DefaultSettingDAO {
    @SqlUpdate("insert into default_setting (type, category, text, value, sort_order) values (:type, :category, :text, :value, :sortOrder)")
    @GetGeneratedKeys
    int create(@BindBean DefaultSetting defaultSetting);

    @SqlUpdate("update default_setting set "
            + "type = coalesce(:type, type), "
            + "category = coalesce(:category, category), "
            + "text = coalesce(:text, text) "
            + "default_value = coalesce(:value, value) "
            + "where default_setting_id = :defaultSettingId")
    void update(@BindBean DefaultSetting defaultSetting);

    @SqlUpdate("update default_setting set is_active = 0 where default_setting_id = :defaultSettingId")
    void delete(@Bind("defaultSettingId") int defaultSettingId);

    @SqlUpdate("delete from default_setting where default_setting_id = :defaultSettingId")
    void deleteHard(@Bind("defaultSettingId") int defaultSettingId);

    @SqlQuery("select * from default_setting where default_setting_id = :defaultSettingId and is_active = 1")
    @Mapper(DefaultSettingMapper.class)
    DefaultSetting findById(@Bind("defaultSettingId") int defaultSettingId);
}
