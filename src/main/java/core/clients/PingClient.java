package core.clients;

import core.entities.DSTaxi;
import grpc.protocols.TaxiProtocolGrpc;
import grpc.protocols.TaxiProtocolOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import utils.Constants;

import java.util.concurrent.TimeUnit;

public class PingClient extends Thread {

    private DSTaxi mainTaxi;
    private DSTaxi otherTaxi;

    public DSTaxi getOtherTaxi() {
        return otherTaxi;
    }

    private TaxiProtocolOuterClass.PingResponse pingResponse;

    public PingClient(DSTaxi mainTaxi, DSTaxi otherTaxi) {
        this.mainTaxi = mainTaxi;
        this.otherTaxi = otherTaxi;
    }

    public TaxiProtocolOuterClass.PingResponse getPingResponse() {
        return pingResponse;
    }

    public TaxiProtocolOuterClass.PingRequest buildRequest() {
        return TaxiProtocolOuterClass.PingRequest.newBuilder()
                .setMessage("Alive?")
                .build();
    }

    private void ping() throws InterruptedException {
        String targetTaxiAddress = String.format("%s:%s", Constants.ADM_SERVER_HOSTNAME, otherTaxi.getPort());
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(targetTaxiAddress).usePlaintext().build();
        TaxiProtocolGrpc.TaxiProtocolStub stub = TaxiProtocolGrpc.newStub(channel);

        TaxiProtocolOuterClass.PingRequest ping = buildRequest();
        stub.ping(ping, new StreamObserver<TaxiProtocolOuterClass.PingResponse>() {
            @Override
            public void onNext(TaxiProtocolOuterClass.PingResponse value) {
                pingResponse = value;
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
            ping();
        } catch (Throwable t) {
            // TODO: Print some error message
        } finally {
            // TODO: Presentation with otherTaxiId is completed
        }
    }
}

