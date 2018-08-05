package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Job;
import com.walletalarm.platform.db.mapper.JobMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface JobDAO {
    @SqlUpdate("insert into job (type, status, comment) values (:type, :status, :comment)")
    @GetGeneratedKeys
    int create(@BindBean Job job);

    @SqlUpdate("update job set type = coalesce(:type, type), "
            + "status = coalesce(:status, status), "
            + "comment = coalesce(:comment, comment) "
            + "where job_id = :jobId")
    void update(@BindBean Job job);

    @SqlUpdate("update job set is_active = 0 where job_id = :jobId")
    void delete(@Bind("jobId") int jobId);

    @SqlUpdate("delete from job where job_id = :jobId")
    void deleteHard(@Bind("jobId") int jobId);

    @SqlQuery("select * from job where job_id = :jobId and is_active = 1")
    @Mapper(JobMapper.class)
    Job findById(@Bind("jobId") int jobId);
}
