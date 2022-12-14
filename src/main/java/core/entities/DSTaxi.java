package core.entities;

import com.annimon.stream.Stream;
import core.enums.District;
import core.enums.TaxiState;
import core.services.HelloService;
import core.services.QuitService;
import core.services.RegistrationService;
import core.services.TaxiService;
import core.wrappers.RESTWrapper;
import grpc.protocols.TaxiProtocolOuterClass;
import rest.beans.Taxi;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DSTaxi {

    private int id;
    private int port;
    private String serverAddress;
    private int batteryLevel;
    private DSPosition position;
    private TaxiState state = TaxiState.FREE;
    private List<DSTaxi> otherTaxis = new ArrayList<>();
    private District district;
    private TaxiService taxiService;
    private RegistrationService registrationService;
    private HelloService helloService;

    private final Object lockExit = new Object();
    private final Object lockCharging = new Object();

    public DSTaxi(int id, int port, String serverAddress, DSPosition position, TaxiState state, List<DSTaxi> otherTaxis) {
        this.id = id;
        this.port = port;
        this.serverAddress = serverAddress;
        this.batteryLevel = Constants.FULL_BATTERY_LEVEL;
        this.position = position;
        this.state = state;
        this.otherTaxis = otherTaxis;
    }

    public DSTaxi(int id, int port, String serverAddress) {
        this.id = id;
        this.port = port;
        this.serverAddress = serverAddress;
    }

    public DSTaxi(Taxi t) {
        this.id = t.getId();
        this.port = t.getPort();
        this.serverAddress = t.getAdm_server_address();
        this.batteryLevel = Constants.FULL_BATTERY_LEVEL;
        this.position = new DSPosition(t.getPosition());
        this.state = TaxiState.FREE;
    }

    public DSTaxi(TaxiProtocolOuterClass.HelloRequest r) {
        this.id = r.getTaxiId();
        this.port = r.getPort();
        this.serverAddress = r.getIpAddress();
        this.batteryLevel = Constants.FULL_BATTERY_LEVEL;
        this.position = new DSPosition(r.getPosition().getX(), r.getPosition().getY());
    }

    public DSTaxi(Taxi t, List<Taxi> otherTaxis) {
        this.id = t.getId();
        this.port = t.getPort();
        this.serverAddress = t.getAdm_server_address();
        this.batteryLevel = t.getBattery_lvl();
        this.position = new DSPosition(t.getPosition());
        this.state = TaxiState.FREE;
        Stream.of(otherTaxis)
                .forEach(other -> this.otherTaxis.add(new DSTaxi(other)));
    }

    public void ride(int distance) throws InterruptedException {
        state = TaxiState.ON_ROAD;

        Thread.sleep(5000);

        decreaseBatteryLevel(distance);

        state = TaxiState.FREE;
    }

    public void recharge() {
        state = TaxiState.CHARGING;

        try {
            System.out.printf("TAXI ID %d IS GOING TO CHARGE%n", this.id);
            Thread.sleep(10000);
        } catch (InterruptedException ie) {
            System.out.printf("TAXI ID %d CHARGE ERROR%n", this.id);
        } finally {
            this.batteryLevel = Constants.FULL_BATTERY_LEVEL;
            System.out.printf("TAXI ID %d IS FULLY CHARGED%n", this.id);
            state = TaxiState.FREE;
        }
    }

    public void updatePosition(DSPosition newPosition) {
        this.position = newPosition;
    }

    // Region exit
    public void leaveNetwork() throws InterruptedException {
        synchronized (lockExit) {
            state = TaxiState.QUITTING;
            leaveAdmServer();
            lockExit.notifyAll();
        }
    }

    private void leaveAdmServer() {
        boolean response = RESTWrapper.getInstance().deleteTaxi(Constants.ADM_SERVER_ADDRESS, this.id);
        if (response) {
            System.out.printf("TAXI ID %d WAS SUCCESSFULLY DELETED FROM NETWORK%n", this.id);
        } else {
            System.out.printf("ERROR DELETING TAXI ID %d FROM NETWORK%n", this.id);
        }
    }

    // End region


    public void decreaseBatteryLevel(int points) {
        this.batteryLevel -= points;
        if (batteryLevel <= Constants.CRITICAL_BATTERY_LEVEL) {
            recharge();
        }
    }

    public void waitExitCall() throws InterruptedException {
        synchronized (lockExit) {
            lockExit.wait();
        }
    }

    // Region run new taxi
    public static void main(String[] args) {
        try {
            DSTaxi runningTaxi = initNewTaxi();
            runningTaxi.startAllServices();
        } catch (Throwable t) {
            System.out.println("TAXI GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    private static DSTaxi initNewTaxi() {
        int port = new Random().nextInt(40000 - 30000) + 30000;
        int id = new Random().nextInt(100 - 1) + 1;

        return new DSTaxi(id, port, Constants.ADM_SERVER_HOSTNAME);
    }

    private void startAllServices() throws InterruptedException {
        startQuitService();
        startTaxiService();
        startRegistrationService();
        startHelloService();
        waitExitCall();
    }

    private void startTaxiService() throws InterruptedException {
        taxiService = new TaxiService(this);
        taxiService.start();
        taxiService.join();
    }

    private void startQuitService() {
        QuitService quitService = new QuitService(this);
        quitService.start();
    }

    private void startRegistrationService() throws InterruptedException {
        registrationService = new RegistrationService(this);
        registrationService.start();
        registrationService.join();
    }

    private void startHelloService() throws InterruptedException {
        helloService = new HelloService(this);
        helloService.start();
        helloService.join();
    }

    // End region

    public void addNewTaxi(DSTaxi taxi) {
        this.otherTaxis.add(taxi);
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
