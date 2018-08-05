package com.walletalarm.platform.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.walletalarm.platform.core.Person;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PersonMapper implements ResultSetMapper<Person> {
    public Person map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new Person(r.getInt("personid"), r.getString("firstName"), r.getString("lastName"));
    }
}
