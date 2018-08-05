package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.Portfolio;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PortfolioMapper implements ResultSetMapper<Portfolio> {
    public Portfolio map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new Portfolio(r.getInt("portfolio_id"),
                r.getString("name"),
                r.getInt("user_id"),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time"),
                r.getBoolean("is_active")
        );
    }
}
