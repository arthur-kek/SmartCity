package core.wrappers;

import com.annimon.stream.Stream;
import com.sun.jersey.api.client.*;
import core.entities.DSStatistic;
import rest.beans.Statistic;
import rest.beans.Taxi;
import rest.beans.Taxis;
import rest.beans.responses.NMeansResponse;
import rest.beans.responses.TaxiResponse;
import rest.beans.responses.TimeFrameMeansResponse;
import utils.Constants;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // TODO togliere ai fini del progetto finale in metodo main
    public static void main(String args[]) {

        TaxiResponse taxi1 = getInstance().addTaxi(Constants.ADM_SERVER_ADDRESS, new Taxi(1, 111, "kek1"));
        TaxiResponse taxi2 = getInstance().addTaxi(Constants.ADM_SERVER_ADDRESS, new Taxi(2, 112, "kek2"));
        TaxiResponse taxi3 = getInstance().addTaxi(Constants.ADM_SERVER_ADDRESS, new Taxi(3, 113, "kek3"));
        TaxiResponse taxi4 = getInstance().addTaxi(Constants.ADM_SERVER_ADDRESS, new Taxi(4, 114, "kek4"));

        List<Taxi> taxiList = getInstance().getTaxis(Constants.ADM_SERVER_ADDRESS);

        //boolean result = getInstance().deleteTaxi(Constants.ADM_SERVER_ADDRESS, 1);
    }

    public TaxiResponse addTaxi(String serverAddress, Taxi taxi) {
        TaxiResponse taxiResponse = new TaxiResponse();

        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "taxi/add")
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, taxi);

            if (response.getStatus() == 304) {
                // Taxi con lo stesso ID esiste
            } else if (response.getStatus() != 200) {
                // Qualche errore generico del server
            } else {
                taxiResponse = response.getEntity(TaxiResponse.class);
            }
        } catch (ClientHandlerException e) {
            //Server non raggiungibile
        }
        return taxiResponse;
    }

    public List<Taxi> getTaxis(String serverAddress) {
        List<Taxi> taxiList = new ArrayList<>();

        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "taxi/getTaxis")
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                // Qualche errore generico del server
            } else {
                taxiList = response.getEntity(Taxis.class).getTaxisList();
            }
        } catch (ClientHandlerException e) {
            //Server non raggiungibile
        }
        return taxiList;
    }

    public boolean deleteTaxi(String serverAddress, int taxiID) {
        boolean result = false;

        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "taxi/delete/" + taxiID)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                // Response error
            } else {
                // Success
                result = true;
            }
        } catch (ClientHandlerException e) {
            // Server non raggiungibile
        }
        return result;
    }

    public boolean pushStatistics(String serverAddress, int taxiID, Statistic statistic) {
        boolean result = false;

        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "statistics/push/" + taxiID)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, statistic);

            if (response.getStatus() != 200) {
                // Response error
            } else {
                // Success
                result = true;
            }
        } catch (ClientHandlerException e) {
            // Server non raggiungibile
        }
        return result;
    }

    public List<DSStatistic> getLastNStatisticsForTaxi(String serverAddress, int taxiID, int n) {
        List<DSStatistic> statistics = new ArrayList<>();

        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "statistics/last/" + n + "|" + taxiID)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);
            if (response.getStatus() != 200) {
                // Response error
            } else {
                statistics = Stream.of(response.getEntity(NMeansResponse.class).getStatisticsResponse())
                        .map(DSStatistic::new)
                        .toList();
            }
        } catch (ClientHandlerException e) {
            // Server non raggiungibile
        }

        return statistics;
    }

    public Map<Integer, DSStatistic> getAllStatisticsInTimeFrame(String serverAddress, long ts1, long ts2) {
        Map<Integer, DSStatistic> statisticMap = new HashMap<>();
        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "statistics/timeframe/" + ts1 + "|" + ts2)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);
            if (response.getStatus() != 200) {
                // Response error
            } else {
                Stream.of(response.getEntity(TimeFrameMeansResponse.class))
                        .map(r -> statisticMap.put(r.getTaxiId(), new DSStatistic(r.getAverageStatistic())));
            }
        } catch (ClientHandlerException e) {
            // Server non raggiungibile
        }

        return statisticMap;
    }

}