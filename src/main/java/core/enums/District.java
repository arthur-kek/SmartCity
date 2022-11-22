package core.enums;

public enum District {

    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4");

    private final String value;

    District(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
