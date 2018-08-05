package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.BlockchainType;
import com.walletalarm.platform.core.Contract;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContractMapper implements ResultSetMapper<Contract> {
    public Contract map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Contract(r.getInt("contract_id"),
                r.getString("address"),
                r.getString("name"),
                r.getString("symbol"),
                r.getInt("decimals"),
                BlockchainType.valueOf(r.getString("blockchain_type")),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time"),
                r.getBoolean("is_active")
        );
    }
}
