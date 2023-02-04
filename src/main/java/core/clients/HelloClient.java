package core.clients;

import core.entities.DSTaxi;
import grpc.protocols.TaxiProtocolGrpc;
import grpc.protocols.TaxiProtocolOuterClass.HelloRequest;
import grpc.protocols.TaxiProtocolOuterClass.HelloResponse;
import grpc.protocols.PositionOuterClass.Position;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import utils.Constants;

import java.util.concurrent.TimeUnit;

public class HelloClient extends Thread {

    private DSTaxi mainTaxi;
    private DSTaxi otherTaxi;

    private HelloResponse helloResponse;

    public HelloClient(DSTaxi mainTaxi, DSTaxi otherTaxi) {
        this.mainTaxi = mainTaxi;
        this.otherTaxi = otherTaxi;
    }

    public DSTaxi getOtherTaxi() {
        return otherTaxi;
    }

    public HelloResponse getHelloResponse() {
        return helloResponse;
    }

    public HelloRequest buildRequest() {
        return HelloRequest.newBuilder()
                .setTaxiId(mainTaxi.getId())
                .setIpAddress(Constants.ADM_SERVER_HOSTNAME)
                .setPort(mainTaxi.getPort())
                .setPosition(Position.newBuilder()
                        .setX(mainTaxi.getPosition().getX())
                        .setY(mainTaxi.getPosition().getY())
                        .build())
                .setIsMaster(mainTaxi.isMaster())
                .build();
    }

    private void sayHello() throws InterruptedException {
        String targetTaxiAddress = String.format("%s:%s", Constants.ADM_SERVER_HOSTNAME, otherTaxi.getPort());
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(targetTaxiAddress).usePlaintext().build();
        TaxiProtocolGrpc.TaxiProtocolStub stub = TaxiProtocolGrpc.newStub(channel);

        HelloRequest hello = buildRequest();
        stub.sayHello(hello, new StreamObserver<HelloResponse>() {
            @Override
            public void onNext(HelloResponse value) {
                helloResponse = value;
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
            sayHello();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
