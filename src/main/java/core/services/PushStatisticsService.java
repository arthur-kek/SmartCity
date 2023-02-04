package core.services;

import core.entities.DSTaxi;
import core.wrappers.RESTWrapper;
import rest.beans.Statistic;
import utils.Constants;

import java.sql.Timestamp;
import java.util.Date;

public class PushStatisticsService extends Thread {

    private final static String SERVICE_NAME = "PUSH_STATISTICS_SERVICE";
    private volatile boolean quitting;

    private DSTaxi taxi;

    public PushStatisticsService(DSTaxi taxi) {
        this.taxi = taxi;
        this.quitting = false;
    }

    private synchronized void waitABit() throws InterruptedException {
        wait(Constants.SEND_STATISTIC_FREQUENCY_TIME);
    }

    private void push() {
        Date date = new Date();
        Statistic statistic = new Statistic(taxi.getTraveledKM(), taxi.getDoneRidesNumber(), taxi.getAveragePollution(), new Timestamp(date.getTime()).toString(), taxi.getBatteryLevel());
        RESTWrapper.getInstance().pushStatistics(Constants.ADM_SERVER_ADDRESS, taxi.getId(), statistic);
        taxi.dropAllStatistics();
    }

    public void pushLastAndQuit() {
        quitting = true;
    }

    @Override
    public void run() {
        try {
            waitABit();
            System.out.println(SERVICE_NAME + " STARTED");
            while(!quitting) {
                push();
                waitABit();
            }
            push();
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
