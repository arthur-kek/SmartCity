package core.services;

import com.annimon.stream.Stream;
import core.entities.DSTaxi;
import core.wrappers.RESTWrapper;
import rest.beans.Taxi;
import rest.beans.responses.TaxiResponse;

import java.io.IOException;
import java.util.List;

public class RegistrationService extends Thread {

    private DSTaxi taxi;

    public RegistrationService(DSTaxi taxi) {
        this.taxi = taxi;
    }

    private void register() throws IOException {
        System.out.println("Start registration new Taxi");
        TaxiResponse newTaxi = RESTWrapper.getInstance().addTaxi(taxi.getServerAddress(), new Taxi(taxi));

       if (newTaxi != null) {
           taxi = new DSTaxi(newTaxi.getTaxi());

           List<DSTaxi> otherTaxis = Stream.of(newTaxi.getOtherTaxis())
                   .map(DSTaxi::new)
                   .toList();

           taxi.setOtherTaxis(otherTaxis);
       }
    }

    private void printResult() {
        System.out.println("The following taxi has been registered to the system");
        System.out.println(taxi);
    }

    @Override
    public void run() {

        try {
            register();
        } catch (IOException e) {

        } finally {

        }
    }
}
