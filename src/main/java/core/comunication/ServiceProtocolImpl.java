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
/*
    @Override
    public void electRider(ServiceProtocolOuterClass.ElectRiderRequest request, StreamObserver<ServiceProtocolOuterClass.ElectRiderResponse> responseObserver) {
        return ServiceProtocolOuterClass.ElectRiderResponse.newBuilder()
                .se
    }*/

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
        DSRide newRide = new DSRide(request.getRide());
        if (service instanceof RideManagementService) {
            String response = ((RideManagementService) service).addRideToQueue(newRide, request.getTopic());
            responseObserver.onNext(buildNewRideResponse(response));
            responseObserver.onCompleted();
        }
    }
}
