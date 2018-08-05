package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Portfolio;
import com.walletalarm.platform.db.mapper.PortfolioMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface PortfolioDAO {
    @SqlUpdate("insert into portfolio (name, user_id) values " +
            "(:name, :userId) " +
            "ON DUPLICATE KEY UPDATE is_active = 1")
    @GetGeneratedKeys
    int create(@BindBean Portfolio portfolio);

    @SqlUpdate("update portfolio set name = coalesce(:name, name) "
            + "where portfolio_id = :portfolioId")
    void update(@BindBean Portfolio portfolio);

    @SqlUpdate("update portfolio set is_active = 0 where portfolio_id = :portfolioId")
    void delete(@Bind("portfolioId") int portfolioId);

    @SqlUpdate("delete from portfolio where portfolio_id = :portfolioId")
    void deleteHard(@Bind("portfolioId") int portfolioId);

    @SqlQuery("select * from portfolio where portfolio_id = :portfolioId and is_active = 1")
    @Mapper(PortfolioMapper.class)
    Portfolio findById(@Bind("portfolioId") int portfolioId);

    @SqlQuery("select * from portfolio where user_id = :userId " +
            "order by name asc " +
            "limit :offset, :limit")
    @Mapper(PortfolioMapper.class)
    List<Portfolio> getList(@Bind("offset") int offset, @Bind("limit") int limit, @Bind("userId") int userId);
}
