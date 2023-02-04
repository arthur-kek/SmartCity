package core.services.masterServices;

import core.comunication.ServiceProtocolImpl;
import core.entities.DSRide;
import core.wrappers.RidesQueue;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import utils.Constants;

import java.io.IOException;
import java.util.Random;

public class RideManagementService extends Thread {

    private final static String SERVICE_NAME = "RIDE_MANAGEMENT_SERVICE";

    private Server rideServer;
    private MasterService masterService;

    public RideManagementService(MasterService service) {
        this.masterService = service;
    }

    private volatile boolean quitting;

    private void startCommunicationServer() throws IOException {
        rideServer = ServerBuilder
                .forPort(Constants.RIDE_MANAGER_DEFAULT_PORT)
                .addService(new ServiceProtocolImpl(this))
                .build();

        rideServer.start();

        //System.out.println(SERVICE_NAME + " STARTED FOR MASTER");
    }

    private RidesQueue getRideMap() {
        return RidesQueue.getInstance();
    }

    private synchronized void waitABit() throws InterruptedException {
        int offset = new Random().nextInt(1001 - 100) + 100;
        wait(Constants.WAIT_TIME_UNTIL_CHECK_RIDES_QUEUE + offset);
    }

    private void checkRideMap() throws InterruptedException {
        while (!quitting) {

            for (int i = 1; i < 5; i++) {
                DSRide rideToNotify = getRideMap().getAndRemoveFromQueue(i);

                if (rideToNotify != null) {
                    RiderElectionService service = new RiderElectionService(this, masterService.getTaxi(), rideToNotify);
                    service.start();
                    service.join();
                    //System.out.printf("RIDE ID %d NOTIFIED%n", rideToNotify.getId());
                    waitABit();
                }
            }

        }
    }

    public synchronized String addRideToQueue(DSRide ride, int topic) {
        String resp = getRideMap().addToQueue(topic, ride);
        //System.out.printf("%s RECEIVED NEW RIDE ID %d, RESULT OF INSERTION IS: %s%n", SERVICE_NAME, ride.getId(), resp);

        return resp;
    }

    public synchronized void addNotElectedRideToQueue(DSRide ride) {
        getRideMap().reinsertNotElectedRide(ride.getRideDistrictId(), ride);
        //System.out.printf("NOT ELECTED RIDE ID %d, WAS REINSERTED TO THE QUEUE: %n", ride.getId());
    }

    public void quitService() {
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
