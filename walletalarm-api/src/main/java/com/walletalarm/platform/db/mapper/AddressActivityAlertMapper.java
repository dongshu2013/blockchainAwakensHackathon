package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.OS;
import com.walletalarm.platform.core.TransactionStatus;
import com.walletalarm.platform.core.alerts.AddressActivityAlert;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressActivityAlertMapper implements ResultSetMapper<AddressActivityAlert> {
    public AddressActivityAlert map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new AddressActivityAlert(r.getInt("user_id"),
                r.getString("notification_code"),
                OS.valueOf(r.getString("os")),
                r.getString("name"),
                r.getString("symbol"),
                r.getInt("transaction_id"),
                r.getString("address_name"),
                r.getString("direction"),
                TransactionStatus.valueOf(r.getString("status")));
    }
}