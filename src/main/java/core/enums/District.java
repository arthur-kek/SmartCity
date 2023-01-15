package core.enums;

public enum District {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4);

    private final int value;

    District(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
