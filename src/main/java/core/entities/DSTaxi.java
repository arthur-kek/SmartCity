package core.entities;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import core.enums.TaxiState;
import core.exceptions.ChargeStationException;
import core.exceptions.WrongTaxiStateException;
import core.services.*;
import core.services.masterServices.MasterService;
import core.wrappers.RESTWrapper;
import grpc.protocols.ServiceProtocolOuterClass;
import grpc.protocols.TaxiProtocolOuterClass;
import org.eclipse.paho.client.mqttv3.MqttException;
import rest.beans.Taxi;
import utils.Constants;
import utils.LogUtils;
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
    private String previousTopic;
    private boolean isMaster;
    private DSChargingStation currentStation;

    private List<Double> averagePollution = new ArrayList<>();
    private TaxiState state = TaxiState.FREE;
    private List<DSTaxi> otherTaxis = new ArrayList<>();

    private final Object lockKmTraveled = new Object();
    private final Object lockBatteryLevel = new Object();
    private final Object lockDoneRidesNumber = new Object();
    private final Object lockCurrentStation = new Object();
    private final Object lockAveragePollution = new Object();
    private final Object lockState = new Object();
    private final Object lockOtherTaxis = new Object();
    private final Object lockCurrentTopic = new Object();
    private final Object lockPosition = new Object();
    private final Object lockMaster = new Object();
    private final Object lockExit = new Object();

    private TaxiService taxiService;
    private RegistrationService registrationService;
    private HelloService helloService;
    private SensorService sensorService;
    private QuitService quitService;
    private PushStatisticsService pushStatisticsService;
    private ManualRechargeService manualRechargeService;
    private RideListenerService rideListenerService;
    private PingService pingService;
    private ChargeRequestService chargeRequestService;
    private MasterService masterService;
    private MasterElectionService masterElectionService;

    public DSTaxi(int id, int port, String serverAddress, DSPosition position, TaxiState state, List<DSTaxi> otherTaxis) {
        this.id = id;
        this.port = port;
        this.serverAddress = serverAddress;
        this.batteryLevel = Constants.FULL_BATTERY_LEVEL;
        this.position = position;
        this.state = state;
        this.otherTaxis = otherTaxis;
        this.currentStation = PositionUtils.getChargingStationByPosition(position);
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
        this.isMaster = r.getIsMaster();
        this.currentStation = PositionUtils.getChargingStationByPosition(this.position);
    }

    public DSTaxi(ServiceProtocolOuterClass.ChargeRequest r) {
        this.id = r.getId();
        this.port = r.getPort();
        this.position = new DSPosition(r.getPosition().getX(), r.getPosition().getY());
        this.serverAddress = Constants.ADM_SERVER_ADDRESS;
        this.currentStation = PositionUtils.getChargingStationByPosition(this.position);
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
        this.currentStation = PositionUtils.getChargingStationByPosition(this.position);
    }

    /*
        If this taxi win the election it makes a ride
    */
    public void makeRide(DSRide ride) throws InterruptedException {
        if (getState() == TaxiState.FREE) {
            try {
                updateTaxiState(TaxiState.ON_ROAD);
                System.out.printf("ON RIDE ID %d ON %s%n", ride.getId(), LogUtils.getCurrentTS());
                Thread.sleep(Constants.RIDE_EXECUTION_TIME);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } finally {
                System.out.printf("RIDE ID %d DONE %s%n", ride.getId(), LogUtils.getCurrentTS());

                int distance = (int) (PositionUtils.CalculateDistance(getPosition(), ride.getStart())
                        + PositionUtils.CalculateDistance(ride.getStart(), ride.getDestination()));

                updateStatistics(distance, true);
                updatePosition(ride.getDestination());
                updateCurrentStation(PositionUtils.getChargingStationByPosition(getPosition()));

                String newTopic = PositionUtils.getTopicByPosition(ride.getDestination());
                decreaseBatteryLevel(distance);

                if (!getCurrentTopic().equals(newTopic)) {
                    previousTopic = currentTopic;
                    updateCurrentTopic(newTopic);
                    System.out.println("CHANGE TOPIC\n");
                    try {
                        rideListenerService.unsubscribe(previousTopic);
                        rideListenerService.subscribe(newTopic);
                    } catch (MqttException e) {
                        System.out.printf("ERROR SUBSCRIBING TOPIC %s%n", newTopic);
                    }
                }
                updateTaxiState(TaxiState.FREE);
            }
        }
    }

    /*
        ChargeManagementService notifies this taxi to recharge
    */
    public void recharge() throws ChargeStationException, WrongTaxiStateException, InterruptedException {
        if (getCurrentStation().isBusy()) {
            throw new ChargeStationException();
        }
        if (getState() != TaxiState.LOW_BATTERY) {
            throw new WrongTaxiStateException(state, TaxiState.LOW_BATTERY);
        } else {
            getCurrentStation().updateState(true);
            updateTaxiState(TaxiState.CHARGING);
            System.out.printf("TAXI ID %d IS GOING TO A RECHARGE ON %s%n", this.id, LogUtils.getCurrentTS());
            DSPosition newPosition = getCurrentStationPosition();
            int distance = calculateDistance(newPosition);
            updateStatistics(distance, false);

            Thread.sleep(Constants.FULL_CHARGING_TIME);
            updateBatteryLevel(Constants.FULL_BATTERY_LEVEL, true);
            getCurrentStation().updateState(false);

            System.out.printf("TAXI ID %d IS FULLY CHARGED %s%n", this.id, LogUtils.getCurrentTS());
            updatePosition(newPosition);
            updateTaxiState(TaxiState.FREE);
        }
    }

    /*
        If battery level reaches critical region taxi starts chargeRequestService that will notify ChargeManagementService about it
    */
    public void decreaseBatteryLevel(int points) throws InterruptedException {
        updateBatteryLevel(points, false);
        if (getBatteryLevel() <= Constants.CRITICAL_BATTERY_LEVEL) {
            updateTaxiState(TaxiState.LOW_BATTERY);
            startChargeRequestService();
        }
    }

    public void dropAllStatistics() {
        updateKmTraveled(0, true);
        updateDoneRidesNumber(true);
        resetAveragePollution();
    }

    /* region exit */

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
            printError("TAXI ID %d WAS SUCCESSFULLY DELETED FROM NETWORK%n");
        } else {
            printError("ERROR DELETING TAXI ID %d FROM NETWORK%n");
        }
    }

    /* end region */


    /* region updates */

    private void updateStatistics(int km, boolean didRide) {
        updateKmTraveled(km, false);
        if (didRide) {
            updateDoneRidesNumber(false);
        }
    }

    private void updateMaster(boolean isMaster) {
        synchronized (lockMaster) {
            this.isMaster = isMaster;
        }
    }

    public void updateBatteryLevel(int points, boolean wipe) {
        synchronized (lockBatteryLevel) {
            if (wipe) {
                this.batteryLevel = points;
            } else {
                this.batteryLevel -= points;
            }
        }
    }

    private void updateTaxiState(TaxiState state) {
        synchronized (lockState) {
            this.state = state;
        }
    }

    private void updateKmTraveled(double km, boolean wipe) {
        synchronized (lockKmTraveled) {
            if (wipe) {
                this.traveledKM = 0;
            } else {
                this.traveledKM += km;
            }
        }
    }

    private void updateDoneRidesNumber(boolean wipe) {
        synchronized (lockDoneRidesNumber) {
            if (wipe) {
                doneRidesNumber = 0;
            } else {
                doneRidesNumber = +1;
            }
        }
    }

    public void updatePosition(DSPosition position) {
        synchronized (lockPosition) {
            this.position = position;
        }
    }

    private void updateCurrentStation(DSChargingStation station) {
        synchronized (lockCurrentStation) {
            this.currentStation = station;
        }
    }

    public void updateCurrentTopic(String topic) {
        synchronized (lockCurrentTopic) {
            this.currentTopic = topic;
        }
    }

    /* end region */

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
        rideListenerService = new RideListenerService(this, getCurrentTopic());
        rideListenerService.start();
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

    private void startChargeRequestService() throws InterruptedException {
        chargeRequestService = new ChargeRequestService(this);
        chargeRequestService.start();
        chargeRequestService.join();
    }

    private void startMasterElectionService() throws InterruptedException {
        masterElectionService = new MasterElectionService(this);
        masterElectionService.start();
        masterElectionService.join();
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

    private void startMasterService() throws InterruptedException {
        masterService = new MasterService(this);
        masterService.start();
        masterService.join();
    }

    public void electMaster() throws InterruptedException {
        if (!getOtherTaxis().isEmpty()) {
            startMasterElectionService();
        }
    }

    // End region
    public void addNewTaxi(DSTaxi taxi) {
        synchronized (lockOtherTaxis) {
            this.otherTaxis.add(taxi);
        }
    }

    public List<DSTaxi> getOtherTaxis() {
        List<DSTaxi> tmp;
        synchronized (lockOtherTaxis) {
            tmp = otherTaxis;
        }
        return tmp;
    }

    public void setOtherTaxis(List<DSTaxi> otherTaxis) {
        synchronized (lockOtherTaxis) {
            this.otherTaxis = otherTaxis;
            if (otherTaxis.isEmpty()) {
                updateMaster(true);
                try {
                    startMasterService();
                } catch (InterruptedException ie) {
                    printError("ERROR STARTING MASTER SERVICE FOR TAXI ID %d%n");
                }
            }
        }
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

    public int getBatteryLevel() {
        int tmp;
        synchronized (lockBatteryLevel) {
            tmp = batteryLevel;
        }
        return tmp;
    }

    public DSPosition getPosition() {
        DSPosition tmp;
        synchronized (lockPosition) {
            tmp = position;
        }
        return tmp;
    }

    public void setPosition(DSPosition position) {
        this.position = position;
        this.currentStation = PositionUtils.getChargingStationByPosition(position);
    }

    public TaxiState getState() {
        TaxiState tmp;
        synchronized (lockState) {
            tmp = state;
        }
        return tmp;
    }

    public void setState(TaxiState state) {
        synchronized (lockState) {
            this.state = state;
        }
    }

    public List<Double> getAveragePollution() {
        List<Double> tmp;
        synchronized (lockAveragePollution) {
            tmp = averagePollution;
        }
        return tmp;
    }

    public void addPollutionAvg(double value) {
        synchronized (lockAveragePollution) {
            this.averagePollution.add(value);
        }
    }

    private void resetAveragePollution() {
        synchronized (lockAveragePollution) {
            this.averagePollution.clear();
        }
    }

    public double getTraveledKM() {
        double tmp;
        synchronized (lockKmTraveled) {
            tmp = traveledKM;
        }
        return tmp;
    }

    public int getDoneRidesNumber() {
        int tmp;
        synchronized (lockDoneRidesNumber) {
            tmp = doneRidesNumber;
        }
        return tmp;
    }

    public String getCurrentTopic() {
        String tmp;
        synchronized (lockCurrentTopic) {
            tmp = currentTopic;
        }
        return tmp;
    }

    public void setCurrentTopic(String currentTopic) {
        synchronized (lockCurrentTopic) {
            this.currentTopic = currentTopic;
        }
    }

    public DSChargingStation getCurrentStation() {
        DSChargingStation tmp;
        synchronized (lockCurrentStation) {
            tmp = currentStation;
        }
        return tmp;
    }

    private DSPosition getCurrentStationPosition() {
        DSPosition tmp;
        synchronized (lockCurrentStation) {
            tmp = currentStation.getStation().getPosition();
        }
        return tmp;
    }

    private int calculateDistance(DSPosition newPosition) {
        DSPosition tmp;
        synchronized (lockPosition) {
            tmp = position;
        }
        return (int) PositionUtils.CalculateDistance(tmp, newPosition);
    }

    public boolean isMaster() {
        boolean tmp;
        synchronized (lockMaster) {
            tmp = isMaster;
        }
        return tmp;
    }

    public void setMaster() {
        synchronized (lockMaster) {
            if (!isMaster) {
                isMaster = true;
            }
        }
        if (masterService != null) {
            try {
                startMasterService();
            } catch (InterruptedException ie) {
                printError("ERROR STARTING MASTER SERVICE FOR TAXI ID %d%n");
            }
        }
    }

    private void printError(String format) {
        System.out.printf(format, this.id);
    }

    public void removeDeadTaxi(int id) {
        synchronized (lockOtherTaxis) {
            Optional<DSTaxi> deadTaxi = Stream.of(otherTaxis)
                    .filter(taxi -> taxi.id == id)
                    .findFirst();

            if (deadTaxi.isPresent()) {
                otherTaxis.remove(deadTaxi.get());
            }
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
