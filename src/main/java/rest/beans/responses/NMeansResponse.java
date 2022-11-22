package rest.beans.responses;

import rest.beans.Statistic;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class NMeansResponse {

    private List<Statistic> statisticsResponse;

    public NMeansResponse(List<Statistic> statisticsResponse) {
        this.statisticsResponse = statisticsResponse;
    }

    public List<Statistic> getStatisticsResponse() {
        return statisticsResponse;
    }

    public void setStatisticsResponse(List<Statistic> statisticsResponse) {
        this.statisticsResponse = statisticsResponse;
    }

}
