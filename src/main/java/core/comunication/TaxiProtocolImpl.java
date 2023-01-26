package core.comunication;

import core.entities.DSRide;
import core.entities.DSTaxi;
import core.enums.TaxiState;
import core.exceptions.ChargeStationException;
import core.exceptions.WrongTaxiStateException;
import grpc.protocols.TaxiProtocolGrpc.TaxiProtocolImplBase;
import grpc.protocols.TaxiProtocolOuterClass;
import grpc.protocols.TaxiProtocolOuterClass.HelloRequest;
import grpc.protocols.TaxiProtocolOuterClass.HelloResponse;
import grpc.protocols.TaxiProtocolOuterClass.PingRequest;
import grpc.protocols.TaxiProtocolOuterClass.PingResponse;
import grpc.protocols.TaxiProtocolOuterClass.MasterResponse;
import grpc.protocols.TaxiProtocolOuterClass.PropagateElectionResponse;
import grpc.protocols.TaxiProtocolOuterClass.NotifyRideElectionResponse;
import io.grpc.stub.StreamObserver;

public class TaxiProtocolImpl extends TaxiProtocolImplBase {

    private DSTaxi taxi;

    public TaxiProtocolImpl(DSTaxi taxi) {
        this.taxi = taxi;
    }

    private HelloResponse buildHelloResponse() {
        return HelloResponse.newBuilder()
                .setTaxiId(taxi.getId())
                .setMessage("Hello, Darkness")
                .setIsMaster(taxi.isMaster())
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

    private PropagateElectionResponse buildElectionResponse(String message) {
        return PropagateElectionResponse.newBuilder()
                .setConfirmMessage(message)
                .build();
    }

    private NotifyRideElectionResponse buildNotifyElectionResponse(String message) {
        return NotifyRideElectionResponse.newBuilder()
                .setResponse(message)
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
    public void propagateElection(TaxiProtocolOuterClass.PropagateElectionRequest request, StreamObserver<TaxiProtocolOuterClass.PropagateElectionResponse> responseObserver) {
        if (taxi.getState() != TaxiState.FREE) {
            responseObserver.onNext(buildElectionResponse("BUSY"));
            responseObserver.onCompleted();
        } else {
            DSRide ride = new DSRide(request.getRide());
            taxi.checkRideElection(ride, request.getCurrentCandidateId(), request.getCurrentCandidateBatteryLevel(), request.getDistance());
            responseObserver.onNext(buildElectionResponse("OK"));
            responseObserver.onCompleted();
        }
    }

    @Override
    public void notifyNewRideToTaxi(TaxiProtocolOuterClass.NotifyRideElection request, StreamObserver<TaxiProtocolOuterClass.NotifyRideElectionResponse> responseObserver) {
        if (taxi.getState() != TaxiState.FREE) {
            responseObserver.onNext(buildNotifyElectionResponse("BUSY"));
            responseObserver.onCompleted();
        } else {
            DSRide ride = new DSRide(request.getRide());
            taxi.initRideElection(ride);
            responseObserver.onNext(buildNotifyElectionResponse("OK"));
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
