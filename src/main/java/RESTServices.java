import com.sun.jersey.api.client.*;
import rest.beans.Taxi;
import rest.beans.Taxis;
import rest.beans.responses.TaxiResponse;
import utils.Constants;

import javax.ws.rs.core.MediaType;
import java.util.List;

public class RESTServices {

    private final Client webClient;
    private static RESTServices instance;

    private RESTServices() {
        webClient = new Client();
    }

    public static synchronized RESTServices getInstance() {
        if (instance == null) {
            instance = new RESTServices();
        }
        return instance;
    }

    // TODO togliere ai fini del progetto finale in metodo main
    public static void main(String args[]) {

        Taxi taxi1 = getInstance().addTaxi(Constants.ADM_SERVER_ADDRESS, new Taxi(1, 111, "kek1"));
        Taxi taxi2 = getInstance().addTaxi(Constants.ADM_SERVER_ADDRESS, new Taxi(2, 112, "kek2"));
        Taxi taxi3 = getInstance().addTaxi(Constants.ADM_SERVER_ADDRESS, new Taxi(3, 113, "kek3"));
        Taxi taxi4 = getInstance().addTaxi(Constants.ADM_SERVER_ADDRESS, new Taxi(4, 114, "kek4"));

        List<Taxi> taxiList = getInstance().getTaxis(Constants.ADM_SERVER_ADDRESS);

        //boolean result = getInstance().deleteTaxi(Constants.ADM_SERVER_ADDRESS, 1);
    }

    public Taxi addTaxi(String serverAddress, Taxi taxi) {
        Taxi t = null;

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
                t = response.getEntity(Taxi.class);
            }
        } catch (ClientHandlerException e) {
            //Server non raggiungibile
        }
        return t;
    }

    public List<Taxi> getTaxis(String serverAddress) {
        List<Taxi> taxiList = null;
        try {
            ClientResponse response = webClient
                    .resource(serverAddress + "taxi/getTaxis")
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                // Qualche errore generico del server
            } else {
                taxiList = response.getEntity(Taxis.class).getTaxis();
            }
        } catch (ClientHandlerException e) {
            //Server non raggiungibile
        }
        return taxiList;
    }

    public boolean deleteTaxi(String baseServerAddress, int taxiID) {
        boolean result = false;
        try {
            ClientResponse response = webClient
                    .resource(baseServerAddress + "taxi/delete/" + taxiID)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .delete(ClientResponse.class);

            if (response.getStatus() != 200) {
                //Utils.printIfLevel(String.format("%s RESPONSE ERROR %s", outputHeader, response.getStatus()), 0);
            } else {
                // Utils.printIfLevel(String.format("%s DRONE %d EXITED SUCCESSFULLY FROM ADMINISTRATOR SERVER", outputHeader, droneId), 0);
                result = true;
            }
        } catch (ClientHandlerException e) {
            //Utils.printIfLevel(String.format("%s SERVER NOT AVAILABLE", outputHeader), 0);
        }
        return result;
    }


}