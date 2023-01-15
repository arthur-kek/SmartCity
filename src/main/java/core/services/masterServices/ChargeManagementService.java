package core.services.masterServices;

import core.comunication.ServiceProtocolImpl;
import core.entities.DSTaxi;
import core.extensions.MapQueue;
import core.wrappers.ChargeQueue;
import core.wrappers.ChargingStations;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import utils.Constants;

import java.io.IOException;

public class ChargeManagementService extends Thread {

    private final static String SERVICE_NAME = "CHARGE_MANAGEMENT_SERVICE";
    private Server chargeServer;
    private MasterService masterService;


    private ChargingStations chargingStations;

    private FreeStationNotifyService freeStationNotifyService;

    private volatile boolean quitting;

    public ChargeManagementService(MasterService masterService) {
        this.masterService = masterService;
        this.chargingStations = new ChargingStations();
        this.quitting = false;
    }

    public synchronized void quitService() {
        quitting = true;
    }

    private MapQueue<DSTaxi> getChargeMap() {
        return ChargeQueue.getInstance().getChargeMap();
    }

    public void startCommunicationServer() throws IOException {
        chargeServer = ServerBuilder
                .forPort(Constants.CHARGE_MANAGER_DEFAULT_PORT)
                .addService(new ServiceProtocolImpl(this))
                .build();

        chargeServer.start();

        System.out.println(SERVICE_NAME + " STARTED FOR MASTER");
    }

    /*
        Method cycles on charging stations, checking if there is a free station, so it sends taxi to charge
    */
    public void startChargeManagement() throws InterruptedException {
        while (!quitting) {

            if (!chargingStations.isFirstStationBusy()) {
                DSTaxi taxiToCharge = getChargeMap().getFromQueue(1);
                if (taxiToCharge != null) {
                    sendTaxiToCharge(taxiToCharge);
                }
            }

            if (!chargingStations.isSecondStationBusy()) {
                DSTaxi taxiToCharge = getChargeMap().getFromQueue(2);
                if (taxiToCharge != null) {
                    sendTaxiToCharge(taxiToCharge);
                }
            }

            if (!chargingStations.isThirdStationBusy()) {
                DSTaxi taxiToCharge = getChargeMap().getFromQueue(3);
                if (taxiToCharge != null) {
                    sendTaxiToCharge(taxiToCharge);
                }
            }

            if (!chargingStations.isFourthStationBusy()) {
                DSTaxi taxiToCharge = getChargeMap().getFromQueue(4);
                if (taxiToCharge != null) {
                    sendTaxiToCharge(taxiToCharge);
                }
            }

        }
    }

    /*
        If taxi, after being notified, respond ok to charge notify, this method removes taxi from charge queue
    */
    public void removeTaxiFromQueue(int idQueue) {
        getChargeMap().removeFromQueue(idQueue);
    }

    /*
        Receive grpc request to charge from taxi, if the queue is empty the method sens taxi immediately to charge,
        otherwise it adds taxi to charge queue
    */
    public String addTaxiToChargeQueue(DSTaxi taxi) {
        System.out.printf("%s RECEIVED RECHARGE REQUEST FROM %d%n", SERVICE_NAME, taxi.getId());
        if (getChargeMap().getFromQueue(taxi.getCurrentStation().getStation().getId()) == null) {
            return "OK";
        }
        getChargeMap().addToQueue(taxi.getCurrentStation().getStation().getId(), taxi);
        return "WAIT";
    }

    /*
        Communicates to taxi that the station of it's district is ready to be used
    */
    public void sendTaxiToCharge(DSTaxi taxi) throws InterruptedException {
        freeStationNotifyService = new FreeStationNotifyService(this, taxi);
        freeStationNotifyService.start();
        freeStationNotifyService.join();
    }

    private void startAll() throws IOException, InterruptedException {
        startCommunicationServer();
        startChargeManagement();
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
