package core.wrappers;

import core.entities.DSRide;
import core.entities.DSTaxi;
import core.extensions.MapQueue;

public class RidesQueue {

    private static MapQueue<DSRide> ridesMap;
    private static RidesQueue instance;

    public RidesQueue() {
        ridesMap = new MapQueue<>();
    }

    public synchronized static RidesQueue getInstance(){
        if(instance==null)
            instance = new RidesQueue();
        return instance;
    }

    public synchronized MapQueue<DSRide> getRidesMap() {
        return ridesMap;
    }
}
