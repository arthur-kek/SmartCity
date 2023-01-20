package core.services.masterServices;

import core.comunication.ServiceProtocolImpl;
import core.entities.DSRide;
import core.entities.DSTaxi;
import core.extensions.MapQueue;
import core.wrappers.RidesQueue;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import utils.Constants;

import java.io.IOException;

public class RideManagementService extends Thread {

    private final static String SERVICE_NAME = "RIDE_MANAGEMENT_SERVICE";

    private Server rideServer;
    private MasterService masterService;

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

    public void startRideManagement() throws InterruptedException {
        while (!quitting) {



        }
    }


    /*
        If taxi, after being notified, respond ok to charge notify, this method removes taxi from charge queue
    */
    public void removeRideFromQueue(int idQueue) {
        getRideMap().removeFromQueue(idQueue);
    }

    /*
        Receive grpc request to charge from taxi, if the queue is empty the method sens taxi immediately to charge,
        otherwise it adds taxi to charge queue
    */
    public String addRideToQueue(DSRide ride, int topic) {
        System.out.printf("%s RECEIVED NEW RIDE ID %d%n", SERVICE_NAME, ride.getId());
        getRideMap().addToQueue(topic, ride);
        return "OK";
    }

    public synchronized void quitService() {
        quitting = true;
    }

    private void startAll() throws IOException, InterruptedException {
        startCommunicationServer();
        startRideManagement();
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
