package core.services.masterServices;

import core.clients.RideElectionNotifyClient;
import core.entities.DSRide;
import core.entities.DSTaxi;
import core.enums.TaxiState;

public class NotifyRideElectionService extends Thread{

    private final static String SERVICE_NAME = "NOTIFY_RIDE_ELECTION_SERVICE";

    private RideManagementService service;
    private DSTaxi master;
    private DSRide ride;

    public NotifyRideElectionService(RideManagementService service, DSTaxi master, DSRide ride) {
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
        if (master.getState() == TaxiState.FREE) {
            master.initRideElection(ride);
        } else {
            targetTaxi = master.getTaxiAfterId(id);
            while (id != master.getId()) {
                RideElectionNotifyClient client = new RideElectionNotifyClient(targetTaxi, ride, service);
                client.start();
                client.join();

                targetTaxi = master.getTaxiAfterId(id);

                if (client.getResponse() != null && client.getResponse().getResponse().equals("OK")) {
                    return;
                } else {
                    id = targetTaxi.getId();
                }
            }

            if (id == master.getId()) {
                System.out.println("RIDER NOT ELECTED");
                service.notifyNotElectedRide(ride);
            }
        }
    }


    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            notifyTaxi();
        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
