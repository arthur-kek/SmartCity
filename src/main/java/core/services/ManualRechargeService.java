package core.services;

import core.entities.DSTaxi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ManualRechargeService extends Thread {

    private final static String SERVICE_NAME = "MANUAL_RECHARGE_SERVICE";
    private DSTaxi taxi;

    private volatile boolean quitting;

    public ManualRechargeService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    public void quit() {
        quitting = true;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("PLEASE, ENTER --recharge TO RECHARGE THIS TAXI");
        try {
            while (!quitting) {
                try {
                    if (br.readLine().equals("--recharge")) {
                        taxi.rechargeManually();
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