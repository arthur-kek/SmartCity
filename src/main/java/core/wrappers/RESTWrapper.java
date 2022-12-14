package core.wrappers;

import com.sun.jersey.api.client.*;
import rest.beans.Statistic;
import rest.beans.Taxi;
import rest.beans.Taxis;
import rest.beans.responses.AdmClientResponse;
import rest.beans.responses.TaxiResponse;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class RESTWrapper {

    private final Client webClient;
    private static RESTWrapper instance;

    private RESTWrapper() {
        webClient = new Client();
    }

    public static synchronized RESTWrapper getInstance() {
        if (instance == null) {
            instance = new RESTWrapper();
        }
        return instance;
    }

    public TaxiResponse addTaxi(String serverAddress, Taxi taxi) {
        TaxiResponse taxiResponse = new TaxiResponse();
        System.out.printf("ADDING TAXI WITH ID %d TO THE NETWORK%n", taxi.getId());
        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "taxi/add")
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, taxi);

            if (response.getStatus() == 304) {
                System.out.printf("TAXI WITH ID %d IS ALREADY PRESENT%n", taxi.getId());
            } else if (response.getStatus() != 200) {
                System.out.printf("GENERIC ERROR DURING ADDING TAXI WITH ID %d TO THE NETWORK%n", taxi.getId());
            } else {
                taxiResponse = response.getEntity(TaxiResponse.class);
            }
        } catch (ClientHandlerException e) {
            System.out.println("SERVER IS UNREACHABLE");
        }
        return taxiResponse;
    }

    public List<Taxi> getTaxis(String serverAddress) {
        List<Taxi> taxiList = new ArrayList<>();
        System.out.printf("TRYING TO GET ALL TAXIS FROM %s%n", serverAddress);
        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "taxi/getTaxis")
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                System.out.printf("TRYING TO GET ALL TAXIS FROM %s GENERIC ERROR%n", serverAddress);
            } else {
                taxiList = response.getEntity(Taxis.class).getTaxisList();
            }
        } catch (ClientHandlerException e) {
            System.out.println("SERVER IS UNREACHABLE");
        }
        return taxiList;
    }

    public boolean deleteTaxi(String serverAddress, int taxiID) {
        boolean result = false;
        System.out.printf("DELETING TAXI WITH ID %d FROM THE NETWORK%n", taxiID);
        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "taxi/delete/" + taxiID)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                System.out.printf("DELETING TAXI WITH ID %d FROM THE NETWORK GENERIC ERROR%n", taxiID);
            } else {
                System.out.printf("TAXI WITH ID %d IS DELETED FROM THE NETWORK%n", taxiID);
                result = true;
            }
        } catch (ClientHandlerException e) {
            System.out.println("SERVER IS UNREACHABLE");
        }
        return result;
    }

    public boolean pushStatistics(String serverAddress, int taxiID, Statistic statistic) {
        boolean result = false;
        System.out.printf("PUSHING STATISTICS FOR %d ON %s%n", taxiID, serverAddress);
        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "statistics/push/" + taxiID)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, statistic);

            if (response.getStatus() != 200) {
                System.out.printf("PUSHING STATISTICS FOR %d ON %s GENERIC ERROR%n", taxiID, serverAddress);
            } else {
                System.out.printf("PUSHING STATISTICS FOR %d ON %s IS COMPLETED%n", taxiID, serverAddress);
                result = true;
            }
        } catch (ClientHandlerException e) {
            System.out.println("SERVER IS UNREACHABLE");
        }
        return result;
    }

    public AdmClientResponse getLastNStatisticsForTaxi(String serverAddress, int taxiID, int n) {
        AdmClientResponse admClientResponse = new AdmClientResponse();
        System.out.printf("TRYING TO GET LAST %d OF TAXI ID %d FROM %s%n", n, taxiID, serverAddress);
        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "statistics/last/" + n + "|" + taxiID)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);
            if (response.getStatus() != 200) {
                System.out.printf("TRYING TO GET LAST %d OF TAXI ID %d FROM %s GENERIC ERROR%n", n, taxiID, serverAddress);
            } else {
                admClientResponse = response.getEntity(AdmClientResponse.class);
            }
        } catch (ClientHandlerException e) {
            System.out.println("SERVER IS UNREACHABLE");
        }

        return admClientResponse;
    }

    public AdmClientResponse getAllStatisticsInTimeFrame(String serverAddress, long ts1, long ts2) {
        AdmClientResponse admClientResponse = new AdmClientResponse();
        System.out.printf("TRYING TO GET ALL STATISTICS BETWEEN TS %d AND TS %d FROM %s%n", ts1, ts2, serverAddress);
        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "statistics/timeframe/" + ts1 + "|" + ts2)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);
            if (response.getStatus() != 200) {
                System.out.printf("TRYING TO GET ALL STATISTICS BETWEEN TS %d AND TS %d FROM %s GENERIC ERROR%n", ts1, ts2, serverAddress);
            } else {
                admClientResponse = response.getEntity(AdmClientResponse.class);
            }
        } catch (ClientHandlerException e) {
            System.out.println("SERVER IS UNREACHABLE");
        }

        return admClientResponse;
    }

}