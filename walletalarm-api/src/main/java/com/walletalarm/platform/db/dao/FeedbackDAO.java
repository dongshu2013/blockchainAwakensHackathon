package com.walletalarm.platform.db.dao;

import com.walletalarm.platform.core.Feedback;
import com.walletalarm.platform.db.mapper.FeedbackMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface FeedbackDAO {
    @SqlUpdate("insert into feedback (user_id, message, comment) values " +
            "(:userId, :message, :comment)")
    @GetGeneratedKeys
    int create(@BindBean Feedback feedback);

    @SqlUpdate("update feedback set feedback_status = coalesce(:feedbackStatus, feedback_status), "
            + "comment = coalesce(:comment, comment) "
            + "where feedback_id = :feedbackId")
    void update(@BindBean Feedback feedback);

    @SqlUpdate("delete from feedback where feedback_id = :feedbackId")
    void deleteHard(@Bind("feedbackId") int feedbackId);

    @SqlQuery("select * from feedback where feedback_id = :feedbackId and is_active = 1")
    @Mapper(FeedbackMapper.class)
    Feedback findById(@Bind("feedbackId") int feedbackId);
}
