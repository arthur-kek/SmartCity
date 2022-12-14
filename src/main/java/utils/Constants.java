package utils;

public class Constants {

    public static final String BROKER_ADDRESS = "tcp://localhost:1883";

    public static final String ADM_SERVER_HOSTNAME = "localhost";

    public static final String ADM_SERVER_PORT = "1337";

    public static final String ADM_SERVER_ADDRESS = String.format("http://%s:%s/",
            ADM_SERVER_HOSTNAME, ADM_SERVER_PORT);

    public static final int SMART_CITY_DIMENSION = 10;

    public static final int FULL_BATTERY_LEVEL = 100;

    public static final int CRITICAL_BATTERY_LEVEL = 30;

    public static final int SEND_STATISTIC_FREQUENCY = 15000;
}
