package core.services;

import com.annimon.stream.Stream;
import core.entities.DSTaxi;
import core.simulator.Measurement;
import core.simulator.PM10Simulator;
import core.simulator.PollutionBuffer;

import java.util.List;

public class SensorService extends Thread {

    private final static String SERVICE_NAME = "SENSOR_SERVICE";
    private DSTaxi taxi;
    private PM10Simulator simulator;
    private PollutionBuffer buffer;

    private boolean quitting;

    public SensorService(DSTaxi taxi) {
        this.taxi = taxi;
        this.buffer = new PollutionBuffer();
        this.simulator = new PM10Simulator(buffer);
        this.quitting = false;
    }

    private double avg(List<Measurement> measurements) {
        double totalPollution = 0;
        for (Measurement m : measurements) {
            totalPollution += m.getValue();
        }

        return totalPollution / measurements.size();
    }

    public void quitService() {
        quitting = true;
    }

    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            simulator.start();

            while(!quitting) {
                this.taxi.addPollutionAvg(avg(buffer.readAllAndClean()));
            }

        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
