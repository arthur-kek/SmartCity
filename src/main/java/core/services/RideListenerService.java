package core.services;

import core.entities.DSRide;
import core.entities.DSTaxi;
import grpc.protocols.RideOuterClass;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import utils.Constants;

public class RideListenerService extends Thread {

    private final String SERVICE_NAME = "RIDE_LISTENER_SERVICE";

    private final String clientID;
    private MqttClient mqttClient;

    private String topic;
    private DSTaxi taxi;

    public RideListenerService(DSTaxi taxi, String topic) {
        this.taxi = taxi;
        this.clientID = MqttClient.generateClientId();
        this.topic = topic;
    }

    private void setUp() throws MqttException {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttClient.connect(mqttConnectOptions);
    }

    private void setCallback() {
        mqttClient.setCallback(new MqttCallback() {
            public void messageArrived(String topic, MqttMessage message) {
                try {
                    DSRide ride = new DSRide(RideOuterClass.Ride.parseFrom(message.getPayload()));
                    taxi.makeRide(ride);
                    //System.out.printf("%s NEW RIDE ARRIVED:\n%s%n", SERVICE_NAME, ride);
                } catch (Throwable t) {
                    System.out.printf("%s RIDE ERROR", SERVICE_NAME);
                }
            }

            public void connectionLost(Throwable cause) {
                System.out.printf("%s CONNECTION LOST", SERVICE_NAME);
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    public void subscribe(String topic) throws MqttException {
        mqttClient.subscribe(topic, Constants.DEFAULT_QOS);
        System.out.printf("%s MQTT CLIENT SUBSCRIBED ON TOPIC %s%n", SERVICE_NAME, topic);
    }

    public void unsubscribe(String topic) throws MqttException {
             mqttClient.unsubscribe(topic);
    }

    public void initConnection() throws MqttException {
        mqttClient = new MqttClient(Constants.BROKER_ADDRESS, clientID, new MemoryPersistence());
        setUp();
        setCallback();
        subscribe(topic);
    }

    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            initConnection();
        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }

}
