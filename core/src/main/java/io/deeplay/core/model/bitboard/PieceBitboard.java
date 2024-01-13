package io.deeplay.core.model.bitboard;

import io.deeplay.core.logic.newlogic.QuadFunction;
import io.deeplay.core.model.Figure;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;

import java.util.Set;
import java.util.function.BiFunction;

/**
 * Класс фигуры, фигура получает информацию о виде и о том как считать ходы.
 */
public class PieceBitboard {

    private final Side side;
    private final Figure figure;
    private final int positionIndex;
    private final long positionBitboard;
    private final QuadFunction<ChessBitboard, Long, Long, Integer, Long> movesGenerationFunction;
    private final BiFunction<ChessBitboard, PieceBitboard, Set<MoveInfo>> wrappedMovesGenerationFunction;
    private long allMovesBitboard;
    /**
     * В данное поле добавляется информация о допустимых для хода клетках.
     */
    private long allRestrictionsBitboard;

    public PieceBitboard(final Side side,
                         final Figure figure,
                         final int positionIndex,
                         final long positionBitboard,
                         final QuadFunction<ChessBitboard, Long, Long, Integer, Long> movesGenerationFunction,
                         final BiFunction<ChessBitboard, PieceBitboard, Set<MoveInfo>> wrappedMovesGenerationFunction) {
        this.side = side;
        this.figure = figure;
        this.positionIndex = positionIndex;
        this.positionBitboard = positionBitboard;
        this.movesGenerationFunction = movesGenerationFunction;
        this.wrappedMovesGenerationFunction = wrappedMovesGenerationFunction;
        this.allRestrictionsBitboard = ~0L; // изначально ограничений - нет, ходить можно везде
    }

    public void initializeMoves(final ChessBitboard chessBitboard) {
        // ~0L - нету ограничений
        this.allMovesBitboard = movesGenerationFunction.apply(chessBitboard, 0L, ~0L, positionIndex);
    }

    public long getMoves(final ChessBitboard chessBitboard, final long ignorePiece, final long restriction) {
        return movesGenerationFunction.apply(chessBitboard, ignorePiece, restriction, positionIndex);
    }

    public long getMovesWithIgnoredPiece(final ChessBitboard chessBitboard, final long ignorePiece) {
        return getMoves(chessBitboard, ignorePiece, ~0L);
    }

    public long getMovesUnderRestrictions(final ChessBitboard chessBitboard) {
        return getMoves(chessBitboard, 0L, allRestrictionsBitboard);
    }

    public Set<MoveInfo> getWrappedMovesUnderRestriction(final ChessBitboard chessBitboard) {
        return wrappedMovesGenerationFunction.apply(chessBitboard, this);
    }

    public void addRestriction(final long restriction) {
        this.allRestrictionsBitboard &= restriction;
    }

    public Side getSide() {
        return side;
    }

    public Figure getFigure() {
        return figure;
    }

    public int getPositionIndex() {
        return positionIndex;
    }

    public long getPositionBitboard() {
        return positionBitboard;
    }

    public long getAllMovesBitboard() {
        return allMovesBitboard;
    }

    public long getAllRestrictionsBitboard() {
        return allRestrictionsBitboard;
    }
}
