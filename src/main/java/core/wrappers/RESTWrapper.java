package core.wrappers;

import com.sun.jersey.api.client.*;
import rest.beans.Statistic;
import rest.beans.Taxi;
import rest.beans.Taxis;
import rest.beans.responses.AdmClientResponse;
import rest.beans.responses.TaxiResponse;
import utils.Constants;

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

    public AdmClientResponse getLastNStatisticsForTaxi(String serverAddress, int taxiID, int n) {
        AdmClientResponse admClientResponse = new AdmClientResponse();

        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "statistics/last/" + n + "|" + taxiID)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);
            if (response.getStatus() != 200) {
                // Response error
            } else {
                admClientResponse = response.getEntity(AdmClientResponse.class);
            }
        } catch (ClientHandlerException e) {
            // Server non raggiungibile
        }

        return admClientResponse;
    }

    public AdmClientResponse getAllStatisticsInTimeFrame(String serverAddress, long ts1, long ts2) {
        AdmClientResponse admClientResponse = new AdmClientResponse();
        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "statistics/timeframe/" + ts1 + "|" + ts2)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);
            if (response.getStatus() != 200) {
                // Response error
            } else {
                admClientResponse = response.getEntity(AdmClientResponse.class);
            }
        } catch (ClientHandlerException e) {
            // Server non raggiungibile
        }

        return admClientResponse;
    }

}