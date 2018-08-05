package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Wallet;
import com.walletalarm.platform.db.mapper.WalletMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.TimestampMapper;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

public interface WalletDAO {
    @SqlUpdate("insert into wallet (address, name, blockchain_type, portfolio_id, user_id) values " +
            "(:address, :name, :blockchainType, :portfolioId, :userId) " +
            "ON DUPLICATE KEY UPDATE is_active = 1")
    @GetGeneratedKeys
    int create(@BindBean Wallet wallet);

    @SqlUpdate("update wallet set address = coalesce(:address, address), "
            + "name = coalesce(:name, name), "
            + "blockchain_type = coalesce(:blockchainType, blockchain_type), "
            + "portfolio_id = coalesce(:portfolioId, portfolio_id), "
            + "user_id = coalesce(:userId, user_id) "
            + "where wallet_id = :walletId")
    void update(@BindBean Wallet wallet);

    @SqlUpdate("update wallet set is_active = 0 where wallet_id = :walletId")
    void delete(@Bind("walletId") int walletId);

    @SqlUpdate("delete from wallet where wallet_id = :walletId")
    void deleteHard(@Bind("walletId") int walletId);

    @SqlQuery("select * from wallet where wallet_id = :walletId and is_active = 1")
    @Mapper(WalletMapper.class)
    Wallet findById(@Bind("walletId") int walletId);

    @SqlQuery("select * from wallet where user_id = :userId and is_active = 1 " +
            "order by name asc " +
            "limit :offset, :limit")
    @Mapper(WalletMapper.class)
    List<Wallet> getList(@Bind("offset") int offset, @Bind("limit") int limit, @Bind("userId") int userId);

    @SqlQuery("select * from wallet where user_id = :userId and portfolio_id = :portfolioId and is_active = 1 " +
            "order by name asc " +
            "limit :offset, :limit")
    @Mapper(WalletMapper.class)
    List<Wallet> getList(@Bind("offset") int offset, @Bind("limit") int limit, @Bind("userId") int userId,
                         @Bind("portfolioId") int portfolioId);

    @SqlQuery("select max(modified_time) from wallet")
    @Mapper(TimestampMapper.class)
    Timestamp getLatestModifiedTime();

    @SqlQuery("select distinct address from wallet where is_active = 1 and modified_time >= :currentMaxWalletModifiedTime " +
            "and modified_time <= :latestWalletModifiedTime")
    Iterator<String> getNewAddressesAdded(@Bind("currentMaxWalletModifiedTime") Timestamp currentMaxWalletModifiedTime,
                                          @Bind("latestWalletModifiedTime") Timestamp latestWalletModifiedTime);

    @SqlQuery("select distinct address from wallet where is_active = 0 and modified_time >= :currentMaxWalletModifiedTime " +
            "and modified_time <= :latestWalletModifiedTime")
    Iterator<String> getNewAddressesRemoved(@Bind("currentMaxWalletModifiedTime") Timestamp currentMaxWalletModifiedTime,
                                            @Bind("latestWalletModifiedTime") Timestamp latestWalletModifiedTime);
}
