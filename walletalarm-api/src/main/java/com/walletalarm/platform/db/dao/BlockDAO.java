package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Block;
import com.walletalarm.platform.db.mapper.BlockMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface BlockDAO {
    @SqlUpdate("insert into block (block_number, status, server) values (:blockNumber, :status, :server)")
    @GetGeneratedKeys
    int create(@BindBean Block block);

    @SqlUpdate("update block set server = coalesce(:server, server), "
            + "status = coalesce(:status, status) "
            + "where block_id = :blockId")
    void update(@BindBean Block block);

    @SqlUpdate("delete from block where block_id = :blockId")
    void deleteHard(@Bind("blockId") int blockId);

    @SqlQuery("select * from block where block_id = :blockId")
    @Mapper(BlockMapper.class)
    Block findById(@Bind("blockId") int blockId);
}
