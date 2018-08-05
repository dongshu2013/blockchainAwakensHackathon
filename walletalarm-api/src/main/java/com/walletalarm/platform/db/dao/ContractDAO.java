package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Contract;
import com.walletalarm.platform.db.mapper.ContractMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.TimestampMapper;

import java.sql.Timestamp;
import java.util.List;

public interface ContractDAO {
    @SqlUpdate("insert into contract (address, name, symbol, decimals, blockchain_type) values " +
            "(:address, :name, :symbol, :decimals, :blockchainType) " +
            "ON DUPLICATE KEY UPDATE is_active = 1")
    @GetGeneratedKeys
    int create(@BindBean Contract contract);

    @SqlUpdate("update contract set address = coalesce(:address, address), "
            + "name = coalesce(:name, name), "
            + "symbol = coalesce(:symbol, symbol), "
            + "decimals = coalesce(:decimals, decimals), "
            + "blockchain_type = coalesce(:blockchainType, blockchain_type) "
            + "where contract_id = :contractId")
    void update(@BindBean Contract contract);

    @SqlUpdate("update contract set is_active = 0 where contract_id = :contractId")
    void delete(@Bind("contractId") int contractId);

    @SqlUpdate("delete from contract where contract_id = :contractId")
    void deleteHard(@Bind("contractId") int contractId);

    @SqlUpdate("delete from contract")
    void deleteHardAll();

    @SqlQuery("select * from contract where contract_id = :contractId and is_active = 1")
    @Mapper(ContractMapper.class)
    Contract findById(@Bind("contractId") int contractId);

    @SqlQuery("select * from contract where address = :address and is_active = 1")
    @Mapper(ContractMapper.class)
    Contract findByAddress(@Bind("address") String address);

    @SqlQuery("select max(modified_time) from contract")
    @Mapper(TimestampMapper.class)
    Timestamp getLatestModifiedTime();

    @SqlQuery("select * from contract where is_active = 1 and modified_time >= :currentMaxContractModifiedTime " +
            "and modified_time <= :latestContractModifiedTime")
    @Mapper(ContractMapper.class)
    List<Contract> getNewContractsAdded(@Bind("currentMaxContractModifiedTime") Timestamp currentMaxContractModifiedTime,
                                        @Bind("latestContractModifiedTime") Timestamp latestContractModifiedTime);
}
