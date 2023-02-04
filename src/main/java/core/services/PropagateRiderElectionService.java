package core.services;

import core.clients.RiderElectionClient;
import core.entities.DSRide;
import core.entities.DSTaxi;
import core.exceptions.MakeRideException;
import utils.LogUtils;


public class PropagateRiderElectionService extends Thread {

    private final String SERVICE_NAME = "RIDER_ELECTION_SERVICE";

    private DSTaxi senderTaxi;
    private DSTaxi targetTaxi;
    private DSRide ride;
    private int currentBestId;
    private int currentBestBatteryLevel;
    private double distance;
    private String participants;

    public PropagateRiderElectionService(DSTaxi senderTaxi, DSTaxi targetTaxi, DSRide ride, int currentBestId, int currentBestBatteryLevel, double distance, String participants) {
        this.senderTaxi = senderTaxi;
        this.targetTaxi = targetTaxi;
        this.ride = ride;
        this.currentBestId = currentBestId;
        this.currentBestBatteryLevel = currentBestBatteryLevel;
        this.distance = distance;
        this.participants = participants;
    }

    /*
        This service try to propagate election to next taxi
            - if it responds OK so propagation is finished
            - if it doesn't respond so
    */
    private void propagateElection() throws InterruptedException {
        RiderElectionClient client = new RiderElectionClient(targetTaxi, ride, currentBestId, currentBestBatteryLevel, distance, participants);
        client.start();
        client.join();

        if (client.getResponse() == null || !client.getResponse().getConfirmMessage().equals("OK")) {
            DSTaxi nextTaxi = senderTaxi.getTaxiAfterId(targetTaxi.getId());

            String response = "UNDEFINED";
            do {
                client = new RiderElectionClient(nextTaxi, ride, currentBestId, currentBestBatteryLevel, distance, participants);
                client.start();
                client.join();

                if (client.getResponse() == null) {
                    nextTaxi = senderTaxi.getTaxiAfterId(nextTaxi.getId());
                } else {
                    response = client.getResponse().getConfirmMessage();
                    if (response.equals("BUSY")) {
                        nextTaxi = senderTaxi.getTaxiAfterId(nextTaxi.getId());
                    }
                }

                if (nextTaxi == senderTaxi) {
                    //System.out.printf("PROPAGATE ELECTION CYCLE FOR RIDE ID %d FINISHED WITH SENDER ID %d AS BEST RIDER%n", ride.getId(), senderTaxi.getId());
                    try {
                        senderTaxi.makeRide(ride);
                        break;
                    } catch (MakeRideException mre) {
                        //System.out.printf("MAKE_RIDE_EXEPTION FOR RIDE ID %d FOR ID %d ON %s%n", ride.getId(), senderTaxi.getId(), LogUtils.getCurrentTS());
                    }
                }
            } while (!response.equals("OK"));
        }
    }

    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            propagateElection();
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
