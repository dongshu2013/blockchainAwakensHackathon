package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.ErrorBody;
import com.walletalarm.platform.core.ReplaceDevice;
import com.walletalarm.platform.core.User;
import com.walletalarm.platform.db.dao.UserDAO;
import io.swagger.annotations.Api;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/users")
public class UserResource {
    private static final String USER_WITH_THIS_DEVICE_ID_ALREADY_EXISTS = "User with this email already exists";
    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @Timed
    @Path("/{userId}")
    public Response getUserByID(@NotNull @Min(1) @PathParam("userId") int userId) {
        try {
            return Response.status(Response.Status.OK).entity(userDAO.findById(userId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Timed
    public Response getUserByDeviceID(@QueryParam("deviceId") String deviceId) {
        try {
            return Response.status(Response.Status.OK).entity(userDAO.findByDeviceId(deviceId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Timed
    @Path("/notificationCode/{notificationCode}")
    public Response getUserByNotificationCode(@PathParam("notificationCode") String notificationCode) {
        try {
            return Response.status(Response.Status.OK).entity(userDAO.findByNotificationCode(notificationCode)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createUser(User user) {
        try {
            //Check if the user already exists
            User existingUser = userDAO.findByDeviceId(user.getDeviceId());
            if (existingUser != null) {
                ErrorBody errorBody = new ErrorBody();
                errorBody.setMessage(USER_WITH_THIS_DEVICE_ID_ALREADY_EXISTS);
                return Response.status(Response.Status.BAD_REQUEST).entity(errorBody).build();
            }

            int userId = userDAO.create(user);
            String output = "{\"userId\":" + userId + "}";
            return Response.status(Response.Status.CREATED).entity(output).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/replaceDevice")
    public Response replaceDevice(ReplaceDevice replaceDevice) {
        try {
            User oldUser = userDAO.findByDeviceId(replaceDevice.getOldDeviceId());
            User newUser = userDAO.findByDeviceId(replaceDevice.getNewDeviceId());

            if (oldUser != null && newUser != null) {
                //Inactivate new device/user
                newUser.setIsActive(false);
                newUser.setDeviceId("Deactivated_" + replaceDevice.getNewDeviceId());
                newUser.setComment("Deactivated this user and used it's deviceId for old userId - " + oldUser.getUserId());
                userDAO.update(newUser);

                //Update deviceId on old user
                oldUser.setDeviceId(replaceDevice.getNewDeviceId());
                oldUser.setIsActive(true);
                oldUser.setComment("Changed device id from old device id - " + replaceDevice.getOldDeviceId() +
                        " to new device id - " + replaceDevice.getNewDeviceId());
                userDAO.update(oldUser);
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid old or new device id").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response updateUser(User user) {
        try {
            userDAO.update(user);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@NotNull @Min(1) @PathParam("userId") int userId) {
        try {
            userDAO.delete(userId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}