package rest.services;

import rest.beans.Position;
import rest.beans.Taxi;
import rest.beans.Taxis;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("taxi")
public class TaxiServiceImpl {

    @GET
    @Path("getTaxis")
    @Produces({"application/json"})
    public Response getTaxisList(){
        return Response.ok(Taxis.getInstance()).build();
    }

    @POST
    @Path("add")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response addTaxi(Taxi t) {
        if(Taxis.getInstance().isAlreadyPresent(t.getId())) {
            return Response.notModified().build();
        }
        t.setPosition(Position.getRandomSpawnPosition());
        Taxis.getInstance().add(t);
        return Response.ok(t).build();
    }

    @GET
    @Path("get/{id}")
    @Produces({"application/json"})
    public Response getTaxi(@PathParam("id") int id) {
        return Response.ok(Taxis.getInstance().get(id)).build();
    }

    @DELETE
    @Path("delete/{id}")
    public Response deleteTaxi(@PathParam("id") int id) {
        return Response.ok(Taxis.getInstance().delete(id)).build();
    }

}
