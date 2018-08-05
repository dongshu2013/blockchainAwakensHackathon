package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.BlockchainType;
import com.walletalarm.platform.core.Wallet;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WalletMapper implements ResultSetMapper<Wallet> {
    public Wallet map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Wallet(r.getInt("wallet_id"),
                r.getString("address"),
                r.getString("name"),
                BlockchainType.valueOf(r.getString("blockchain_type")),
                r.getInt("portfolio_id"),
                r.getInt("user_id"),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time"),
                r.getBoolean("is_active")
        );
    }
}
