package utils;

import core.entities.DSPosition;

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

}
