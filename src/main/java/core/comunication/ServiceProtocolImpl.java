package core.comunication;

import core.entities.DSRide;
import core.entities.DSTaxi;
import core.services.masterServices.ChargeManagementService;
import core.services.masterServices.RideManagementService;
import grpc.protocols.ServiceProtocolGrpc;
import grpc.protocols.ServiceProtocolOuterClass;
import io.grpc.stub.StreamObserver;
import utils.Constants;

public class ServiceProtocolImpl extends ServiceProtocolGrpc.ServiceProtocolImplBase {

    private Thread service;

    public ServiceProtocolImpl(Thread service) {
        this.service = service;
    }

    private ServiceProtocolOuterClass.ChargeResponse buildChargeResponse(String message) {
        return ServiceProtocolOuterClass.ChargeResponse.newBuilder()
                .setResponse(message)
                .build();
    }

    private ServiceProtocolOuterClass.NewRideResponse buildNewRideResponse(String message) {
        return ServiceProtocolOuterClass.NewRideResponse.newBuilder()
                .setMessage(message)
                .build();
    }

    @Override
    public void askForCharge(ServiceProtocolOuterClass.ChargeRequest request, StreamObserver<ServiceProtocolOuterClass.ChargeResponse> responseObserver) {
        DSTaxi taxi = new DSTaxi(request);
        if (service instanceof ChargeManagementService) {
            String response = ((ChargeManagementService) service).addTaxiToChargeQueue(taxi);
            responseObserver.onNext(buildChargeResponse(response));
            responseObserver.onCompleted();
        }
    }

    @Override
    public void notifyNewRide(ServiceProtocolOuterClass.NotifyNewRideToServer request, StreamObserver<ServiceProtocolOuterClass.NewRideResponse> responseObserver) {
        DSRide ride = new DSRide(request.getRide());
        String response;
        if (service instanceof RideManagementService) {
            if (request.getElection()) {
                response = ((RideManagementService) service).removeRideFromQueue(request.getTopic(), ride);
            } else {
                ((RideManagementService) service).addRideToQueue(ride, request.getTopic());
                response = "OK";

            }
            responseObserver.onNext(buildNewRideResponse(response));
            responseObserver.onCompleted();
        }
    }
}
