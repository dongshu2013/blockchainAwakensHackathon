package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Person;
import com.walletalarm.platform.db.mapper.PersonMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface PersonDAO {

    @SqlUpdate("insert into people (firstName, lastName) values (:firstName, :lastName)")
    @GetGeneratedKeys
    int create(@BindBean Person person);

    @SqlUpdate("update people set firstName = :firstName , lastName = :lastName where personid = :personid")
    void update(@BindBean Person person);

    @SqlQuery("select * from people where personid = :personid LIMIT 1")
    @Mapper(PersonMapper.class)
    Person findById(@Bind("personid") long personid);

}
