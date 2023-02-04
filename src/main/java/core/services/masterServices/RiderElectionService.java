package core.services.masterServices;

import core.clients.RideElectionNotifyClient;
import core.entities.DSRide;
import core.entities.DSTaxi;
import core.enums.TaxiState;
import core.exceptions.MakeRideException;
import utils.LogUtils;

public class RiderElectionService extends Thread {

    private final static String SERVICE_NAME = "NOTIFY_RIDE_ELECTION_SERVICE";

    private RideManagementService service;
    private DSTaxi master;
    private DSRide ride;

    public RiderElectionService(RideManagementService service, DSTaxi master, DSRide ride) {
        this.service = service;
        this.master = master;
        this.ride = ride;
    }

    /*
        Master taxi try to find some taxi than isn't busy, so can start election
    */
    public void notifyTaxi() throws InterruptedException {
        DSTaxi targetTaxi;
        int id = master.getId();
        if (master.getState() == TaxiState.FREE && master.getCurrentDistrict().getValue() == ride.getRideDistrictId()) {
            if (master.getOtherTaxis().isEmpty()) {
                try {
                    master.makeRide(ride);
                } catch (MakeRideException mre) {
                   // System.out.printf("MAKE_RIDE_EXEPTION FOR RIDE ID %d FOR ID %d ON %s%n", ride.getId(), master.getId(), LogUtils.getCurrentTS());
                }
            } else {
                master.initRideElection(ride);
            }
        } else {
            targetTaxi = master.getTaxiAfterId(id);
            id = targetTaxi.getId();
            //System.out.printf("TRYING TO ELECT TAXI ID %d AS INITIATOR FOR RIDE ID %d ON %s%n", targetTaxi.getId(), ride.getId(), LogUtils.getCurrentTS());
            while (id != master.getId()) {
                RideElectionNotifyClient client = new RideElectionNotifyClient(targetTaxi, ride, service);
                client.start();
                client.join();

                if (client.getResponse() != null && client.getResponse().getResponse().equals("OK")) {
                    //System.out.printf("TAXI ID %d ELECTED AS INITIATOR FOR RIDE ID %d ON %s%n", targetTaxi.getId(), ride.getId(), LogUtils.getCurrentTS());
                    break;
                } else {
                    targetTaxi = master.getTaxiAfterId(targetTaxi.getId());
                    id = targetTaxi.getId();
                    //System.out.printf("TRYING TO ELECT TAXI ID %d AS INITIATOR FOR RIDE ID %d ON %s%n", targetTaxi.getId(), ride.getId(), LogUtils.getCurrentTS());
                }
            }
            if (id == master.getId()) {
                notifyNonElectedRide();
            }
        }
    }


    private void notifyNonElectedRide() {
        //System.out.printf("RIDER NOT ELECTED FOR RIDE ID %d ON %s%n", ride.getId(), LogUtils.getCurrentTS());
        service.addNotElectedRideToQueue(ride);
    }

    @Override
    public void run() {
        try {
            //System.out.println(SERVICE_NAME + " STARTED");
            notifyTaxi();
        } catch (Throwable t) {
            //System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            //System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
