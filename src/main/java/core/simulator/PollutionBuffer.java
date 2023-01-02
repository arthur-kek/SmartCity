package core.simulator;

import utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class PollutionBuffer implements Buffer {

    List<Measurement> measurements;

    public PollutionBuffer() {
        this.measurements = new ArrayList<>();
    }

    private synchronized void add(Measurement m) {
        if (measurements.size() == Constants.PM_BUFFER_WINDOW_SIZE) {
            notify();
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("ADDING MEASUREMENTS ERROR\n");
                e.printStackTrace();
            }
        }
        measurements.add(m);
    }

    private synchronized List<Measurement> read() {
        if (measurements.size() < Constants.PM_BUFFER_WINDOW_SIZE) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("READING MEASUREMENTS ERROR\n");
                e.printStackTrace();
            }
        }

        List<Measurement> measurementsToRead = new ArrayList<>(measurements);
        int overlap = Constants.PM_BUFFER_WINDOW_SIZE * Constants.PM_BUFFER_WINDOW_OVERLAP / 100;
        measurements.subList(0, overlap).clear();
        notify();
        return measurementsToRead;
    }

    @Override
    public void addMeasurement(Measurement m) {
        add(m);
    }

    @Override
    public List<Measurement> readAllAndClean() {
        return read();
    }

}
