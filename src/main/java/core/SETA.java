package core;

import core.entities.DSPosition;
import core.entities.DSRide;
import core.enums.District;
import core.exceptions.InvalidRide;
import grpc.protocols.PositionOuterClass;
import grpc.protocols.RideOuterClass;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import utils.Constants;
import utils.PositionUtils;

public class SETA {

    private final static String SERVICE_NAME = "SETA";

    private MqttClient mqttClient;

    private int districtOneCounter;
    private int districtTwoCounter;
    private int districtThreeCounter;
    private int districtFourCounter;

    public SETA() throws MqttException {
        mqttClient = createMQTTClient();
        this.setUp();
        this.setCallback();
        this.districtOneCounter = 0;
        this.districtTwoCounter = 0;
        this.districtThreeCounter = 0;
        this.districtFourCounter = 0;
    }

    private MqttClient createMQTTClient() throws MqttException {
        return new MqttClient(Constants.BROKER_ADDRESS, MqttClient.generateClientId(), new MemoryPersistence());
    }

    public void loopRides() throws InterruptedException, MqttException {
        while (true) {
            createNewRides();
            waitABit();
        }
    }

    private void setUp() throws MqttException {
        if (!mqttClient.isConnected()) {
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(true);
            mqttClient.connect(mqttConnectOptions);
        }
    }

    private void setCallback() {
        mqttClient.setCallback(new MqttCallback() {
            public void messageArrived(String topic, MqttMessage message) {

            }

            public void connectionLost(Throwable t) {
                System.out.println("CONNECTION LOST");
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
                if (token.isComplete()) {
                    System.out.println("RIDE PUBLISHED");
                }
            }
        });
    }

    // Generates two rides each 5 seconds
    public void createNewRides() throws MqttException {
        try {
            // TODO generate two rides
            DSRide ride = new DSRide();
            int id = generateNewId(ride);
            ride.setId(id);
            publishRide(ride);
            /*DSRide ride2 = new DSRide();
            publishRide(ride2);*/

        } catch (InvalidRide e) {
            System.out.printf("%s INVALID RIDE", SERVICE_NAME);
        }
    }

    private int generateNewId(DSRide ride) {
        int temp;
        District district = PositionUtils.getDistrictByPosition(ride.getStart());
        switch (district) {
            case ONE:
                temp = district.getValue() * 1000 + districtOneCounter;
                districtOneCounter++;
                return temp;
            case TWO:
                temp = district.getValue() * 1000 + districtTwoCounter;
                districtTwoCounter++;
                return temp;
            case THREE:
                temp = district.getValue() * 1000 + districtThreeCounter;
                districtThreeCounter++;
                return temp;
            case FOUR:
                temp = district.getValue() * 1000 + districtFourCounter;
                districtFourCounter++;
                return temp;
        }
        return -1;
    }

    private void publishRide(DSRide ride) throws MqttException {
        MqttMessage message = new MqttMessage(buildRideMessage(ride));
        message.setQos(Constants.DEFAULT_QOS);
        String topic = getRideTopic(ride.getStart());
        mqttClient.publish(topic, message);

        switch (topic) {
            case Constants.TOPIC_ONE:
                System.out.printf("NEW RIDE ID %d CREATED ON %s START POSITION: (%d;%d)%n", 1000 + districtOneCounter - 1, topic, ride.getStart().getX(), ride.getStart().getY());
                break;
            case Constants.TOPIC_TWO:
                System.out.printf("NEW RIDE ID %d CREATED ON %s START POSITION: (%d;%d)%n", 2000 + districtTwoCounter - 1, topic, ride.getStart().getX(), ride.getStart().getY());
                break;
            case Constants.TOPIC_THREE:
                System.out.printf("NEW RIDE ID %d CREATED ON %s START POSITION: (%d;%d)%n", 3000 + districtThreeCounter - 1, topic, ride.getStart().getX(), ride.getStart().getY());
                break;
            case Constants.TOPIC_FOUR:
                System.out.printf("NEW RIDE ID %d CREATED ON %s START POSITION: (%d;%d)%n", 4000 + districtFourCounter - 1, topic, ride.getStart().getX(), ride.getStart().getY());
                break;
        }

    }

    public String getRideTopic(DSPosition p) {
        return PositionUtils.getTopicByPosition(p);
    }

    public static byte[] buildRideMessage(DSRide ride) {
        return RideOuterClass.Ride.newBuilder()
                .setId(ride.getId())
                .setStart(PositionOuterClass.Position.newBuilder()
                        .setX(ride.getStart().getX())
                        .setY(ride.getStart().getY())
                        .build())
                .setDestination(PositionOuterClass.Position.newBuilder()
                        .setX(ride.getDestination().getX())
                        .setY(ride.getDestination().getY())
                        .build())
                .build()
                .toByteArray();
    }

    private synchronized void waitABit() throws InterruptedException {
        wait(Constants.RIDE_GENERATION_FREQUENCY_TIME);
    }

    public static void main(String[] args) {
        try {
            SETA seta = new SETA();
            seta.loopRides();
        } catch (InterruptedException e) {
            System.out.printf("%s INTERRUPTED", SERVICE_NAME);
            e.printStackTrace();
        } catch (MqttException e) {
            System.out.printf("%s MQTT ERROR", SERVICE_NAME);
        } finally {
            System.out.printf("%s TERMINATED", SERVICE_NAME);
        }
    }
}
