package core.services;

import com.google.protobuf.InvalidProtocolBufferException;
import core.clients.NotifyNewRideClient;
import core.entities.DSRide;
import core.entities.DSTaxi;
import core.exceptions.ChargeStationException;
import core.exceptions.WrongTaxiStateException;
import core.wrappers.RidesQueue;
import grpc.protocols.RideOuterClass;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import utils.Constants;
import utils.PositionUtils;

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
                    int idTopic = PositionUtils.getTopicIdByTopic(topic);

                    NotifyNewRideClient client = new NotifyNewRideClient(taxi, ride, idTopic);
                    client.start();
                    client.join();

                    if (client.getNewRideResponse() != null) {
                        if (!client.getNewRideResponse().getMessage().equals("INSERTED")) {
                            //System.out.printf("TAXI ID %d CAN'T SEND RIDE ID %d TO SERVER. ERROR %s%n", taxi.getId(), ride.getId(), client.getNewRideResponse().getMessage());
                        }
                    }
                } catch (InvalidProtocolBufferException | InterruptedException e) {
                    throw new RuntimeException(e);
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
        //System.out.printf("%s MQTT CLIENT SUBSCRIBED ON TOPIC %s%n", SERVICE_NAME, topic);
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

    public void disconnect() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
