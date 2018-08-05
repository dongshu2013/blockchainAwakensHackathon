package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Login;
import com.walletalarm.platform.db.mapper.LoginMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface LoginDAO {
    @SqlUpdate("insert into login (user_id, ip_address) values (:userId, :ipAddress)")
    @GetGeneratedKeys
    int create(@BindBean Login login);

    @SqlUpdate("delete from login where login_id = :loginId")
    void deleteHard(@Bind("loginId") int loginId);

    @SqlQuery("select * from login where login_id = :loginId")
    @Mapper(LoginMapper.class)
    Login findById(@Bind("loginId") int loginId);
}
