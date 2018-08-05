package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.BlockBatch;
import com.walletalarm.platform.db.mapper.BlockBatchMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.LongMapper;

import java.util.List;

public interface BlockBatchDAO {
    @SqlUpdate("insert into block_batch (start_block_number, end_block_number) values " +
            "(:startBlockNumber, :endBlockNumber) ")
    @GetGeneratedKeys
    int create(@BindBean BlockBatch blockBatch);

    @SqlBatch("insert into block_batch (start_block_number, end_block_number) values " +
            "(:startBlockNumber, :endBlockNumber) ")
    void batchInsert(@Bind("startBlockNumber") List<Long> startBlockNumberList,
                     @Bind("endBlockNumber") List<Long> endBlockNumberList);

    @SqlUpdate("update block_batch set scan_server = :scanServer, "
            + "scan_start_time = CURRENT_TIMESTAMP, "
            + "scan_status = 'PROCESSING' "
            + "where block_batch_id = :blockBatchId and scan_server is null")
    int verifyScanBatchAssignment(@Bind("blockBatchId") long blockBatchId, @Bind("scanServer") String scanServer);

    @SqlUpdate("update block_batch set notify_server = :notifyServer, "
            + "notify_start_time = CURRENT_TIMESTAMP, "
            + "notify_status = 'PROCESSING' "
            + "where block_batch_id = :blockBatchId and notify_server is null")
    int verifyNotifyBatchAssignment(@Bind("blockBatchId") long blockBatchId, @Bind("notifyServer") String notifyServer);

    @SqlUpdate("delete from block_batch where block_batch_id = :blockBatchId")
    void deleteHard(@Bind("blockBatchId") long blockBatchId);

    @SqlQuery("select * from block_batch where block_batch_id = :blockBatchId")
    @Mapper(BlockBatchMapper.class)
    BlockBatch findById(@Bind("blockBatchId") long blockBatchId);

    @SqlQuery("select * from block_batch where start_block_number = :startBlockNumber")
    @Mapper(BlockBatchMapper.class)
    BlockBatch findByStartBlockNumber(@Bind("startBlockNumber") long startBlockNumber);

    @SqlQuery("select max(end_block_number) from block_batch")
    @Mapper(LongMapper.class)
    long getMaxBlockNumber();

    @SqlUpdate("update block_batch set scan_server = null, "
            + "scan_start_time = null, "
            + "scan_end_time = null, "
            + "scan_status = 'OPEN', "
            + "total_transactions = 0, "
            + "scan_reset_time = CURRENT_TIMESTAMP "
            + "where scan_status = 'PROCESSING' and TIMESTAMPDIFF(SECOND, scan_start_time, CURRENT_TIMESTAMP) >= 120 "
            + "and block_batch_id = :blockBatchId")
    int resetScanUnprocessedJobs(@Bind("blockBatchId") long blockBatchId);

    @SqlQuery("select block_batch_id from block_batch " +
            "where scan_status = 'PROCESSING' and TIMESTAMPDIFF(SECOND, scan_start_time, CURRENT_TIMESTAMP) >= 120")
    @Mapper(LongMapper.class)
    List<Long> getResetScanUnprocessedBlockBatchIds();

    @SqlUpdate("update block_batch set notify_server = null, "
            + "notify_start_time = null, "
            + "notify_end_time = null, "
            + "notify_status = 'OPEN', "
            + "total_transactions = 0, "
            + "notify_reset_time = CURRENT_TIMESTAMP "
            + "where notify_status = 'PROCESSING' and TIMESTAMPDIFF(SECOND, notify_start_time, CURRENT_TIMESTAMP) >= 120 "
            + "and block_batch_id = :blockBatchId")
    int resetNotifyUnprocessedJobs(@Bind("blockBatchId") long blockBatchId);

    @SqlQuery("select block_batch_id from block_batch " +
            "where notify_status = 'PROCESSING' and TIMESTAMPDIFF(SECOND, notify_start_time, CURRENT_TIMESTAMP) >= 120")
    @Mapper(LongMapper.class)
    List<Long> getResetNotifyUnprocessedBlockBatchIds();

    @SqlQuery("select * from block_batch where scan_status = 'OPEN' order by block_batch_id ASC LIMIT 1")
    @Mapper(BlockBatchMapper.class)
    BlockBatch getNextScanBlockBatch();

    @SqlQuery("select * from block_batch where scan_status = 'CLOSED' and notify_status = 'OPEN' order by block_batch_id ASC LIMIT 1")
    @Mapper(BlockBatchMapper.class)
    BlockBatch getNextNotifyBlockBatch();

    @Transaction
    default BlockBatch getNextScanBlockBatch(@Bind("scanServer") String scanServer) {
        BlockBatch nextScanBlockBatch = getNextScanBlockBatch();
        if (nextScanBlockBatch != null && nextScanBlockBatch.getBlockBatchId() > 0) {
            int rowsUpdated = verifyScanBatchAssignment(nextScanBlockBatch.getBlockBatchId(), scanServer);
            if (rowsUpdated > 0) {
                return nextScanBlockBatch;
            }
        }
        return null;
    }

    @Transaction
    default BlockBatch getNextNotifyBlockBatch(@Bind("notifyServer") String notifyServer) {
        BlockBatch nextNotifyBlockBatch = getNextNotifyBlockBatch();
        if (nextNotifyBlockBatch != null && nextNotifyBlockBatch.getBlockBatchId() > 0) {
            int rowsUpdated = verifyNotifyBatchAssignment(nextNotifyBlockBatch.getBlockBatchId(), notifyServer);
            if (rowsUpdated > 0) {
                return nextNotifyBlockBatch;
            }
        }
        return null;
    }

    @SqlUpdate("update block_batch set scan_end_time = CURRENT_TIMESTAMP, "
            + "scan_status = 'CLOSED', "
            + "total_transactions = :totalTransactions "
            + "where block_batch_id = :blockBatchId and scan_server = :scanServer and scan_status = 'PROCESSING'")
    void commitBlockBatchScan(@Bind("totalTransactions") int totalTransactions, @Bind("blockBatchId") long blockBatchId,
                              @Bind("scanServer") String scanServer);

    @SqlUpdate("update block_batch set notify_end_time = CURRENT_TIMESTAMP, "
            + "notify_status = 'CLOSED' "
            + "where block_batch_id = :blockBatchId and notify_server = :notifyServer and notify_status = 'PROCESSING'")
    void commitBlockBatchNotify(@Bind("blockBatchId") long blockBatchId, @Bind("notifyServer") String notifyServer);
}
