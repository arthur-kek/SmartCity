package core.services;

import core.clients.ChargeRequestClient;
import core.clients.MasterElectionClient;
import core.entities.DSTaxi;
import core.exceptions.ChargeStationException;
import core.exceptions.WrongTaxiStateException;

public class MasterElectionService extends Thread {

    private final String SERVICE_NAME = "MASTER_ELECTION_SERVICE";
    private DSTaxi taxi;

    public MasterElectionService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    /*
        Taxi with minor ID becomes master
    */
    private void electAndNotifyMaster() throws InterruptedException {

        DSTaxi possibleMaster = taxi.getOtherTaxis().get(0);

        for (DSTaxi t : taxi.getOtherTaxis()) {
            if (t.getId() < possibleMaster.getId()) {
                possibleMaster = t;
            }
        }

        MasterElectionClient client = new MasterElectionClient(possibleMaster);
        client.start();
        client.join();

        if (client.getMasterResponse() != null && client.getMasterResponse().getOkMessage().equals("OK")) {
            System.out.printf("NEW MASTER IS TAXI ID %d%n", possibleMaster.getId());
        }
    }


    @Override
    public void run() {
        try {
            System.out.println(SERVICE_NAME + " STARTED");
            electAndNotifyMaster();
        } catch (Throwable t) {
            System.out.println(SERVICE_NAME + " GENERIC ERROR");
            t.printStackTrace();
        } finally {
            System.out.println(SERVICE_NAME + " TERMINATED");
        }
    }
}
