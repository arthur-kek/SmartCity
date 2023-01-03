package core.services;

import core.entities.DSTaxi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ManualRechargeService extends Thread {

    private final static String SERVICE_NAME = "MANUAL_RECHARGE_SERVICE";
    private DSTaxi taxi;

    public ManualRechargeService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                try {
                    System.out.println("PLEASE, ENTER --recharge TO RECHARGE THIS TAXI");
                    if (br.readLine().equals("--recharge")) {
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