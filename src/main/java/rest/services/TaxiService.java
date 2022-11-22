package rest.services;

import rest.beans.Position;
import rest.beans.Taxi;
import rest.beans.Taxis;
import rest.beans.responses.TaxiResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("taxi")
public class TaxiService {

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
        TaxiResponse response = new TaxiResponse();
        if(Taxis.getInstance().isAlreadyPresent(t.getId())) {
            return Response.notModified().build();
        }
        t.setPosition(Position.getRandomSpawnPosition());
        response.setTaxi(t);
        response.setOtherTaxis(Taxis.getInstance().getTaxisList());
        Taxis.getInstance().add(t);
        return Response.ok(response).build();
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
