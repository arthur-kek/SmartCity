package core.enums;

import core.entities.DSPosition;
import utils.Constants;

import java.util.Optional;
import java.util.stream.Stream;

public enum ChargingStation {

    FIRST(new DSPosition(0, 0), 1),
    SECOND(new DSPosition(0, Constants.SMART_CITY_DIMENSION - 1), 2),
    THIRD(new DSPosition(Constants.SMART_CITY_DIMENSION - 1, 0), 3),
    FOURTH(new DSPosition(Constants.SMART_CITY_DIMENSION - 1, Constants.SMART_CITY_DIMENSION - 1), 4);

    private final DSPosition position;
    private final int id;

    public DSPosition getPosition() {
        return this.position;
    }

    public int getId() {
        return id;
    }

    ChargingStation(DSPosition position, int id) {
        this.position = position;
        this.id = id;
    }

    public static ChargingStation get(int i) {
        Optional<ChargingStation> optional = Stream.of(ChargingStation.values())
                .filter(cs -> cs.id == i)
                .findFirst();

        return optional.orElse(null);
    }


}
