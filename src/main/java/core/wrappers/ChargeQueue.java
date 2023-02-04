package core.wrappers;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import core.entities.DSTaxi;
import core.entities.DSTaxiOrdered;
import core.extensions.GenericList;
import core.extensions.MapQueue;

import java.util.Date;

public class ChargeQueue {

    private static MapQueue<DSTaxiOrdered> chargeMap;
    private static ChargeQueue instance;

    public ChargeQueue() {
        chargeMap = new MapQueue<>();
    }

    public synchronized static ChargeQueue getInstance() {
        if (instance == null)
            instance = new ChargeQueue();
        return instance;
    }

    public synchronized DSTaxi getFromQueue(int key) {
        if (chargeMap.getFromQueue(key) != null) {
            return chargeMap.getFromQueue(key).getTaxi();
        }
        return null;
    }

    public synchronized void addToQueue(int key, DSTaxi taxi, long ts) {
        DSTaxiOrdered taxiToInsert = new DSTaxiOrdered(taxi, ts);

        GenericList<DSTaxiOrdered> chargeQueue = chargeMap.getEntireQueue(key);
        if (chargeQueue == null || chargeQueue.getList().isEmpty()) {
            chargeMap.addToQueue(key, taxiToInsert);
        } else {
            int position = 0;

            for (DSTaxiOrdered t : chargeQueue.getList()) {
                if (t.getTs() > ts) {
                    position++;

                }
            }

            chargeMap.insertAtCertainPosition(key, position, taxiToInsert);
        }
    }

    public synchronized void removeTaxiFromQueue(int key) {
        chargeMap.removeFromQueue(key);
    }
}
