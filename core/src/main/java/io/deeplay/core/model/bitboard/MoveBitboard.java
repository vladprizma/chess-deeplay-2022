package io.deeplay.core.model.bitboard;

import io.deeplay.core.model.MoveType;

import java.util.Objects;

/**
 * Тип хода и сама позиция в виде битборда.
 * Выделение такого класса необходимо из-за того что некоторые фигуры имеют больше 2-х типов ходов (пешка, король).
 */
public class MoveBitboard {
    private final MoveType moveType;
    /**
     * 1 бит и 63 нуля, где 1 - возможный ход фигуры.
     */
    private final long moveBitboard;

    public MoveBitboard(MoveType moveType, long moveBitboard) {
        this.moveType = moveType;
        this.moveBitboard = moveBitboard;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public long getMoveBitboard() {
        return moveBitboard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveBitboard that = (MoveBitboard) o;
        return moveBitboard == that.moveBitboard && moveType == that.moveType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moveType, moveBitboard);
    }

    @Override
    public String toString() {
        return "MoveBitboard{" +
                "moveType=" + moveType +
                ", moveBitboard=" + moveBitboard +
                '}';
    }
}
