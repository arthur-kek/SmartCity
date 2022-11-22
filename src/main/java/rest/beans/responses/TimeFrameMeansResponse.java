package rest.beans.responses;

import rest.beans.Statistic;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class TimeFrameMeansResponse {

    private int taxiId;
    private Statistic averageStatistic;

    public TimeFrameMeansResponse(int taxiId, Statistic averageStatistic) {
        this.taxiId = taxiId;
        this.averageStatistic = averageStatistic;
    }

    public int getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(int taxiId) {
        this.taxiId = taxiId;
    }

    public Statistic getAverageStatistic() {
        return averageStatistic;
    }

    public void setAverageStatistic(Statistic averageStatistic) {
        this.averageStatistic = averageStatistic;
    }
}
