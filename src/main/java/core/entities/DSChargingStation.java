package core.entities;

import core.enums.ChargingStation;
import core.enums.ChargingStationState;


public class DSChargingStation {

    private ChargingStation station;
    private ChargingStationState state;

    public DSChargingStation(ChargingStation station) {
        this.station = station;
        state = ChargingStationState.FREE;
    }

}
