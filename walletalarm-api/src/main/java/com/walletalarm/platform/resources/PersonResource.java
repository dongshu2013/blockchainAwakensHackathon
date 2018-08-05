package com.walletalarm.platform.resources;

import com.codahale.metrics.annotation.Timed;
import com.walletalarm.platform.core.Person;
import com.walletalarm.platform.db.dao.PersonDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    private final PersonDAO personDAO;

    public PersonResource(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @GET
    @Timed
    @Path("/{id}")
    public Response getPersonByID(@PathParam("id") long id) {
        try {
            return Response.status(Response.Status.OK).entity(personDAO.findById(id)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createPerson(Person person) {
        try {
            return Response.status(Response.Status.OK).entity(personDAO.create(person)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response updatePerson(Person person) {
        try {
            personDAO.update(person);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
