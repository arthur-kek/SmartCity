package rest.services;

import rest.beans.Position;
import rest.beans.Statistic;
import rest.beans.Statistics;
import rest.helpers.StatisticsHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("statistics")
public class StatisticService {

    @GET
    @Path("last/{n}-{taxiId}")
    @Produces({"application/json"})
    public Response getLastNMeans(@PathParam("n") int n, @PathParam("taxiId") int taxiId) {
        List<Statistic> statistics = Statistics.getInstance().getStatisticsByTaxi(taxiId);
        if (statistics.isEmpty()) {
            return Response.notModified().build();
        }
        return Response
                .ok(StatisticsHelper.getLastNMeans(n, statistics))
                .build();

    }

    @GET
    @Path("timeframe/{ts1}-{ts2}")
    @Produces({"application/json"})
    public Response getMeansBetweenTs(@PathParam("ts1") long ts1, @PathParam("ts2") long ts2) {
        if (Statistics.getInstance().getStatisticsMap().isEmpty()) {
            return Response.notModified().build();
        }
        return Response
                .ok(StatisticsHelper.getAllMeansInTimeFrame(ts1, ts2, Statistics.getInstance().getStatisticsMap()))
                .build();
    }

    @POST
    @Path("push/{id}")
    @Consumes({"application/json"})
    public Response pushStatistics(@PathParam("id") int taxiId, Statistics statistic) {
        //Statistics.getInstance().addStatistic(taxiId, statistic);
        System.out.println("RECEIVED NEW STATISTICS FROM TAXI ID " + taxiId + "\n");
        System.out.println(statistic.toString());
        return Response.ok().build();
    }
}
