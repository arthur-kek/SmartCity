package utils;

import core.entities.DSChargingStation;
import core.entities.DSPosition;
import core.enums.ChargingStation;
import core.enums.District;
import core.wrappers.ChargingStations;

public class PositionUtils {

    public static double CalculateDistance(DSPosition a, DSPosition b) {
        return Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }

    public static String getTopicByPosition(DSPosition position) {
        if (position.getX() < 5) {
            if (position.getY() < 5) {
                return Constants.TOPIC_ONE;
            } else {
                return Constants.TOPIC_THREE;
            }
        } else {
            if (position.getY() < 5) {
                return Constants.TOPIC_TWO;
            } else {
                return Constants.TOPIC_FOUR;
            }
        }
    }

    public static District getDistrictByPosition(DSPosition position) {
        if (position.getX() < 5) {
            if (position.getY() < 5) {
                return District.ONE;
            } else {
                return District.THREE;
            }
        } else {
            if (position.getY() < 5) {
                return District.TWO;
            } else {
                return District.FOUR;
            }
        }
    }

    public static DSChargingStation getChargingStationByPosition(DSPosition position) {
        ChargingStation station = ChargingStation.getByPosition(position);
        return ChargingStations.getByEnum(station);
    }

    public static int getTopicIdByTopic(String topic) {
        switch (topic) {
            case Constants.TOPIC_ONE:
                return 1;
            case Constants.TOPIC_TWO:
                return 2;
            case Constants.TOPIC_THREE:
                return 3;
            case Constants.TOPIC_FOUR:
                return 4;
        }
        return -1;
    }

    public static int getTopicIdByPosition(DSPosition position) {
        String topic = getTopicByPosition(position);
        return getTopicIdByTopic(topic);
    }

}
