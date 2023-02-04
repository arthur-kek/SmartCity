package core.services.masterServices;

import core.comunication.ServiceProtocolImpl;
import core.entities.DSTaxi;
import core.entities.DSTaxiOrdered;
import core.extensions.MapQueue;
import core.wrappers.ChargeQueue;
import core.wrappers.ChargingStations;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import utils.Constants;
import utils.TaxisUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Random;

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

    public void quitService() {
        quitting = true;
    }

    private ChargeQueue getChargeQueue() {
        return ChargeQueue.getInstance();
    }

    public void startCommunicationServer() throws IOException {
        chargeServer = ServerBuilder
                .forPort(Constants.CHARGE_MANAGER_DEFAULT_PORT)
                .addService(new ServiceProtocolImpl(this))
                .build();

        chargeServer.start();

        //System.out.println(SERVICE_NAME + " STARTED FOR MASTER");
    }

    /*
        Method cycles on charging stations, checking if there is a free station, so it sends taxi to charge
    */
    public void startChargeManagement() throws InterruptedException {
        while (!quitting) {

            if (!chargingStations.isFirstStationBusy()) {
                DSTaxi taxiToCharge = getChargeQueue().getFromQueue(1);
                if (taxiToCharge != null) {
                    sendTaxiToCharge(taxiToCharge);
                }
            }

            if (!chargingStations.isSecondStationBusy()) {
                DSTaxi taxiToCharge = getChargeQueue().getFromQueue(2);
                if (taxiToCharge != null) {
                    sendTaxiToCharge(taxiToCharge);
                }
            }

            if (!chargingStations.isThirdStationBusy()) {
                DSTaxi taxiToCharge = getChargeQueue().getFromQueue(3);
                if (taxiToCharge != null) {
                    sendTaxiToCharge(taxiToCharge);
                }
            }

            if (!chargingStations.isFourthStationBusy()) {
                DSTaxi taxiToCharge = getChargeQueue().getFromQueue(4);
                if (taxiToCharge != null) {
                    sendTaxiToCharge(taxiToCharge);
                }
            }

            waitABit();

        }
    }

    private synchronized void waitABit() throws InterruptedException {
        int offset = new Random().nextInt(1001 - 100) + 100;
        wait(Constants.WAIT_TIME_UNTIL_CHECK_CHARGE_QUEUE + offset);
    }

    /*
        If taxi, after being notified, respond ok to charge notify, this method removes taxi from charge queue
    */
    public void removeTaxiFromQueue(int idQueue) {
        getChargeQueue().removeTaxiFromQueue(idQueue);
    }

    /*
        Receive grpc request to charge from taxi, if the queue is empty the method sens taxi immediately to charge,
        otherwise it adds taxi to charge queue
        if taxi's clock is broken so method try to align it by confronting it with his own, then inserts taxi to queue ordered by ts of request of other taxis
    */
    public String addTaxiToChargeQueue(DSTaxi taxi, String timestamp) {
        //System.out.printf("%s RECEIVED RECHARGE REQUEST FROM %d%n", SERVICE_NAME, taxi.getId());
        if (getChargeQueue().getFromQueue(taxi.getCurrentStation().getStation().getId()) == null) {
            return "OK";
        }
        String response = "";
        try {
            long offset = getOffset(timestamp);
            long taxiMillis = TaxisUtils.castTsToLong(timestamp);
            getChargeQueue().addToQueue(taxi.getCurrentStation().getStation().getId(), taxi, taxiMillis + offset);
            response = String.valueOf(offset);
        } catch (ParseException pe) {
            System.out.println("PARSE ERROR");
        }
        return response;
    }

    private long getOffset(String ts) {
        try {
            return TaxisUtils.calculateOffset(ts, System.currentTimeMillis());
        } catch (ParseException pe) {
            System.out.println("PARSE ERROR");
        }
        return 0;
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
