package rest.services;

import rest.beans.Statistic;
import rest.beans.Statistics;
import rest.helpers.StatisticsHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("statistics")
public class StatisticService {

    @GET
    @Path("last/{n}|{taxiId}")
    @Produces({"application/json"})
    public Response getLastNMeans(@PathParam("n") int n, @PathParam("taxiId") int taxiId) {
        return Response
                .ok(StatisticsHelper.getLastNMeans(n, Statistics.getInstance().getStatisticsByTaxi(taxiId)))
                .build();
    }

    @GET
    @Path("timeframe/{ts1}|{ts2}")
    @Produces({"application/json"})
    public Response getMeansBetweenTs(@PathParam("ts1") long ts1, @PathParam("ts2") long ts2) {
        // TODO: Not yet implemented
        return Response
                .ok(StatisticsHelper.getAllMeansInTimeFrame(ts1, ts2, Statistics.getInstance().getStatisticsMap()))
                .build();
    }

    @POST
    @Path("push/{id}")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response pushStatistics(@PathParam("id") int taxiId, Statistic statistic) {
        Statistics.getInstance().addStatistic(taxiId, statistic);
        return Response.ok().build();
    }
}
