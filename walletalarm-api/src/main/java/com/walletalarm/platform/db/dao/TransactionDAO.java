package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.BlockchainType;
import com.walletalarm.platform.core.OS;
import com.walletalarm.platform.core.Transaction;
import com.walletalarm.platform.core.TransactionStatus;
import com.walletalarm.platform.core.alerts.AddressActivityAlert;
import com.walletalarm.platform.db.mapper.AddressActivityAlertMapper;
import com.walletalarm.platform.db.mapper.TransactionMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface TransactionDAO {
    @SqlUpdate("insert into transaction (block_batch_id, hash, blockchain_type, matching_address, `from`, `to`, status, " +
            "time, fee, value, contract_id, name, symbol) values " +
            "(:blockBatchId, :hash, :blockchainType, :matchingAddress, :from, :to, :status, :time, :fee, :value, " +
            ":contractId, :name, :symbol) " +
            "ON DUPLICATE KEY UPDATE hash = :hash")
    @GetGeneratedKeys
    int create(@BindBean Transaction transaction);

    @SqlBatch("insert into transaction (block_batch_id, hash, blockchain_type, matching_address, `from`, `to`, status, " +
            "time, fee, value, contract_id, name, symbol) values " +
            "(:blockBatchId, :hash, :blockchainType, :matchingAddress, :from, :to, :status, :time, :fee, :value, " +
            ":contractId, :name, :symbol) " +
            "ON DUPLICATE KEY UPDATE hash = :hash")
    void batchInsert(@Bind("blockBatchId") List<Long> blockBatchIdList,
                     @Bind("hash") List<String> hashList,
                     @Bind("blockchainType") List<BlockchainType> blockchainTypeList,
                     @Bind("matchingAddress") List<String> matchingAddressList,
                     @Bind("from") List<String> fromList,
                     @Bind("to") List<String> toList,
                     @Bind("status") List<TransactionStatus> statusList,
                     @Bind("time") List<Timestamp> timeList,
                     @Bind("fee") List<BigDecimal> feeList,
                     @Bind("value") List<BigDecimal> valueList,
                     @Bind("contractId") List<Integer> contractIdList,
                     @Bind("name") List<String> nameList,
                     @Bind("symbol") List<String> symbolList);

    @SqlUpdate("update transaction set note = coalesce(:note, note) "
            + "where transaction_id = :transactionId")
    void update(@BindBean Transaction transaction);

    @SqlUpdate("update transaction set is_active = 0 where transaction_id = :transactionId")
    void delete(@Bind("transactionId") int transactionId);

    @SqlUpdate("delete from transaction where transaction_id = :transactionId")
    void deleteHard(@Bind("transactionId") int transactionId);

    @SqlQuery("select * from transaction where transaction_id = :transactionId")
    @Mapper(TransactionMapper.class)
    Transaction findById(@Bind("transactionId") int transactionId);

    @SqlQuery("select * from transaction where block_batch_id = :blockBatchId order by transaction_id asc")
    @Mapper(TransactionMapper.class)
    List<Transaction> getByBlockBatchId(@Bind("blockBatchId") long blockBatchId);

    @SqlUpdate("delete from transaction where block_batch_id = :blockBatchId")
    void deleteByBlockBatchId(@Bind("blockBatchId") long blockBatchId);

    @SqlQuery("select u.user_id, u.notification_code, u.os, t.name, t.symbol, t.transaction_id, w.name as address_name, " +
            "t.status, CASE WHEN t.matching_address = t.to THEN 'sent from' ELSE 'received in' END as direction from transaction t " +
            "inner join block_batch b on t.block_batch_id = b.block_batch_id " +
            "inner join wallet w on w.address = t.matching_address " +
            "inner join user u on u.user_id = w.user_id " +
            "left outer join contract c on t.contract_id = c.contract_id " +
            "where t.block_batch_id = :blockBatchId and u.os = :os")
    @Mapper(AddressActivityAlertMapper.class)
    List<AddressActivityAlert> getAddressAlerts(@Bind("blockBatchId") long blockBatchId, @Bind("os") OS os);

    @SqlQuery("select * from transaction where to = :address or from = :address " +
            "order by time desc " +
            "limit :offset, :limit")
    @Mapper(TransactionMapper.class)
    List<Transaction> getTransactionByAddress(@Bind("address") String address, @Bind("offset") int offset,
                                              @Bind("limit") int limit);
}