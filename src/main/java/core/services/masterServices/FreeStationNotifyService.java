package core.services.masterServices;

import core.clients.FreeStationNotifyClient;
import core.entities.DSTaxi;

public class FreeStationNotifyService extends Thread {

    private final static String SERVICE_NAME = "FREE_STATION_NOTIFY_SERVICE";

    private ChargeManagementService service;
    private DSTaxi taxi;

    public FreeStationNotifyService(ChargeManagementService service, DSTaxi taxi) {
        this.service = service;
        this.taxi = taxi;
    }

    public void sendTaxiToCharge() throws InterruptedException {
        FreeStationNotifyClient client = new FreeStationNotifyClient(service, taxi);
        client.start();
        client.join();

        if (client.getNotifyCharging() != null) {
            service.removeTaxiFromQueue(taxi.getCurrentStation().getStation().getId());
        }
    }


    @Override
    public void run() {
        try {
            //System.out.println(SERVICE_NAME + " STARTED");
            sendTaxiToCharge();
        } catch (Throwable t) {
            //System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            //System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
