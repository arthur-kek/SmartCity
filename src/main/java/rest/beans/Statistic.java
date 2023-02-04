package rest.beans;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
public class Statistic {

    private double traveledKm;
    private int doneRidesNumber;
    private List<Double> pollutionList;
    private String tsOfComputation;
    private int batteryLvl;

    public Statistic() {

    }

    public Statistic(double traveledKm, int doneRidesNumber, List<Double> pollutionList, String tsOfComputation, int batteryLvl) {
        this.traveledKm = traveledKm;
        this.doneRidesNumber = doneRidesNumber;
        this.pollutionList = pollutionList;
        this.tsOfComputation = tsOfComputation;
        this.batteryLvl = batteryLvl;
    }

    public double getTraveledKm() {
        return traveledKm;
    }

    public void setTraveledKm(double traveledKm) {
        this.traveledKm = traveledKm;
    }

    public int getDoneRidesNumber() {
        return doneRidesNumber;
    }

    public void setDoneRidesNumber(int doneRidesNumber) {
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

    public int getBatteryLvl() {
        return batteryLvl;
    }

    public void setBatteryLvl(int batteryLvl) {
        this.batteryLvl = batteryLvl;
    }

    private String getPMList() {
        String s = "";
        for (Double d : pollutionList) {
            if (d != null) {
                s = s.concat(d + "|");
            }
        }
        return s;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "\ntraveledKm=" + traveledKm +
                ", \ndoneRidesNumber=" + doneRidesNumber +
                ", \npollutionList=" + getPMList() +
                ", \ntsOfComputation=" + tsOfComputation +
                ", \nbatteryLvl=" + batteryLvl +
                '}';
    }
}
