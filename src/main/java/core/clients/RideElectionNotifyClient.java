package core.clients;

import core.entities.DSRide;
import core.entities.DSTaxi;

import core.services.masterServices.RideManagementService;
import grpc.protocols.PositionOuterClass;
import grpc.protocols.RideOuterClass;
import grpc.protocols.TaxiProtocolGrpc;
import grpc.protocols.TaxiProtocolOuterClass;
import grpc.protocols.TaxiProtocolOuterClass.NotifyRideElectionResponse;
import grpc.protocols.TaxiProtocolOuterClass.NotifyRideElection;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import utils.Constants;

import java.util.concurrent.TimeUnit;

public class RideElectionNotifyClient extends Thread {

    private RideManagementService rideManagementService;
    private DSTaxi taxi;
    private DSRide ride;

    private NotifyRideElectionResponse response;

    public RideElectionNotifyClient(DSTaxi taxi, DSRide ride, RideManagementService rideManagementService) {
        this.taxi = taxi;
        this.ride = ride;
        this.rideManagementService = rideManagementService;
    }

    public NotifyRideElectionResponse getResponse() {
        return response;
    }

    public NotifyRideElection buildRequest() {
        return NotifyRideElection.newBuilder()
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
                .build();
    }

    private void notifyTaxi() throws InterruptedException {
        String targetTaxiAddress = String.format("%s:%s", Constants.ADM_SERVER_HOSTNAME, taxi.getPort());
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(targetTaxiAddress).usePlaintext().build();
        TaxiProtocolGrpc.TaxiProtocolStub stub = TaxiProtocolGrpc.newStub(channel);

        NotifyRideElection request = buildRequest();

        stub.notifyNewRideToTaxi(request, new StreamObserver<TaxiProtocolOuterClass.NotifyRideElectionResponse>() {
            @Override
            public void onNext(TaxiProtocolOuterClass.NotifyRideElectionResponse value) {
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
            notifyTaxi();
        } catch (Throwable t) {
            // TODO: Print some error message
        } finally {
            // TODO: Presentation with otherTaxiId is completed
        }
    }


}
