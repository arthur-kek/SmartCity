package enums;

import entities.DSPosition;
import utils.Constants;

import java.util.Optional;
import java.util.stream.Stream;

public enum ChargeStationEnum {

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

    ChargeStationEnum(DSPosition position, int id) {
        this.position = position;
        this.id = id;
    }

    public static ChargeStationEnum get(int i) {
        Optional<ChargeStationEnum> optional = Stream.of(ChargeStationEnum.values())
                .filter(cs -> cs.id == i)
                .findFirst();

        return optional.orElse(null);
    }


}
