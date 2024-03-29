package core.services;

import com.annimon.stream.Stream;
import core.entities.DSPosition;
import core.entities.DSTaxi;
import core.wrappers.RESTWrapper;
import rest.beans.Taxi;
import rest.beans.responses.TaxiResponse;
import utils.Constants;
import utils.PositionUtils;

import java.io.IOException;
import java.util.List;

public class RegistrationService extends Thread {

    private final static String SERVICE_NAME = "REGISTRATION_SERVICE";
    private DSTaxi taxi;

    public RegistrationService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    private void register() throws IOException {
        System.out.printf("SEND TAXI ID %d TO THE SERVER", taxi.getId());
        TaxiResponse newTaxi = RESTWrapper.getInstance().addTaxi(Constants.ADM_SERVER_ADDRESS, new Taxi(taxi));

        if (newTaxi.getTaxi() != null) {
            taxi.setId(newTaxi.getTaxi().getId());
            taxi.setPort(newTaxi.getTaxi().getPort());
            DSPosition position = new DSPosition(newTaxi.getTaxi().getPosition());
            taxi.updatePosition(position);
            taxi.updateBatteryLevel(Constants.FULL_BATTERY_LEVEL, true);
            taxi.updateCurrentTopic(PositionUtils.getTopicByPosition(position));
            taxi.updateCurrentDistrict(PositionUtils.getDistrictByPosition(position));

            List<DSTaxi> otherTaxis = Stream.of(newTaxi.getOtherTaxis())
                    .map(DSTaxi::new)
                    .toList();

            taxi.setOtherTaxis(otherTaxis);
        }
    }

    private void printResult() {
        System.out.printf("TAXI ID %d HAS BEEN REGISTERED", taxi.getId());
        System.out.println(taxi);
    }

    @Override
    public void run() {
        try {
            register();
        } catch (IOException e) {
            System.out.printf("ERROR DURING REGISTRATION TAXI ID %d", taxi.getId());
        } finally {
            printResult();
        }
    }
}
