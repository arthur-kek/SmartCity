package rest.beans;

import core.entities.DSTaxi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="taxi")
public class Taxi {

    private int id;
    private int port;
    private String adm_server_address;
    private int battery_lvl;

    private Position position;

    public Taxi() {

    }

    public Taxi(int id, int port, String adm_server_address) {
        this.id = id;
        this.port = port;
        this.adm_server_address = adm_server_address;
        this.battery_lvl = 100;
    }

    public Taxi(DSTaxi t) {
        this.id = t.getId();
        this.port = t.getPort();
        this.adm_server_address = t.getServerAddress();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAdm_server_address() {
        return adm_server_address;
    }

    public void setAdm_server_address(String adm_server_address) {
        this.adm_server_address = adm_server_address;
    }

    public int getBattery_lvl() {
        return battery_lvl;
    }

    public void setBattery_lvl(int battery_lvl) {
        this.battery_lvl = battery_lvl;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Taxi{" +
                "id=" + id +
                ", port=" + port +
                ", adm_server_address='" + adm_server_address + '\'' +
                ", battery_lvl=" + battery_lvl +
                ", position=" + position +
                '}';
    }
}
