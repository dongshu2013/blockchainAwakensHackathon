package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.BlockchainType;
import com.walletalarm.platform.core.TransactionStatus;
import com.walletalarm.platform.core.WalletTransaction;
import com.walletalarm.platform.db.mapper.WalletTransactionMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface WalletTransactionDAO {
    @SqlBatch("insert into wallet_transaction (hash, blockchain_type, `from`, `to`, status, time, fee, value, name, " +
            "symbol) values " +
            "(:hash, :blockchainType, :from, :to, :status, :time, :fee, :value, :name, :symbol) " +
            "ON DUPLICATE KEY UPDATE hash = :hash")
    void batchInsert(@Bind("hash") List<String> hashList,
                     @Bind("blockchainType") List<BlockchainType> blockchainTypeList,
                     @Bind("from") List<String> fromList,
                     @Bind("to") List<String> toList,
                     @Bind("status") List<TransactionStatus> statusList,
                     @Bind("time") List<Timestamp> timeList,
                     @Bind("fee") List<BigDecimal> feeList,
                     @Bind("value") List<BigDecimal> valueList,
                     @Bind("name") List<String> nameList,
                     @Bind("symbol") List<String> symbolList);

    @SqlUpdate("update wallet_transaction set note = coalesce(:note, note) "
            + "where wallet_transaction_id = :walletTransactionId")
    void update(@BindBean WalletTransaction walletTransaction);

    @SqlUpdate("update wallet_transaction set is_active = 0 where wallet_transaction_id = :walletTransactionId")
    void delete(@Bind("walletTransactionId") int walletTransactionId);

    @SqlUpdate("delete from wallet_transaction where wallet_transaction_id = :walletTransactionId")
    void deleteHard(@Bind("walletTransactionId") int walletTransactionId);

    @SqlQuery("select * from wallet_transaction where wallet_transaction_id = :walletTransactionId")
    @Mapper(WalletTransactionMapper.class)
    WalletTransaction findById(@Bind("walletTransactionId") int walletTransactionId);

    @SqlQuery("select * from wallet_transaction where to = :address or from = :address " +
            "order by time desc " +
            "limit :offset, :limit")
    @Mapper(WalletTransactionMapper.class)
    List<WalletTransaction> getTransactionByAddress(@Bind("address") String address, @Bind("offset") int offset,
                                              @Bind("limit") int limit);
}