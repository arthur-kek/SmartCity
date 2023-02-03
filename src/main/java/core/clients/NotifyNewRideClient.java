package core.clients;

import core.entities.DSRide;
import core.entities.DSTaxi;
import grpc.protocols.PositionOuterClass;
import grpc.protocols.RideOuterClass;
import grpc.protocols.ServiceProtocolGrpc;
import grpc.protocols.ServiceProtocolOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import utils.Constants;
import utils.LogUtils;

import java.util.concurrent.TimeUnit;

public class NotifyNewRideClient extends Thread {

    private DSTaxi taxi;
    private DSRide ride;
    private int topicId;

    private ServiceProtocolOuterClass.NewRideResponse newRideResponse;

    public ServiceProtocolOuterClass.NewRideResponse getNewRideResponse() {
        return newRideResponse;
    }

    public NotifyNewRideClient(DSTaxi taxi, DSRide ride, int topicId) {
        this.taxi = taxi;
        this.ride = ride;
        this.topicId = topicId;
    }

    public ServiceProtocolOuterClass.NotifyNewRideToServer buildRequest() {
        return ServiceProtocolOuterClass.NotifyNewRideToServer.newBuilder()
                .setRide(RideOuterClass.Ride.newBuilder()
                        .setId(ride.getId())
                        .setStart(PositionOuterClass.Position.newBuilder()
                                .setX(ride.getStart().getX())
                                .setY(ride.getStart().getY())
                                .build())
                        .setDestination(PositionOuterClass.Position.newBuilder()
                                .setX(ride.getDestination().getX())
                                .setY(ride.getDestination().getY())
                                .build())
                        .setDistrictId(ride.getRideDistrictId()))
                .setTopic(topicId)
                .build();

    }

    private void notifyNewRide() throws InterruptedException {
        String rideManagerAddress = String.format("%s:%s", Constants.ADM_SERVER_HOSTNAME, Constants.RIDE_MANAGER_DEFAULT_PORT);
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(rideManagerAddress).usePlaintext().build();
        ServiceProtocolGrpc.ServiceProtocolStub stub = ServiceProtocolGrpc.newStub(channel);

        ServiceProtocolOuterClass.NotifyNewRideToServer request = buildRequest();
        stub.notifyNewRide(request, new StreamObserver<ServiceProtocolOuterClass.NewRideResponse>() {
            @Override
            public void onNext(ServiceProtocolOuterClass.NewRideResponse value) {
                newRideResponse = value;
            }

            @Override
            public void onError(Throwable t) {
                // TODO: Print some error message
                channel.shutdownNow();
            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }
        });
        channel.awaitTermination(30, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            // TODO: Print start presentation
            notifyNewRide();
        } catch (Throwable t) {
            // TODO: Print some error message
        } finally {
            // TODO: Presentation with otherTaxiId is completed
        }
    }
}
