package core.services;

import core.clients.RiderElectionClient;
import core.entities.DSRide;
import core.entities.DSTaxi;


public class RiderElectionService extends Thread {

    private final String SERVICE_NAME = "RIDER_ELECTION_SERVICE";

    private DSTaxi senderTaxi;
    private DSTaxi targetTaxi;
    private DSRide ride;
    private int currentBestId;
    private int currentBestBatteryLevel;
    private double distance;

    public RiderElectionService(DSTaxi senderTaxi, DSTaxi targetTaxi, DSRide ride, int currentBestId, int currentBestBatteryLevel, double distance) {
        this.senderTaxi = senderTaxi;
        this.targetTaxi = targetTaxi;
        this.ride = ride;
        this.currentBestId = currentBestId;
        this.currentBestBatteryLevel = currentBestBatteryLevel;
        this.distance = distance;
    }

    private void propagateElection() throws InterruptedException {
        RiderElectionClient client = new RiderElectionClient(targetTaxi, ride, currentBestId, currentBestBatteryLevel, distance);
        client.start();
        client.join();

        if (client.getResponse() == null) {
            senderTaxi.retryPropagation(ride, currentBestId, currentBestBatteryLevel, distance, targetTaxi.getId());
        }

        if (client.getResponse() != null && client.getResponse().getConfirmMessage().equals("BUSY")) {
            senderTaxi.retryPropagation(ride, currentBestId, currentBestBatteryLevel, distance, targetTaxi.getId());
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
