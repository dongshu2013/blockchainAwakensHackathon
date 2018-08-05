package com.walletalarm.platform.db.mapper;

import com.walletalarm.platform.core.Feedback;
import com.walletalarm.platform.core.FeedbackStatus;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedbackMapper implements ResultSetMapper<Feedback> {
    public Feedback map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Feedback(r.getInt("feedback_id"),
                r.getInt("user_id"),
                r.getString("message"),
                r.getString("comment"),
                FeedbackStatus.valueOf(r.getString("feedback_status")),
                r.getTimestamp("created_time"),
                r.getTimestamp("modified_time"),
                r.getBoolean("is_active")
        );
    }
}
