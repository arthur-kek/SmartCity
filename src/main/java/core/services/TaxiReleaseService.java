package core.services;

import core.clients.ReleaseTaxiClient;
import core.entities.DSTaxi;
import utils.TaxisUtils;

import java.util.ArrayList;
import java.util.List;

public class TaxiReleaseService extends Thread {

    private final static String SERVICE_NAME = "TAXI_RELEASE_SERVICE";
    private DSTaxi taxi;
    private String participants;

    public TaxiReleaseService(DSTaxi taxi, String participants) {
        this.taxi = taxi;
        this.participants = participants;
    }

    private void releaseTaxis() throws InterruptedException {

        List<DSTaxi> taxiToRelease = TaxisUtils.getTaxisToRelease(participants, taxi.getOtherTaxis());

        if (taxiToRelease.isEmpty()) {
            return;
        }

        List<ReleaseTaxiClient> clients = new ArrayList<>();
        for (DSTaxi t : taxiToRelease) {
            if (t != null) {
                ReleaseTaxiClient client = new ReleaseTaxiClient(taxi, t);
                clients.add(client);
                client.start();
            }
        }

        for (ReleaseTaxiClient rtc : clients) {
            rtc.join();
        }

    }

    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            releaseTaxis();
        } catch (InterruptedException ie) {
            System.out.println(SERVICE_NAME + " INTERRUPTED");
            ie.printStackTrace();
        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
