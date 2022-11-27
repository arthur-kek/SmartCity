package rest.beans.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AdmClientResponse {

    private double averageKm;
    private double averageDoneRides;
    private double averagePollution;
    private double averageBatteryLvl;

    public AdmClientResponse() {
    }

    public AdmClientResponse(double averageKm, double averageDoneRides, double averagePollution, double averageBatteryLvl) {
        this.averageKm = averageKm;
        this.averageDoneRides = averageDoneRides;
        this.averagePollution = averagePollution;
        this.averageBatteryLvl = averageBatteryLvl;
    }

    public double getAverageKm() {
        return averageKm;
    }

    public void setAverageKm(double averageKm) {
        this.averageKm = averageKm;
    }

    public double getAverageDoneRides() {
        return averageDoneRides;
    }

    public void setAverageDoneRides(double averageDoneRides) {
        this.averageDoneRides = averageDoneRides;
    }

    public double getAveragePollution() {
        return averagePollution;
    }

    public void setAveragePollution(double averagePollution) {
        this.averagePollution = averagePollution;
    }

    public double getAverageBatteryLvl() {
        return averageBatteryLvl;
    }

    public void setAverageBatteryLvl(double averageBatteryLvl) {
        this.averageBatteryLvl = averageBatteryLvl;
    }

    @Override
    public String toString() {
        return "AdmClientResponse{" +
                "averageKm=" + averageKm +
                ", averageDoneRides=" + averageDoneRides +
                ", averagePollution=" + averagePollution +
                ", averageBatteryLvl=" + averageBatteryLvl +
                '}';
    }
}
