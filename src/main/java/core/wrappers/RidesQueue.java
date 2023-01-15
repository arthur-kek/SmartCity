package core.wrappers;

import core.entities.DSRide;
import core.entities.DSTaxi;
import core.extensions.MapQueue;

public class RidesQueue {

    private static MapQueue<DSRide> ridesMap;
    private static ChargeQueue instance;

    public RidesQueue() {
        ridesMap = new MapQueue<>();
    }

    public synchronized static ChargeQueue getInstance(){
        if(instance==null)
            instance = new ChargeQueue();
        return instance;
    }

    public synchronized MapQueue<DSRide> getChargeMap() {
        return ridesMap;
    }
}
