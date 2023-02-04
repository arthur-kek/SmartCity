package core.services;

import core.entities.DSTaxi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class QuitService extends Thread {

    private final static String SERVICE_NAME = "QUIT_SERVICE";
    private DSTaxi taxi;

    public QuitService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("PLEASE, ENTER --exit TO LEAVE SMART CITY NETWORK");
        try {
            while (true) {
                try {
                    String keyWord = br.readLine();
                    if (keyWord.equals("--exit")) {
                        taxi.leaveNetwork();
                        break;
                    }
                } catch (IOException e) {
                    System.out.println(SERVICE_NAME + " GENERIC ERROR");
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            System.out.println(SERVICE_NAME + " INTERRUPTED");
            e.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
