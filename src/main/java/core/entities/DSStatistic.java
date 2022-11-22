package core.entities;

import rest.beans.Pollution;
import rest.beans.Statistic;

import java.util.List;

public class DSStatistic {

    private float traveledKm;
    private int doneRidesNumber;
    private List<Pollution> pollutionList;
    private long tsOfComputation;
    private int batteryLvl;

    public DSStatistic(float traveledKm, int doneRidesNumber, List<Pollution> pollutionList, long tsOfComputation, int batteryLvl) {
        this.traveledKm = traveledKm;
        this.doneRidesNumber = doneRidesNumber;
        this.pollutionList = pollutionList;
        this.tsOfComputation = tsOfComputation;
        this.batteryLvl = batteryLvl;
    }

    public DSStatistic(Statistic statistic) {
        this.traveledKm = statistic.getTraveledKm();
        this.doneRidesNumber = statistic.getDoneRidesNumber();
        this.pollutionList = statistic.getPollutionList();
        this.tsOfComputation = statistic.getTsOfComputation();
        this.batteryLvl = statistic.getBatteryLvl();
    }


    public float getTraveledKm() {
        return traveledKm;
    }

    public void setTraveledKm(float traveledKm) {
        this.traveledKm = traveledKm;
    }

    public int getDoneRidesNumber() {
        return doneRidesNumber;
    }

    public void setDoneRidesNumber(int doneRidesNumber) {
        this.doneRidesNumber = doneRidesNumber;
    }

    public List<Pollution> getPollutionList() {
        return pollutionList;
    }

    public void setPollutionList(List<Pollution> pollutionList) {
        this.pollutionList = pollutionList;
    }

    public long getTsOfComputation() {
        return tsOfComputation;
    }

    public void setTsOfComputation(long tsOfComputation) {
        this.tsOfComputation = tsOfComputation;
    }

    public int getBatteryLvl() {
        return batteryLvl;
    }

    public void setBatteryLvl(int batteryLvl) {
        this.batteryLvl = batteryLvl;
    }
}
