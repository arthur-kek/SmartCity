package core.services;

import core.comunication.TaxiProtocolImpl;
import core.entities.DSTaxi;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class TaxiService extends Thread {

    private final static String SERVICE_NAME = "TAXI_SERVICE";
    private DSTaxi taxi;
    private Server taxiServer;

    public TaxiService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    private void startTaxiService() throws IOException {
        taxiServer = ServerBuilder
                .forPort(taxi.getPort())
                .addService(new TaxiProtocolImpl(taxi))
                .build();

        taxiServer.start();

        System.out.println(SERVICE_NAME + " STARTED FOR TAXI ID " + taxi.getId() + " ON PORT " + taxi.getPort());
    }

    public void exitService() throws InterruptedException {
        taxiServer.shutdownNow();
        taxiServer.awaitTermination();
    }

    @Override
    public void run() {
        try {
            startTaxiService();
        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " ERROR FOR TAXI ID " + taxi.getId() + " ON PORT " + taxi.getPort());
        }  finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }

}
