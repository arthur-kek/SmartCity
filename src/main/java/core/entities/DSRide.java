package core.entities;

import core.exceptions.InvalidRide;
import grpc.protocols.RideOuterClass;
import utils.Constants;

import java.util.Random;
import java.util.UUID;

public class DSRide {
    private int id;
    private DSPosition start;
    private DSPosition destination;

    public DSRide() throws InvalidRide {
        Random random = new Random();

        DSPosition startingPoint = new DSPosition(random.nextInt(Constants.SMART_CITY_DIMENSION),
                random.nextInt(Constants.SMART_CITY_DIMENSION));

        DSPosition destinationPoint = new DSPosition(random.nextInt(Constants.SMART_CITY_DIMENSION),
                random.nextInt(Constants.SMART_CITY_DIMENSION));

        if (startingPoint.equals(destinationPoint)) {
            throw new InvalidRide();
        }

        this.start = startingPoint;
        this.destination = destinationPoint;
    }

    public DSRide(RideOuterClass.Ride ride) {
        this.id = ride.getId();
        this.start = new DSPosition(ride.getStart());
        this.destination = new DSPosition(ride.getDestination());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DSPosition getStart() {
        return start;
    }

    public DSPosition getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "DSRide{" +
                "id=" + id +
                ", start=" + start +
                ", destination=" + destination +
                '}';
    }
}
