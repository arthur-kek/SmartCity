package rest.beans.responses;

import rest.beans.Taxi;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class TaxiResponse {

    private Taxi taxi;
    private List<Taxi> otherTaxis;

    public Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    public List<Taxi> getOtherTaxis() {
        return otherTaxis;
    }

    public void setOtherTaxis(List<Taxi> otherTaxis) {
        this.otherTaxis = otherTaxis;
    }
}
