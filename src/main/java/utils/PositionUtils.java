package utils;

import core.entities.DSPosition;

public class PositionUtils {

    public static double CalculateDistance(DSPosition a, DSPosition b) {
        return Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }

}
