package core.services;

import core.entities.DSTaxi;
import utils.Constants;

public class PushStatisticsService extends Thread {

    private final static String SERVICE_HEADER = "PUSH_STATISTICS_SERVICE";

    private DSTaxi taxi;

    public PushStatisticsService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    private synchronized void waitSomeTime() throws InterruptedException {
        wait(Constants.SEND_STATISTIC_FREQUENCY);
    }

    private void push() {

    }
}
