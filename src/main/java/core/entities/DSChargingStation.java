package core.entities;

import core.enums.ChargingStation;


public class DSChargingStation {

    private ChargingStation station;
    private boolean isBusy;
    private final Object lockState = new Object();

    public DSChargingStation(ChargingStation station) {
        this.station = station;
        isBusy = false;
    }

    public boolean isBusy() {
        boolean tmp;
        synchronized (lockState) {
            tmp = isBusy;
        }
        return tmp;
    }

    public void updateState(boolean state) {
        synchronized (lockState) {
            this.isBusy = state;
        }
    }

    public ChargingStation getStation() {
        return station;
    }

}
