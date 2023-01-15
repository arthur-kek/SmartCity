package core.wrappers;

import core.entities.DSTaxi;
import core.extensions.MapQueue;

public class ChargeQueue {

    private static MapQueue<DSTaxi> chargeMap;
    private static ChargeQueue instance;

    public ChargeQueue() {
        chargeMap = new MapQueue<>();
    }

    public synchronized static ChargeQueue getInstance(){
        if(instance==null)
            instance = new ChargeQueue();
        return instance;
    }

    public synchronized MapQueue<DSTaxi> getChargeMap() {
        return chargeMap;
    }
}
