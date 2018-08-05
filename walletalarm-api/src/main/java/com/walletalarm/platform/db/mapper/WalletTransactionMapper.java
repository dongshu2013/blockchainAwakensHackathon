package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.BlockchainType;
import com.walletalarm.platform.core.TransactionStatus;
import com.walletalarm.platform.core.WalletTransaction;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WalletTransactionMapper implements ResultSetMapper<WalletTransaction> {
    public WalletTransaction map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new WalletTransaction(r.getInt("wallet_transaction_id"),
                r.getString("hash"),
                BlockchainType.valueOf(r.getString("blockchain_type")),
                r.getString("from"),
                r.getString("to"),
                TransactionStatus.valueOf(r.getString("status")),
                r.getTimestamp("time"),
                r.getBigDecimal("fee"),
                r.getBigDecimal("value"),
                r.getString("name"),
                r.getString("symbol"),
                r.getString("note"));
    }
}