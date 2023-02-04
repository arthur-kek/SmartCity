package core.services;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import core.clients.HelloClient;
import core.clients.PingClient;
import core.entities.DSTaxi;
import core.wrappers.RESTWrapper;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class PingService extends Thread {

    private final static String SERVICE_NAME = "PING_SERVICE";
    private volatile boolean quitting;
    private DSTaxi taxi;

    RESTWrapper instance;

    public PingService(DSTaxi t) {
        this.taxi = t;
        instance = RESTWrapper.getInstance();
    }

    private void startPinging() throws InterruptedException {

        while (!quitting) {
            if (!taxi.getOtherTaxis().isEmpty()) {

                List<PingClient> clients = new ArrayList<>();
                for (DSTaxi t : taxi.getOtherTaxis()) {
                    PingClient client = new PingClient(taxi, t);
                    clients.add(client);
                    client.start();
                }

                for (PingClient pc : clients) {
                    pc.join();
                }

                Stream.of(clients)
                        .forEach(pingClient -> {
                            if (pingClient.getPingResponse() == null) {
                                int id = pingClient.getOtherTaxi().getId();
                                System.out.printf("FOUND DEAD TAXI, ID: %d%n", id);
                                instance.deleteTaxi(Constants.ADM_SERVER_ADDRESS, id);
                                taxi.removeDeadTaxi(id);
                            }
                        });

                if (!taxi.isMaster()) {
                    Optional<DSTaxi> opt = Stream.of(taxi.getOtherTaxis())
                            .filter(DSTaxi::isMaster)
                            .findFirst();

                    if (opt.isEmpty()) {
                        taxi.electMaster();
                    }
                }
            }

            sleep(Constants.PING_SLEEP_TIME);
        }
    }

    public void quitService() {
        quitting = true;
    }

    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            startPinging();
        } catch (InterruptedException ie) {
            System.out.println(SERVICE_NAME + " INTERRUPTED");
            ie.printStackTrace();
        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
