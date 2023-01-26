package core.services.masterServices;

import core.comunication.ServiceProtocolImpl;
import core.entities.DSRide;
import core.entities.DSTaxi;
import core.extensions.MapQueue;
import core.wrappers.RidesQueue;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import utils.Constants;
import utils.PositionUtils;

import java.io.IOException;

public class RideManagementService extends Thread {

    private final static String SERVICE_NAME = "RIDE_MANAGEMENT_SERVICE";

    private Server rideServer;
    private MasterService masterService;
    private DSRide rideOnFirstTopic;
    private DSRide rideOnSecondTopic;
    private DSRide rideOnThirdTopic;
    private DSRide rideOnFourthTopic;

    public RideManagementService(MasterService service) {
        this.masterService = service;
    }

    private volatile boolean quitting;
    public void startCommunicationServer() throws IOException {
        rideServer = ServerBuilder
                .forPort(Constants.RIDE_MANAGER_DEFAULT_PORT)
                .addService(new ServiceProtocolImpl(this))
                .build();

        rideServer.start();

        System.out.println(SERVICE_NAME + " STARTED FOR MASTER");
    }

    private MapQueue<DSRide> getRideMap() {
        return RidesQueue.getInstance().getRidesMap();
    }

    public void checkRideMap() throws InterruptedException {
        if (rideOnFirstTopic == null && getRideMap().getFromQueue(1) != null) {
            rideOnFirstTopic = getRideMap().getFromQueue(1);
            NotifyRideElectionService service = new NotifyRideElectionService(this, masterService.getTaxi(), rideOnFirstTopic);
            service.start();
            System.out.println("RIDE NOTIFIED");
        }

        if (rideOnSecondTopic == null && getRideMap().getFromQueue(2) != null) {
            rideOnSecondTopic = getRideMap().getFromQueue(2);
            NotifyRideElectionService service = new NotifyRideElectionService(this, masterService.getTaxi(), rideOnSecondTopic);
            service.start();
            System.out.println("RIDE NOTIFIED");
        }

        if (rideOnThirdTopic == null && getRideMap().getFromQueue(3) != null) {
            rideOnThirdTopic = getRideMap().getFromQueue(3);
            NotifyRideElectionService service = new NotifyRideElectionService(this, masterService.getTaxi(), rideOnThirdTopic);
            service.start();
            System.out.println("RIDE NOTIFIED");
        }

        if (rideOnFourthTopic == null && getRideMap().getFromQueue(4) != null) {
            rideOnFourthTopic = getRideMap().getFromQueue(4);
            NotifyRideElectionService service = new NotifyRideElectionService(this, masterService.getTaxi(), rideOnFourthTopic);
            service.start();
            System.out.println("RIDE NOTIFIED");
        }

    }

    public String removeRideFromQueue(int idQueue, DSRide ride) {
        switch (idQueue) {
            case 1:
                if (getRideMap().getFromQueue(1) != null && getRideMap().getFromQueue(1).getId() == ride.getId()) {
                    getRideMap().removeFromQueue(1);
                    rideOnFirstTopic = null;
                } else {
                    System.out.println("ERROR NOTIFYING RIDE REMOVE");
                    return "ERROR";
                }
                break;
            case 2:
                if (getRideMap().getFromQueue(2) != null && getRideMap().getFromQueue(2).getId() == ride.getId()) {
                getRideMap().removeFromQueue(2);
                rideOnSecondTopic = null;
                } else {
                    System.out.println("ERROR NOTIFYING RIDE REMOVE");
                    return "ERROR";
                }
                break;
            case 3:
                if (getRideMap().getFromQueue(3) != null && getRideMap().getFromQueue(3).getId() == ride.getId()) {
                getRideMap().removeFromQueue(3);
                rideOnThirdTopic = null;
                } else {
                    System.out.println("ERROR NOTIFYING RIDE REMOVE");
                    return "ERROR";
                }
                break;
            case 4:
                if (getRideMap().getFromQueue(4) != null && getRideMap().getFromQueue(4).getId() == ride.getId()) {
                getRideMap().removeFromQueue(4);
                rideOnFourthTopic = null;
                } else {
                    System.out.println("ERROR NOTIFYING RIDE REMOVE");
                    return "ERROR";
                }
                break;

        }
        return "OK";
    }

    /*
        Il rider is not elected this service will retry on next cycle
    */
    public void notifyNotElectedRide(DSRide ride) {
        int topic = PositionUtils.getTopicIdByPosition(ride.getStart());
        switch (topic) {
            case 1:
                rideOnFirstTopic = null;
                break;
            case 2:
                rideOnSecondTopic = null;
                return;
            case 3:
                rideOnThirdTopic = null;
                break;
            case 4:
                rideOnFourthTopic = null;
                break;
        }
    }

    /*
        Each time new ride is added this server cycles all four topics and check if there is any ride on the queue, if there is any ride service starts election
    */
    public void addRideToQueue(DSRide ride, int topic) {
        System.out.printf("%s RECEIVED NEW RIDE ID %d%n", SERVICE_NAME, ride.getId());
        getRideMap().addToQueue(topic, ride);
        try {
            checkRideMap();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public synchronized void quitService() {
        quitting = true;
    }

    private void startAll() throws IOException, InterruptedException {
        startCommunicationServer();
        checkRideMap();
    }

    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            startAll();
        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
