package io.deeplay.core.model;

import java.util.Objects;

public class MoveInfo {

    private final Coord cellFrom;
    private final Coord cellTo;
    private final MoveType moveType;
    private final Figure figure;
    private final Figure promoteTo;

    public MoveInfo(final Coord cellFrom,
                    final Coord cellTo,
                    final MoveType moveType,
                    final Figure figure,
                    final Figure promoteTo) {
        this.cellFrom = cellFrom;
        this.cellTo = cellTo;
        this.moveType = moveType;
        this.figure = figure;
        this.promoteTo = promoteTo;
    }

    public MoveInfo(final Coord cellFrom, final Coord cellTo, final MoveType moveType, final Figure figure) {
        this(cellFrom, cellTo, moveType, figure, null);
    }

    public MoveInfo() {
        this.cellFrom = null;
        this.cellTo = null;
        this.moveType = null;
        this.figure = null;
        this.promoteTo = null;
    }

    public Coord getCellFrom() {
        return cellFrom;
    }

    public Coord getCellTo() {
        return cellTo;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Figure getFigure() {
        return figure;
    }

    public Figure getPromoteTo() {
        return promoteTo;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveInfo moveInfo = (MoveInfo) o;
        return Objects.equals(cellFrom, moveInfo.cellFrom) && Objects.equals(cellTo, moveInfo.cellTo) && moveType == moveInfo.moveType && figure == moveInfo.figure && promoteTo == moveInfo.promoteTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellFrom, cellTo, moveType, figure, promoteTo);
    }

    @Override
    public String toString() {
        return "MoveInfo{" +
                "cellFrom=" + cellFrom +
                ", cellTo=" + cellTo +
                ", moveType=" + moveType +
                ", figure=" + figure +
                ", promoteTo=" + promoteTo +
                '}';
    }
}
