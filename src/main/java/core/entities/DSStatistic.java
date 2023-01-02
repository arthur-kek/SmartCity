package core.entities;

import rest.beans.Statistic;

import java.sql.Timestamp;
import java.util.List;

public class DSStatistic {

    private double traveledKm;
    private double doneRidesNumber;
    private List<Double> pollutionList;
    private String tsOfComputation;
    private double batteryLvl;

    public DSStatistic(float traveledKm, int doneRidesNumber, List<Double> pollutionList, String tsOfComputation, int batteryLvl) {
        this.traveledKm = traveledKm;
        this.doneRidesNumber = doneRidesNumber;
        this.pollutionList = pollutionList;
        this.tsOfComputation = tsOfComputation;
        this.batteryLvl = batteryLvl;
    }

    public DSStatistic(Statistic statistic) {
        this.traveledKm = statistic.getTraveledKm();
        this.doneRidesNumber = statistic.getDoneRidesNumber();
        //this.pollutionList = statistic.getPollutionList();
        //this.tsOfComputation = statistic.getTsOfComputation();
        this.batteryLvl = statistic.getBatteryLvl();
    }

    public double getTraveledKm() {
        return traveledKm;
    }

    public void setTraveledKm(double traveledKm) {
        this.traveledKm = traveledKm;
    }

    public double getDoneRidesNumber() {
        return doneRidesNumber;
    }

    public void setDoneRidesNumber(double doneRidesNumber) {
        this.doneRidesNumber = doneRidesNumber;
    }

    public List<Double> getPollutionList() {
        return pollutionList;
    }

    public void setPollutionList(List<Double> pollutionList) {
        this.pollutionList = pollutionList;
    }

    public String getTsOfComputation() {
        return tsOfComputation;
    }

    public void setTsOfComputation(String tsOfComputation) {
        this.tsOfComputation = tsOfComputation;
    }

    public double getBatteryLvl() {
        return batteryLvl;
    }

    public void setBatteryLvl(double batteryLvl) {
        this.batteryLvl = batteryLvl;
    }
}




