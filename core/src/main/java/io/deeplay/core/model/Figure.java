package io.deeplay.core.model;

public enum Figure {
    W_PAWN(1, false),
    W_KNIGHT(2, false),
    W_BISHOP(3, false),
    W_ROOK(5, false),
    W_QUEEN(9, false),
    W_KING(10, false),
    B_PAWN(-1, false),
    B_KNIGHT(-2, false),
    B_BISHOP(-3, false),
    B_ROOK(-5, false),
    B_QUEEN(-9, false),
    B_KING(-10, false),
    NONE(0, false);

    public final int weight;
    private boolean moved;

    Figure(final int weight, final boolean moved) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
