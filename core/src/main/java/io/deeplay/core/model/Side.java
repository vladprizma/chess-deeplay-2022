package io.deeplay.core.model;

public enum Side {
    WHITE("w", "Белые"),
    BLACK("b", "Черные");

    public String toString() {
        return side;
    }

    private final String side;
    private final String description;

    Side(final String side, final String description) {
        this.side = side;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Side otherSide(final Side side){
        return side == WHITE ? BLACK : WHITE;
    }

}
