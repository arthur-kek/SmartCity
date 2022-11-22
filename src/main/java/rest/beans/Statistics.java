package rest.beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
public class Statistics {

    private static Map<Integer, List<Statistic>> statisticMap;

    private static Statistics instance;

    public Statistics() {
        statisticMap = new HashMap<>();
    }

    public synchronized static Statistics getInstance(){
        if(instance==null)
            instance = new Statistics();
        return instance;
    }

    public synchronized Map<Integer, List<Statistic>> getStatisticsMap() {
        return statisticMap;
    }

    public synchronized void setStatisticsMap(Map<Integer, List<Statistic>> statistics) {
        statisticMap = statistics;
    }

    public synchronized List<Statistic> getStatisticsByTaxi(int taxiId) {
        return statisticMap.get(taxiId);
    }

    public synchronized void addStatistic(int taxiId, Statistic statistic) {
        List<Statistic> tmp = statisticMap.get(taxiId);
        tmp.add(statistic);
        statisticMap.put(taxiId, tmp);
    }
}
