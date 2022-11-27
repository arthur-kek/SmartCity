package core.entities;

import core.enums.TaxiState;
import rest.beans.Taxi;
import java.util.List;

public class DSTaxi {

    private int id;
    private int port;
    private String serverAddress;
    private int batteryLevel;
    private DSPosition position;
    private TaxiState state;
    private List<DSTaxi> otherTaxis;

    public DSTaxi(int id, int port, String serverAddress, int batteryLevel, DSPosition position, TaxiState state, List<DSTaxi> otherTaxis) {
        this.id = id;
        this.port = port;
        this.serverAddress = serverAddress;
        this.batteryLevel = batteryLevel;
        this.position = position;
        this.state = state;
        this.otherTaxis = otherTaxis;
    }

    public DSTaxi(Taxi t) {
        this.id = t.getId();
        this.port = t.getPort();
        this.serverAddress = t.getAdm_server_address();
        this.batteryLevel = t.getBattery_lvl();
        this.position = new DSPosition(t.getPosition());
        this.state = TaxiState.FREE;
    }

    public void ride(int distance) throws InterruptedException {
        state = TaxiState.ON_ROAD;

        Thread.sleep(5000);

        decreaseBatteryLevel(distance);

        state = TaxiState.FREE;
    }

    public void recharge() throws InterruptedException {
        state = TaxiState.CHARGING;

        Thread.sleep(10000);

        this.batteryLevel = 100;

        state = TaxiState.FREE;
    }

    public void updatePosition(DSPosition newPosition) {
        this.position = newPosition;
    }

    public void leaveNetwork() throws InterruptedException {
        state = TaxiState.QUITTING;
        // TODO: Not yet implemented
    }

    public void decreaseBatteryLevel(int points) throws InterruptedException {
        this.batteryLevel =- points;

        if (batteryLevel <= 30) {
            recharge();
        }
    }

    public List<DSTaxi> getOtherTaxis() {
        return otherTaxis;
    }

    public void setOtherTaxis(List<DSTaxi> otherTaxis) {
        this.otherTaxis = otherTaxis;
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

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public DSPosition getPosition() {
        return position;
    }

    public void setPosition(DSPosition position) {
        this.position = position;
    }

    public TaxiState getState() {
        return state;
    }

    public void setState(TaxiState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "DSTaxi{" +
                "id=" + id +
                ", port=" + port +
                ", serverAddress='" + serverAddress + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", position=" + position +
                ", state=" + state +
                '}';
    }
}
