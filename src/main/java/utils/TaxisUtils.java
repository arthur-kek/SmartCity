package utils;

import core.entities.DSTaxi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaxisUtils {

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

    public static long calculateOffset(String taxiTs, long masterTs) throws ParseException {
        Date dateTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").parse(taxiTs);
        return dateTime.getTime() - masterTs;
    }

    public static long castTsToLong(String taxiTs) throws ParseException {
        Date dateTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").parse(taxiTs);
        return dateTime.getTime();
    }
}
