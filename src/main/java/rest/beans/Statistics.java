package rest.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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
        return statisticMap.get(taxiId) != null ? statisticMap.get(taxiId) : new ArrayList<>();
    }

    public synchronized void addStatistic(int taxiId, Statistic statistic) {
        List<Statistic> tmp = statisticMap.get(taxiId);
        if (tmp == null) {
            tmp = new ArrayList<>();
            tmp.add(statistic);
            statisticMap.put(taxiId, tmp);
        }
        tmp.add(statistic);
        statisticMap.put(taxiId, tmp);
    }
}
