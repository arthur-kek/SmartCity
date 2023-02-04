package core.clients;

import core.entities.DSTaxi;
import grpc.protocols.TaxiProtocolGrpc;
import grpc.protocols.TaxiProtocolOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import utils.Constants;

import java.util.concurrent.TimeUnit;

public class MasterElectionClient extends Thread { ;
    private DSTaxi taxi;

    public DSTaxi getTaxi() {
        return taxi;
    }

    private TaxiProtocolOuterClass.MasterResponse masterResponse;

    public MasterElectionClient( DSTaxi taxi) {
        this.taxi = taxi;
    }

    public TaxiProtocolOuterClass.MasterResponse getMasterResponse() {
        return masterResponse;
    }

    public TaxiProtocolOuterClass.SetMasterRequest buildRequest() {
        return TaxiProtocolOuterClass.SetMasterRequest.newBuilder()
                .setElected(true)
                .build();
    }

    private void setMaster() throws InterruptedException {
        String targetTaxiAddress = String.format("%s:%s", Constants.ADM_SERVER_HOSTNAME, taxi.getPort());
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(targetTaxiAddress).usePlaintext().build();
        TaxiProtocolGrpc.TaxiProtocolStub stub = TaxiProtocolGrpc.newStub(channel);

        TaxiProtocolOuterClass.SetMasterRequest ping = buildRequest();
        stub.setMaster(ping, new StreamObserver<TaxiProtocolOuterClass.MasterResponse>() {
            @Override
            public void onNext(TaxiProtocolOuterClass.MasterResponse value) {
                masterResponse = value;
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
            setMaster();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
