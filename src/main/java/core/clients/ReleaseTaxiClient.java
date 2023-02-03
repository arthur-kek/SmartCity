package core.clients;

import core.entities.DSTaxi;
import grpc.protocols.TaxiProtocolGrpc;
import grpc.protocols.TaxiProtocolOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import utils.Constants;

import java.util.concurrent.TimeUnit;

public class ReleaseTaxiClient extends Thread {

    private DSTaxi mainTaxi;
    private DSTaxi otherTaxi;

    private TaxiProtocolOuterClass.releaseTaxiResponse releaseTaxiResponse;

    public ReleaseTaxiClient(DSTaxi mainTaxi, DSTaxi otherTaxi) {
        this.mainTaxi = mainTaxi;
        this.otherTaxi = otherTaxi;
    }

    public TaxiProtocolOuterClass.releaseTaxiResponse getReleaseTaxiResponse() {
        return releaseTaxiResponse;
    }

    public TaxiProtocolOuterClass.releaseTaxiRequest buildRequest() {
        return TaxiProtocolOuterClass.releaseTaxiRequest.newBuilder()
                .setReleaseMessage("RELEASED")
                .build();
    }

    private void releaseTaxi() throws InterruptedException {
        String targetTaxiAddress = String.format("%s:%s", Constants.ADM_SERVER_HOSTNAME, otherTaxi.getPort());
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(targetTaxiAddress).usePlaintext().build();
        TaxiProtocolGrpc.TaxiProtocolStub stub = TaxiProtocolGrpc.newStub(channel);

        TaxiProtocolOuterClass.releaseTaxiRequest ping = buildRequest();
        stub.releaseTaxi(ping, new StreamObserver<TaxiProtocolOuterClass.releaseTaxiResponse>() {
            @Override
            public void onNext(TaxiProtocolOuterClass.releaseTaxiResponse value) {
                releaseTaxiResponse = value;
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
            releaseTaxi();
        } catch (Throwable t) {
            // TODO: Print some error message
        } finally {
            // TODO: Presentation with otherTaxiId is completed
        }
    }



}
