package core.services;

import core.entities.DSTaxi;
import utils.Constants;

public class PrintDataService extends Thread {

    private final static String SERVICE_NAME = "PRINT_DATA_SERVICE";

    private DSTaxi taxi;

    private volatile boolean quitting;

    public PrintDataService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    private void startService() throws InterruptedException {
        while (!quitting) {
            System.out.println(taxi.toString());
            sleep(Constants.PRINT_DATA_SLEEP_TIME);
        }
    }

    public void quitService() {
        quitting = true;
    }

    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            startService();
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
