package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.Block;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BlockMapper implements ResultSetMapper<Block> {
    public Block map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Block(r.getInt("block_id"),
                r.getString("block_number"),
                r.getString("status"),
                r.getString("server"),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time")
        );
    }
}
