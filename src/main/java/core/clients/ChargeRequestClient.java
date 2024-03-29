package core.clients;

import core.entities.DSTaxi;
import grpc.protocols.PositionOuterClass;
import grpc.protocols.ServiceProtocolGrpc;
import grpc.protocols.ServiceProtocolOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import utils.Constants;
import utils.LogUtils;

import java.util.concurrent.TimeUnit;

public class ChargeRequestClient extends Thread {

    private DSTaxi mainTaxi;

    private ServiceProtocolOuterClass.ChargeResponse chargeResponse;

    public ChargeRequestClient(DSTaxi mainTaxi) {
        this.mainTaxi = mainTaxi;
    }

    public ServiceProtocolOuterClass.ChargeResponse getChargeAnswer() {
        return chargeResponse;
    }

    public ServiceProtocolOuterClass.ChargeRequest buildRequest() {
        return ServiceProtocolOuterClass.ChargeRequest.newBuilder()
                .setId(mainTaxi.getId())
                .setPort(mainTaxi.getPort())
                .setPosition(PositionOuterClass.Position.newBuilder()
                        .setX(mainTaxi.getPosition().getX())
                        .setY(mainTaxi.getPosition().getY())
                        .build())
                .setTs(LogUtils.getCurrentTSWithOffset())
                .build();
    }

    private void askForCharge() throws InterruptedException {
        String chargeManagerAddress = String.format("%s:%s", Constants.ADM_SERVER_HOSTNAME, Constants.CHARGE_MANAGER_DEFAULT_PORT);
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(chargeManagerAddress).usePlaintext().build();
        ServiceProtocolGrpc.ServiceProtocolStub stub = ServiceProtocolGrpc.newStub(channel);

        ServiceProtocolOuterClass.ChargeRequest request = buildRequest();
        stub.askForCharge(request, new StreamObserver<ServiceProtocolOuterClass.ChargeResponse>() {
            @Override
            public void onNext(ServiceProtocolOuterClass.ChargeResponse value) {
                chargeResponse = value;
            }

            @Override
            public void onError(Throwable t) {
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
            askForCharge();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
