package core.services;

import core.clients.ChargeRequestClient;
import core.entities.DSTaxi;
import core.exceptions.ChargeStationException;
import core.exceptions.WrongTaxiStateException;

public class ChargeRequestService extends Thread {
    private final static String SERVICE_NAME = "CHARGE_MANAGEMENT_SERVICE";

    private DSTaxi taxi;

    public ChargeRequestService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    public void askForCharging() throws InterruptedException {
        System.out.printf("TAXI ID %d SENT CHARGE REQUEST%n", taxi.getId());

        ChargeRequestClient client = new ChargeRequestClient(taxi);
        client.start();
        client.join();

        if (client.getChargeAnswer() != null) {
            if (client.getChargeAnswer().getResponse().equals("OK")) {
                try {
                    taxi.recharge();
                } catch (ChargeStationException cse) {
                    System.out.println(cse.getMessage());
                } catch (WrongTaxiStateException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String offset = client.getChargeAnswer().getResponse();
                taxi.updateCurrentClock(offset);
                System.out.printf("TAXI ID %d IS WAITING FOR CHARGING STATION ID %d%n", taxi.getId(), taxi.getCurrentStation().getStation().getId());
            }
        }
    }

    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            askForCharging();

        } catch (InterruptedException ie) {
            System.out.println(SERVICE_NAME + " INTERRUPTED");
            ie.printStackTrace();
        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
