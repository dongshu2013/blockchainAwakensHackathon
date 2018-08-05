package com.walletalarm.platform.db.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public interface CoinMarketCapDAO {
    @SqlBatch("insert into coinmarketcap (id, name, symbol) values " +
            "(:id, :name, :symbol) ")
    void batchInsert(@Bind("id") List<Integer> idList,
                     @Bind("name") List<String> nameList,
                     @Bind("symbol") List<String> symbolList);

    @SqlUpdate("delete from coinmarketcap")
    void deleteAll();
}