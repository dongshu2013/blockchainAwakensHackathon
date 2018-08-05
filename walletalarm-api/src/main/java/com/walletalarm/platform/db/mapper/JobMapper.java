package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.Job;
import com.walletalarm.platform.core.JobStatus;
import com.walletalarm.platform.core.JobType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JobMapper implements ResultSetMapper<Job> {
    public Job map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Job(r.getInt("job_id"),
                JobType.valueOf(r.getString("type")),
                JobStatus.valueOf(r.getString("status")),
                r.getString("comment"),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time"),
                r.getBoolean("is_active")
        );
    }
}