package core.clients;

import core.entities.DSTaxi;
import core.services.masterServices.ChargeManagementService;
import grpc.protocols.TaxiProtocolGrpc;
import grpc.protocols.TaxiProtocolOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import utils.Constants;
import utils.LogUtils;

import java.util.concurrent.TimeUnit;

public class FreeStationNotifyClient extends Thread {

    private ChargeManagementService chargeManagementService;
    private DSTaxi otherTaxi;

    private TaxiProtocolOuterClass.NotifyCharging notifyCharging;

    public FreeStationNotifyClient(ChargeManagementService chargeManagementService, DSTaxi otherTaxi) {
        this.chargeManagementService = chargeManagementService;
        this.otherTaxi = otherTaxi;
    }

    public TaxiProtocolOuterClass.NotifyCharging getNotifyCharging() {
        return notifyCharging;
    }

    public TaxiProtocolOuterClass.NotifyFreeStation buildRequest() {
        return TaxiProtocolOuterClass.NotifyFreeStation.newBuilder()
                .setMessage("OK")
                .build();
    }

    private void sendToCharge() throws InterruptedException {
        String targetTaxiAddress = String.format("%s:%s", Constants.ADM_SERVER_HOSTNAME, otherTaxi.getPort());
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(targetTaxiAddress).usePlaintext().build();
        TaxiProtocolGrpc.TaxiProtocolStub stub = TaxiProtocolGrpc.newStub(channel);

        TaxiProtocolOuterClass.NotifyFreeStation request = buildRequest();

        stub.sendToCharge(request, new StreamObserver<TaxiProtocolOuterClass.NotifyCharging>() {
            @Override
            public void onNext(TaxiProtocolOuterClass.NotifyCharging value) {
                notifyCharging = value;
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
            sendToCharge();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
