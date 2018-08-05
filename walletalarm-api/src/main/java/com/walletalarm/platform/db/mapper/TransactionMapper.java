package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.BlockchainType;
import com.walletalarm.platform.core.Transaction;
import com.walletalarm.platform.core.TransactionStatus;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements ResultSetMapper<Transaction> {
    public Transaction map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Transaction(r.getInt("transaction_id"),
                r.getString("hash"),
                r.getLong("block_batch_id"),
                BlockchainType.valueOf(r.getString("blockchain_type")),
                r.getString("matching_address"),
                r.getString("from"),
                r.getString("to"),
                TransactionStatus.valueOf(r.getString("status")),
                r.getTimestamp("time"),
                r.getBigDecimal("fee"),
                r.getBigDecimal("value"),
                r.getInt("contract_id"),
                r.getString("name"),
                r.getString("symbol"),
                r.getString("note"));
    }
}