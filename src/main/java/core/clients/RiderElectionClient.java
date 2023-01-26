package core.clients;

import core.entities.DSRide;
import core.entities.DSTaxi;
import grpc.protocols.PositionOuterClass;
import grpc.protocols.RideOuterClass;
import grpc.protocols.TaxiProtocolGrpc;
import grpc.protocols.TaxiProtocolOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import utils.Constants;

import java.util.concurrent.TimeUnit;

public class RiderElectionClient extends Thread {
    private DSTaxi targetTaxi;
    private DSRide ride;
    private int currentBestId;
    private int currentBestBatteryLevel;
    private double distance;

    private TaxiProtocolOuterClass.PropagateElectionResponse response;

    public TaxiProtocolOuterClass.PropagateElectionResponse getResponse() {
        return response;
    }

    public RiderElectionClient(DSTaxi targetTaxi, DSRide ride, int currentBestId, int currentBestBatteryLevel, double distance) {
        this.targetTaxi = targetTaxi;
        this.ride = ride;
        this.currentBestId = currentBestId;
        this.currentBestBatteryLevel = currentBestBatteryLevel;
        this.distance = distance;
    }

    public TaxiProtocolOuterClass.PropagateElectionRequest buildRequest() {
        return TaxiProtocolOuterClass.PropagateElectionRequest.newBuilder()
                .setRide(RideOuterClass.Ride.newBuilder()
                        .setId(ride.getId())
                        .setStart(PositionOuterClass.Position.newBuilder()
                                .setX(ride.getStart().getX())
                                .setY(ride.getStart().getY())
                                .build())
                        .setDestination(PositionOuterClass.Position.newBuilder()
                                .setX(ride.getDestination().getX())
                                .setY(ride.getDestination().getY())
                                .build()))
                .setCurrentCandidateId(currentBestId)
                .setCurrentCandidateBatteryLevel(currentBestBatteryLevel)
                .setDistance(distance)
                .build();
    }

    private void propagate() throws InterruptedException {
        String targetTaxiAddress = String.format("%s:%s", Constants.ADM_SERVER_HOSTNAME, targetTaxi.getPort());
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(targetTaxiAddress).usePlaintext().build();
        TaxiProtocolGrpc.TaxiProtocolStub stub = TaxiProtocolGrpc.newStub(channel);

        TaxiProtocolOuterClass.PropagateElectionRequest request = buildRequest();

        stub.propagateElection(request, new StreamObserver<TaxiProtocolOuterClass.PropagateElectionResponse>() {
            @Override
            public void onNext(TaxiProtocolOuterClass.PropagateElectionResponse value) {
                response = value;
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
            propagate();
        } catch (Throwable t) {
            // TODO: Print some error message
        } finally {
            // TODO: Presentation with otherTaxiId is completed
        }
    }
}
