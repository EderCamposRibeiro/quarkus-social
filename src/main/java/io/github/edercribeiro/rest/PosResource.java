package io.github.edercribeiro.rest;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PosResource {

    @POST
    @Transactional
    public Response savePost(){
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPost(){
        return Response.ok().build();
    }
}
