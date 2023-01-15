package core.wrappers;

import core.entities.DSChargingStation;
import core.enums.ChargingStation;

public class ChargingStations {

    private static DSChargingStation firstStation;
    private static DSChargingStation secondStation;
    private static DSChargingStation thirdStation;
    private static DSChargingStation fourthStation;

    private static ChargingStations instance;

    public synchronized static ChargingStations getInstance(){
        if(instance==null)
            instance = new ChargingStations();
        return instance;
    }

    public ChargingStations() {
        if (firstStation == null) {
            firstStation = new DSChargingStation(ChargingStation.FIRST);
        }
        if (secondStation == null) {
            secondStation = new DSChargingStation(ChargingStation.SECOND);
        }
        if (thirdStation == null) {
            thirdStation = new DSChargingStation(ChargingStation.THIRD);
        }
        if (fourthStation == null) {
            fourthStation = new DSChargingStation(ChargingStation.FOURTH);
        }
    }

    public static DSChargingStation getFirstStation() {
        if (firstStation == null) {
            firstStation = new DSChargingStation(ChargingStation.FIRST);
        }
        return firstStation;
    }

    public static DSChargingStation getSecondStation() {
        if (secondStation == null) {
            secondStation = new DSChargingStation(ChargingStation.SECOND);
        }
        return secondStation;
    }

    public static DSChargingStation getThirdStation() {
        if (thirdStation == null) {
            thirdStation = new DSChargingStation(ChargingStation.THIRD);
        }
        return thirdStation;
    }

    public static DSChargingStation getFourthStation() {
        if (fourthStation == null) {
            fourthStation = new DSChargingStation(ChargingStation.FOURTH);
        }
        return fourthStation;
    }

    public boolean isFirstStationBusy() {
        return firstStation.isBusy();
    }

    public boolean isSecondStationBusy() {
        return secondStation.isBusy();
    }

    public boolean isThirdStationBusy() {
        return thirdStation.isBusy();
    }

    public boolean isFourthStationBusy() {
        return fourthStation.isBusy();
    }

    public static DSChargingStation getByEnum(ChargingStation station) {
        switch (station) {
            case FIRST:
                return getFirstStation();
            case SECOND:
                return getSecondStation();
            case THIRD:
                return getThirdStation();
            case FOURTH:
                return getFourthStation();
        }
        return getFirstStation();
    }
}
