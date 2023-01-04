package core.entities;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import core.enums.District;
import core.enums.TaxiState;
import core.services.*;
import core.wrappers.RESTWrapper;
import grpc.protocols.TaxiProtocolOuterClass;
import rest.beans.Taxi;
import utils.Constants;
import utils.PositionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DSTaxi {

    private int id;
    private int port;
    private String serverAddress;
    private int batteryLevel;
    private DSPosition position;
    private double traveledKM;
    private int doneRidesNumber;
    private String currentTopic;

    private List<Double> averagePollution = new ArrayList<>();
    private TaxiState state = TaxiState.FREE;
    private List<DSTaxi> otherTaxis = new ArrayList<>();
    private District district;
    private TaxiService taxiService;
    private RegistrationService registrationService;
    private HelloService helloService;
    private SensorService sensorService;
    private QuitService quitService;
    private PushStatisticsService pushStatisticsService;
    private ManualRechargeService manualRechargeService;
    private RideListenerService rideListenerService;
    private PingService pingService;

    private final Object lockExit = new Object();
    private final Object lockCharging = new Object();

    public void dropAllStatistics() {
        this.traveledKM = 0;
        this.doneRidesNumber = 0;
        averagePollution.clear();
    }

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

    public void makeRide(DSRide ride) throws InterruptedException {
        if(state == TaxiState.LOW_BATTERY || state == TaxiState.CHARGING) {

        } else {
            state = TaxiState.ON_ROAD;

            Thread.sleep(Constants.RIDE_EXECUTION_TIME);

            System.out.println("ON RIDE\n");

            int distance = (int) PositionUtils.CalculateDistance(ride.getStart(), ride.getDestination());
            String newTopic = PositionUtils.getTopicByPosition(ride.getDestination());
            decreaseBatteryLevel(distance);

            if (!currentTopic.equals(newTopic)) {
                currentTopic = newTopic;
                System.out.println("CHENGE TOPIC\n");
                startRideListenerService();
            }
            System.out.println("RIDE DONE\n");
            state = TaxiState.FREE;
        }
    }

    public void recharge() {
        state = TaxiState.CHARGING;
        try {
            System.out.printf("TAXI ID %d IS GOING TO CHARGE%n", this.id);
            Thread.sleep(Constants.FULL_CHARGING_TIME);
        } catch (InterruptedException ie) {
            System.out.printf("TAXI ID %d CHARGE ERROR%n", this.id);
        } finally {
            this.batteryLevel = Constants.FULL_BATTERY_LEVEL;
            System.out.printf("TAXI ID %d IS FULLY CHARGED%n", this.id);
            state = TaxiState.FREE;
        }
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
            state = TaxiState.LOW_BATTERY;
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
        startManualRechargeService();
        startRegistrationService();
        startPushStatisticsService();
        startHelloService();
        startSensorService();
        startPingService();
        startRideListenerService();
        waitExitCall();
    }

    private void startRideListenerService() throws InterruptedException {
        rideListenerService = new RideListenerService(this, currentTopic);
        rideListenerService.start();
        rideListenerService.join();
    }

    private void startTaxiService() throws InterruptedException {
        taxiService = new TaxiService(this);
        taxiService.start();
        taxiService.join();
    }

    private void startManualRechargeService() throws InterruptedException {
        manualRechargeService = new ManualRechargeService(this);
        manualRechargeService.start();
    }

    private void startSensorService() throws InterruptedException {
        sensorService = new SensorService(this);
        sensorService.start();
    }

    private void startQuitService() {
        quitService = new QuitService(this);
        quitService.start();
    }

    private void startPingService() {
        pingService = new PingService(this);
        pingService.start();
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

    private void startPushStatisticsService() throws InterruptedException {
        pushStatisticsService = new PushStatisticsService(this);
        pushStatisticsService.start();
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

    public List<Double> getAveragePollution() {
        return averagePollution;
    }

    public void addPollutionAvg(double value) {
        this.averagePollution.add(value);
    }

    public double getTraveledKM() {
        return traveledKM;
    }

    public void setTraveledKM(double traveledKM) {
        this.traveledKM = traveledKM;
    }

    public int getDoneRidesNumber() {
        return doneRidesNumber;
    }

    public void setDoneRidesNumber(int doneRidesNumber) {
        this.doneRidesNumber = doneRidesNumber;
    }

    public String getCurrentTopic() {
        return currentTopic;
    }

    public void setCurrentTopic(String currentTopic) {
        this.currentTopic = currentTopic;
    }

    public void removeDeadTaxi(int id) {
        Optional<DSTaxi> deadTaxi = Stream.of(otherTaxis)
                .filter(taxi -> taxi.id == id)
                .findFirst();

        if (deadTaxi.isPresent()) {
            otherTaxis.remove(deadTaxi.get());
        }
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
