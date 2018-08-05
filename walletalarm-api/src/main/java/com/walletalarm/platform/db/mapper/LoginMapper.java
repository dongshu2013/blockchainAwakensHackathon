package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.Login;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginMapper implements ResultSetMapper<Login> {
    public Login map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Login(r.getInt("login_id"),
                r.getInt("user_id"),
                r.getString("ip_address"),
                r.getTimestamp("login_time")
        );
    }
}
