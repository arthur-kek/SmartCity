package core.exceptions;

import core.enums.TaxiState;

public class WrongTaxiStateException extends Throwable{

    private final TaxiState currentState;
    private final TaxiState stateItShouldBeIn;

    public WrongTaxiStateException(TaxiState currentState, TaxiState stateItShouldBeIn) {
        this.currentState = currentState;
        this.stateItShouldBeIn = stateItShouldBeIn;
    }

    public String getMessage() {
        return String.format("Current taxi state is %s, but it should be %s%n", currentState, stateItShouldBeIn);
    }
}
