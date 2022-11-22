package rest.beans;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Pollution {

    private double PM10;
    private long timestamp;


    public Pollution() {

    }

    public double getPM10() {
        return PM10;
    }

    public void setPM10(double PM10) {
        this.PM10 = PM10;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
