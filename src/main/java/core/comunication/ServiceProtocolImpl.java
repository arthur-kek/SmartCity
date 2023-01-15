package core.comunication;

import core.entities.DSTaxi;
import core.services.masterServices.ChargeManagementService;
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
}
