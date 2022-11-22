package core.services;

import core.entities.DSTaxi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class QuitService extends Thread {

    private DSTaxi taxi;

    public QuitService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    @Override
    public void run() {
        String outputHeader = "QUIT SERVICE:";

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                try {
                    System.out.println("PLEASE, ENTER --exit TO LEAVE SMART CITY NETWORK");
                    if (br.readLine().equals("--exit")) {
                        taxi.leaveNetwork();
                        break;
                    }
                } catch (IOException e) {

                }
            }
        } catch (InterruptedException e) {

        } finally {
            // Terminato
        }
    }
}
