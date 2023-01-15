package core.exceptions;

public class ChargeStationException extends Throwable {

    public String getMessage() {
        return "Current charge station state is BUSY, but it should be FREE";
    }
}
