package core.services.masterServices;

import core.entities.DSTaxi;

public class MasterService extends Thread {

    private final static String SERVICE_NAME = "MASTER_SERVICE";

    private ChargeManagementService chargeManagementService;
    private RideManagementService rideManagementService;
    private DSTaxi taxi;

    public MasterService(DSTaxi taxi) {
        this.taxi = taxi;
    }


    private void startAllServices() {
        startRideManagementService();
        startChargeManagementService();
    }

    private void startChargeManagementService() {
        chargeManagementService = new ChargeManagementService(this);
        chargeManagementService.start();
    }

    private void quitChargeManagementService() throws InterruptedException {
        chargeManagementService.quitService();
        chargeManagementService.join();
    }

    private void startRideManagementService() {
        rideManagementService = new RideManagementService(this);
        rideManagementService.start();
    }

    private void quitRideManagementService() throws InterruptedException {
        rideManagementService.quitService();
        rideManagementService.join();
    }

    public void quit() {
        try {
            quitChargeManagementService();
            quitRideManagementService();
            System.exit(0);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            startAllServices();
        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }

    public DSTaxi getTaxi() {
        return taxi;
    }
}
