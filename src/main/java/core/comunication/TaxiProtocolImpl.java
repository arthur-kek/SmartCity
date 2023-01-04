package core.comunication;

import core.entities.DSTaxi;
import grpc.protocols.TaxiProtocolGrpc.TaxiProtocolImplBase;
import grpc.protocols.TaxiProtocolOuterClass.HelloRequest;
import grpc.protocols.TaxiProtocolOuterClass.HelloResponse;
import grpc.protocols.TaxiProtocolOuterClass.PingRequest;
import grpc.protocols.TaxiProtocolOuterClass.PingResponse;
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
}
