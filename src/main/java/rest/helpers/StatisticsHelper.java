package rest.helpers;

import rest.beans.Pollution;
import rest.beans.Statistic;
import rest.beans.responses.AdmClientResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsHelper {

    public static AdmClientResponse getLastNMeans(int n, List<Statistic> taxiStatistics) {
        double totalKm = 0;
        double totalBattery = 0;
        double totalRides = 0;
        double totalPm = 0;

        if (n > taxiStatistics.size()) {
            n = taxiStatistics.size();
        }

        List<Statistic> lastNStatistics = taxiStatistics.subList(taxiStatistics.size() - n, taxiStatistics.size());

        for (Statistic statistic : lastNStatistics) {
            totalKm += statistic.getTraveledKm();
            totalBattery += statistic.getBatteryLvl();
            totalRides += statistic.getDoneRidesNumber();
            totalPm += calculateAveragePmValue(statistic.getPollutionList());
        }

        return new AdmClientResponse(
                totalKm / n,
                totalRides / n,
                totalPm / n,
                totalBattery / n
        );
    }

    public static AdmClientResponse getAllMeansInTimeFrame(long ts1, long ts2, Map<Integer, List<Statistic>> statistics) {
        List<Statistic> statisticsToProcess = new ArrayList<>();
        for (Map.Entry<Integer, List<Statistic>> entry : statistics.entrySet()) {
            for (Statistic statistic : entry.getValue())
                if (statistic.getTsOfComputation() > ts1 && statistic.getTsOfComputation() < ts2) {
                    statisticsToProcess.add(statistic);
                }
        }

        return getLastNMeans(statisticsToProcess.size(), statisticsToProcess);
    }

    public static double calculateAveragePmValue(List<Pollution> pollutionList) {
        double totalPm = 0;
        for (Pollution pollution : pollutionList) {
            totalPm += pollution.getPM10();
        }

        return totalPm / pollutionList.size();
    }
}
