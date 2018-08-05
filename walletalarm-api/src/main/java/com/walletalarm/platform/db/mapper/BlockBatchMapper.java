package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.BlockBatch;
import com.walletalarm.platform.core.BlockBatchStatus;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BlockBatchMapper implements ResultSetMapper<BlockBatch> {
    public BlockBatch map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new BlockBatch(r.getLong("block_batch_id"),
                r.getLong("start_block_number"),
                r.getLong("end_block_number"),
                BlockBatchStatus.valueOf(r.getString("scan_status")),
                BlockBatchStatus.valueOf(r.getString("notify_status")),
                r.getString("scan_server"),
                r.getString("notify_server"),
                r.getTimestamp("scan_start_time"),
                r.getTimestamp("scan_end_time"),
                r.getTimestamp("scan_reset_time"),
                r.getTimestamp("notify_start_time"),
                r.getTimestamp("notify_end_time"),
                r.getTimestamp("notify_reset_time"),
                r.getInt("total_transactions"),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time")
        );
    }
}
