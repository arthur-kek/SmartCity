package core.services;

import com.annimon.stream.Stream;
import core.clients.HelloClient;
import core.entities.DSTaxi;
import grpc.protocols.TaxiProtocolOuterClass;

import java.util.ArrayList;
import java.util.List;


public class HelloService extends Thread {

    private final static String SERVICE_HEADER = "HELLO_SERVICE";

    private DSTaxi taxi;

    public HelloService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    public void sayHelloInBroadcast() throws InterruptedException {
        List<HelloClient> helloClients = new ArrayList<>();

        for (DSTaxi t : taxi.getOtherTaxis()) {
            HelloClient client = new HelloClient(taxi, t);
            helloClients.add(client);
            client.start();
        }

        for (HelloClient hc : helloClients) {
            hc.join();
        }

        System.out.println("OTHER TAXIS HELLO RESPONSES:");
        Stream.of(helloClients)
                .forEach(helloClient ->
                        printResponse(helloClient.getHelloResponse(),
                                helloClient.getOtherTaxi().getId()));
    }

    @Override
    public void run() {
        try {
            System.out.println(SERVICE_HEADER + " STARTED");
            sayHelloInBroadcast();

        } catch (InterruptedException ie) {
            System.out.println(SERVICE_HEADER + " INTERRUPTED");
            ie.printStackTrace();
        } catch (Throwable t) {
            System.out.println(SERVICE_HEADER + " GENERIC ERROR");
            t.printStackTrace();
        }
    }

    private void printResponse(TaxiProtocolOuterClass.HelloResponse response, int taxiId) {
        if (response != null) {
            String message = String.format("\nThe response from taxi id: %d is: " + response.getMessage(), taxiId);
            System.out.println(message);
        }
    }
}
