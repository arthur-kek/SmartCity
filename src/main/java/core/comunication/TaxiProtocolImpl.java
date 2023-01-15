package core.comunication;

import core.entities.DSTaxi;
import core.exceptions.ChargeStationException;
import core.exceptions.WrongTaxiStateException;
import grpc.protocols.TaxiProtocolGrpc.TaxiProtocolImplBase;
import grpc.protocols.TaxiProtocolOuterClass;
import grpc.protocols.TaxiProtocolOuterClass.HelloRequest;
import grpc.protocols.TaxiProtocolOuterClass.HelloResponse;
import grpc.protocols.TaxiProtocolOuterClass.PingRequest;
import grpc.protocols.TaxiProtocolOuterClass.PingResponse;
import grpc.protocols.TaxiProtocolOuterClass.MasterResponse;
import io.grpc.stub.StreamObserver;

public class TaxiProtocolImpl extends TaxiProtocolImplBase {

    private DSTaxi taxi;

    public TaxiProtocolImpl(DSTaxi taxi) {
        this.taxi = taxi;
    }

    private HelloResponse buildHelloResponse() {
        return HelloResponse.newBuilder()
                .setMessage("Hello, Darkness")
                .build();
    }

    private PingResponse buildPingResponse() {
        return PingResponse.newBuilder()
                .setId(taxi.getId())
                .setMaster(taxi.isMaster())
                .build();
    }

    private MasterResponse buildMasterResponse() {
        return MasterResponse.newBuilder()
                .setOkMessage("OK")
                .build();
    }

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        DSTaxi otherTaxi = new DSTaxi(request);
        taxi.addNewTaxi(otherTaxi);
        responseObserver.onNext(buildHelloResponse());
        responseObserver.onCompleted();
    }

    @Override
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(buildPingResponse());
        responseObserver.onCompleted();
    }

    @Override
    public void setMaster(TaxiProtocolOuterClass.SetMasterRequest request, StreamObserver<TaxiProtocolOuterClass.MasterResponse> responseObserver) {
        if (request.getElected()) {
            taxi.setMaster();
            responseObserver.onNext(buildMasterResponse());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void sendToCharge(TaxiProtocolOuterClass.NotifyFreeStation request, StreamObserver<TaxiProtocolOuterClass.NotifyCharging> responseObserver) {
        if (request.getMessage().equals("OK")) {
            try {
                taxi.recharge();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ChargeStationException cse) {
                System.out.println(cse.getMessage());
            } catch (WrongTaxiStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
