package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.Feedback;
import com.walletalarm.platform.db.dao.FeedbackDAO;
import io.swagger.annotations.Api;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/feedbacks")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/feedbacks")
public class FeedbackResource {
    private final FeedbackDAO feedbackDAO;

    public FeedbackResource(FeedbackDAO feedbackDAO) {
        this.feedbackDAO = feedbackDAO;
    }

    @GET
    @Timed
    @Path("/{feedbackId}")
    public Response getFeedbackByID(@NotNull @Min(1) @PathParam("feedbackId") int feedbackId) {
        try {
            return Response.status(Response.Status.OK).entity(feedbackDAO.findById(feedbackId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createFeedback(Feedback feedback) {
        try {
            int feedbackId = feedbackDAO.create(feedback);
            String output = "{\"feedbackId\":" + feedbackId + "}";
            return Response.status(Response.Status.CREATED).entity(output).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response updateFeedback(Feedback feedback) {
        try {
            feedbackDAO.update(feedback);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}