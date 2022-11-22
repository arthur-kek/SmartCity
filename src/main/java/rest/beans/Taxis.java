package rest.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Taxis {

    private List<Taxi> taxisList;

    private static Taxis instance;

    private Taxis() {
        taxisList = new ArrayList<>();
    }

    //singleton
    public synchronized static Taxis getInstance(){
        if(instance==null)
            instance = new Taxis();
        return instance;
    }

    public synchronized List<Taxi> getTaxisList() {

        return new ArrayList<>(taxisList);

    }

    public synchronized Taxi delete(int id){
        Taxi t = getTaxiByID(id);
        if(t != null)
            taxisList.remove(t);
        return t;
    }

    public synchronized void setTaxisList(List<Taxi> taxisList) {
        this.taxisList = taxisList;
    }

    public synchronized void add(Taxi t){
        taxisList.add(t);
    }

    public synchronized Taxi get(int id) {
        for(Taxi t: getTaxisList())
            if(t.getId() == id)
                return t;
        return null;
    }

    public Taxi getTaxiByID(int id) {
        for(Taxi t : getTaxisList()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public boolean isAlreadyPresent(int id){
        List<Taxi> temp = getTaxisList();
        for(Taxi t: temp)
            if(t.getId() == id)
                return true;
        return false;
    }

}
