package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Setting;
import com.walletalarm.platform.db.mapper.SettingMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface SettingDAO {
    @SqlUpdate("insert into setting (user_id, default_setting_id, value) values (:userId, :defaultSettingId, :value) " +
            "ON DUPLICATE KEY UPDATE value = :value")
    @GetGeneratedKeys
    int createOrUpdate(@BindBean Setting setting);

    @SqlUpdate("update setting set value = coalesce(:value, value) where setting_id = :settingId")
    void update(@BindBean Setting setting);

    @SqlUpdate("update setting set is_active = 0 where setting_id = :settingId")
    void delete(@Bind("settingId") int userSettingId);

    @SqlUpdate("delete from setting where setting_id = :settingId")
    void deleteHard(@Bind("settingId") int settingId);

    @SqlQuery("select s.setting_id, ds.default_setting_id, s.user_id, ds.type, ds.category, ds.text, s.value " +
            "from default_setting ds left outer join setting s on ds.default_setting_id = s.default_setting_id and " +
            "s.setting_id = :settingId where ds.is_active = 1 and s.is_active = 1")
    @Mapper(SettingMapper.class)
    Setting findById(@Bind("settingId") int settingId);

    @SqlQuery("select coalesce(s.setting_id, 0) as setting_id, ds.default_setting_id, coalesce(s.user_id, 0) as user_id, " +
            "ds.type, ds.category, ds.text, coalesce(s.value, ds.value) as value " +
            "from default_setting ds left outer join setting s on ds.default_setting_id = s.default_setting_id and " +
            "s.user_id = :userId where ds.is_active = 1 order by ds.sort_order asc")
    @Mapper(SettingMapper.class)
    List<Setting> findSettingsByUser(@Bind("userId") int userId);
}
