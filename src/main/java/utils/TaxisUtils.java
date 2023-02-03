package utils;

import core.entities.DSTaxi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaxisUtils {

    public static boolean isIdInsideParticipants(String participants, int taxiId) {
        String[] idList = participants.split(">");

        for (String id : idList) {
            if (id.equals(String.valueOf(taxiId))) {
                return true;
            }
        }
        return false;
    }

    public static List<DSTaxi> getTaxisToRelease(String participants, List<DSTaxi> allTaxis) {

        if (participants.length() == 0) {
            return new ArrayList<>();
        }

        String[] idList = participants.split(">");
        List<DSTaxi> taxiToRelease = new ArrayList<>();

        for (String id : idList) {
            taxiToRelease.add(getTaxiById(Integer.parseInt(id), allTaxis));
        }
        return taxiToRelease;
    }

    private static DSTaxi getTaxiById(int id, List<DSTaxi> allTaxis) {
        for (DSTaxi t: allTaxis) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }
}
