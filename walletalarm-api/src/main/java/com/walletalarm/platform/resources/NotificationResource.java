package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.Notification;
import com.walletalarm.platform.core.NotificationCounts;
import com.walletalarm.platform.core.NotificationStatus;
import com.walletalarm.platform.core.NotificationType;
import com.walletalarm.platform.db.dao.NotificationDAO;
import io.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/notifications")
public class NotificationResource {
    private final NotificationDAO notificationDAO;

    public NotificationResource(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @GET
    @Timed
    @Path("/{notificationId}")
    public Response getNotificationByID(@PathParam("notificationId") int notificationId) {
        try {
            return Response.status(Response.Status.OK).entity(notificationDAO.findById(notificationId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Timed
    @Path("/count")
    public Response getNotificationUnreadCount(@DefaultValue("0") @QueryParam("userId") int userId) {
        try {
            return Response.status(Response.Status.OK).entity(notificationDAO.unreadCount(userId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/countsByType")
    @Timed
    public Response getUnreadNotificationCountsByType(@DefaultValue("0") @QueryParam("userId") int userId) {
        try {
            NotificationCounts notificationCounts = new NotificationCounts(
                    notificationDAO.unreadCountByType(NotificationType.TRANSACTION, userId));
            return Response.status(Response.Status.OK).entity(notificationCounts).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Timed
    public Response getList(@DefaultValue("0") @QueryParam("userId") int userId,
                            @DefaultValue("0") @QueryParam("offset") int offset,
                            @DefaultValue("10") @QueryParam("limit") int limit,
                            @QueryParam("notificationType") NotificationType notificationType) {
        try {
            List<Notification> notificationList;
            if (notificationType == null) {
                notificationList = notificationDAO.getList(userId, offset, limit);
            } else {
                notificationList = notificationDAO.getListByType(userId, notificationType, offset, limit);
            }
            return Response.status(Response.Status.OK).entity(notificationList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createNotification(Notification notification) {
        try {
            int notificationId = notificationDAO.create(notification);
            String output = "{\"notificationId\":" + notificationId + "}";
            return Response.status(Response.Status.CREATED).entity(output).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response updateNotification(Notification notification) {
        try {
            notificationDAO.update(notification);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/markAllRead")
    public Response markAllRead(int userId) {
        try {
            notificationDAO.updateAllNotificationStatus(NotificationStatus.READ, userId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{notificationId}")
    public Response deleteNotification(@PathParam("notificationId") int notificationId) {
        try {
            notificationDAO.delete(notificationId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/deleteAll/{userId}")
    public Response deleteAllNotificationByUser(@PathParam("userId") int userId) {
        try {
            notificationDAO.deleteAllByUser(userId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}