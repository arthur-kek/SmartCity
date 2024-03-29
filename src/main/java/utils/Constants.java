package utils;

public class Constants {

    public static final String BROKER_ADDRESS = "tcp://localhost:1883";

    public static final String ADM_SERVER_HOSTNAME = "localhost";

    public static final String ADM_SERVER_PORT = "1337";

    public static final String ADM_SERVER_ADDRESS = String.format("http://%s:%s/",
            ADM_SERVER_HOSTNAME, ADM_SERVER_PORT);

    public static final int CHARGE_MANAGER_DEFAULT_PORT = 44444;

    public static final int RIDE_MANAGER_DEFAULT_PORT = 44445;

    public static final int BUSY_WAIT_TIME = 10000;

    public static final int SMART_CITY_DIMENSION = 10;

    public static final int FULL_BATTERY_LEVEL = 100;

    public static final int CRITICAL_BATTERY_LEVEL = 30;

    public static final int SEND_STATISTIC_FREQUENCY_TIME = 15000;

    public static final int RIDE_GENERATION_FREQUENCY_TIME = 2500;

    public static final int WAIT_TIME_UNTIL_CHECK_RIDES_QUEUE = 2000;

    public static final int WAIT_TIME_UNTIL_CHECK_CHARGE_QUEUE = 5000;

    public static final int RIDE_EXECUTION_TIME = 5000;

    public static final int FULL_CHARGING_TIME = 10000;

    public static final int PM_BUFFER_WINDOW_OVERLAP = 50;

    public static final int PM_BUFFER_WINDOW_SIZE = 8;

    public static final int DEFAULT_QOS = 2;

    public static final int PING_SLEEP_TIME = 500;

    public static final int PRINT_DATA_SLEEP_TIME = 10000;

    public static final String TOPIC_ONE = "seta/smartcity/rides/district1";

    public static final String TOPIC_TWO = "seta/smartcity/rides/district2";

    public static final String TOPIC_THREE = "seta/smartcity/rides/district3";

    public static final String TOPIC_FOUR = "seta/smartcity/rides/district4";

}
