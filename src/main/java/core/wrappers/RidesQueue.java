package core.wrappers;

import com.annimon.stream.Stream;
import core.entities.DSRide;
import core.extensions.MapQueue;

import java.util.ArrayList;
import java.util.List;

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

    public synchronized void reinsertNotElectedRide(int key, DSRide ride) {
        ridesMap.insertAsFirst(key, ride);

        //printQueueState();
    }

    public synchronized DSRide getAndRemoveFromQueue(int key) {
        return ridesMap.getAndRemoveFromQueue(key);
    }

    public synchronized String addToQueue(int key, DSRide ride) {
        String response;
        if (ridesMap.getEntireQueue(key) == null || !checkIfExists(key, ride.getId())) {
            ridesMap.addToQueue(key, ride);
            response = "INSERTED";
        } else {
            response = "NON INSERTED";
        }

        //printQueueState();
        return response;
    }

    private boolean checkIfExists(int key, int rideId) {
        for (DSRide ride : ridesMap.getEntireQueue(key).getList()) {
            if (ride.getId() == rideId) {
                return true;
            }
        }
        return false;
    }
/*    private void printQueueState() {
       printQueueState(1, sortById(1));
        printQueueState(2, sortById(2));
        printQueueState(3, sortById(3));
        printQueueState(4, sortById(4));
    }

    private List<Integer> sortById(int key) {
        if (ridesMap.getEntireQueue(key) == null) {
            return new ArrayList<>();
        }
        return Stream.of(ridesMap.getEntireQueue(key).getList())
                .map(DSRide::getId)
                .toList();
    }

    private void printQueueState(int key, List<Integer> ids) {
        System.out.println(key + " QUEUE RIDES: ");
        if (ids.isEmpty()) {
            System.out.print(key + " EMPTY");
        } else {
            for (int i : ids) {
                System.out.print(i + "|");
            }
        }
        System.out.println("\n");
    }*/
}
