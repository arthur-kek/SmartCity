package core.entities;

public class DSTaxiOrdered {

    private DSTaxi taxi;
    private long ts;

    public DSTaxiOrdered(DSTaxi taxi, long ts) {
        this.taxi = taxi;
        this.ts = ts;
    }

    public DSTaxi getTaxi() {
        return taxi;
    }

    public long getTs() {
        return ts;
    }
}
