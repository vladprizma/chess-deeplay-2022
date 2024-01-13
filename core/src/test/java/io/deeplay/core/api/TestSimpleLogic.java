package io.deeplay.core.api;

import io.deeplay.core.model.Coord;
import io.deeplay.core.model.Figure;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.MoveType;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.deeplay.core.logic.BitUtils.BitIndex.*;
import static org.junit.Assert.*;

public class TestSimpleLogic {
    private final static SimpleLogicAppeal simpleLogicAppeal = new SimpleLogic();

    @Test
    public void testGetMovesIsEmptyBecauseItsOtherSideTurn() {
        String fenNotation = "1nbqkbn1/Pp2p1p1/7p/p5r1/7P/2p5/1PPPPPPp/RNBQKBN1 w - - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == H2_IDX.ordinal()
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetMovesKnightCanAttackThreatToSaveKing() {
        String fenNotation = "7q/1k6/8/5N2/4K2r/8/8/2b5 w - - 0 1";

        Set<MoveInfo> saveKingMoves = Stream.of(
                new MoveInfo(new Coord(E4_IDX.ordinal()), new Coord(F3_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E4_IDX.ordinal()), new Coord(D5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E4_IDX.ordinal()), new Coord(D3_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(F5_IDX.ordinal()), new Coord(H4_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_KNIGHT)
        ).collect(Collectors.toSet());

        assertEquals(saveKingMoves, simpleLogicAppeal.getMoves(fenNotation));
    }

    @Test
    public void testGetMovesOnlyKingCanMoveBecauseOfTwoChecks() { // Тут коню невозможно срубить ладью, т.к. двойной шах
        String fenNotation = "7q/1k6/8/5N2/4K2r/2n5/8/2b5 w - - 0 1";

        Set<MoveInfo> saveKingMoves = Stream.of(
                new MoveInfo(new Coord(E4_IDX.ordinal()), new Coord(F3_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E4_IDX.ordinal()), new Coord(D3_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING)
        ).collect(Collectors.toSet());

        assertEquals(saveKingMoves, simpleLogicAppeal.getMoves(fenNotation));
    }

    @Test
    public void testGetMovesEvenIfOpponentPieceIsPinnedHisAttackSquaresCheckKing() {
        String fenNotation = "8/3Q4/8/3r2K1/8/8/8/3k4 w - - 0 1";

        assertEquals(6, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).count());
    }

    @Test
    public void testMateIsFalseBecauseQueenCanSaveKing() {
        String fenNotation = "8/8/3r4/k7/2r1r3/1Q6/3K4/3N3B w - - 0 1";
        assertFalse(simpleLogicAppeal.isMate(fenNotation));
        MoveInfo saveKingMove = new MoveInfo(
                new Coord(B3_IDX.ordinal()),
                new Coord(D3_IDX.ordinal()),
                MoveType.USUAL_MOVE, Figure.W_QUEEN);
        assertTrue(simpleLogicAppeal.getMoves(fenNotation).contains(saveKingMove));
    }

    @Test
    public void testMateIsTrueBecauseKingStuckUnderCheck() {
        String fenNotation = "8/8/3r4/k7/2r1r3/8/3K4/3N3B w - - 0 1";
        assertTrue(simpleLogicAppeal.isMate(fenNotation));
        assertTrue(simpleLogicAppeal.getMoves(fenNotation).isEmpty());
    }

    @Test
    public void testMateIsFalseBecauseBishopCanSaveKing() {
        String fenNotation = "5B2/8/3r4/k7/2r1r3/8/3K4/3N3B w - - 0 1";
        assertFalse(simpleLogicAppeal.isMate(fenNotation));
        MoveInfo saveKingMove = new MoveInfo(
                new Coord(F8_IDX.ordinal()),
                new Coord(D6_IDX.ordinal()),
                MoveType.USUAL_ATTACK, Figure.W_BISHOP);
        assertTrue(simpleLogicAppeal.getMoves(fenNotation).contains(saveKingMove));
    }

    @Test
    public void testMateIsFalseBecauseCanAttackKnight() {
        String fenNotation = "8/8/8/k7/2r1r3/3N1n2/3K4/3N3B w - - 0 1";
        assertFalse(simpleLogicAppeal.isMate(fenNotation));
        MoveInfo saveKingMove = new MoveInfo(
                new Coord(H1_IDX.ordinal()),
                new Coord(F3_IDX.ordinal()),
                MoveType.USUAL_ATTACK, Figure.W_BISHOP);
        assertTrue(simpleLogicAppeal.getMoves(fenNotation).contains(saveKingMove));
    }

    @Test
    public void testStalemateIsTrueBlackSideHasNoMoves() {
        String fenNotation = "4k3/8/3Q3N/8/8/8/8/3K4 b - - 0 1";
        assertTrue(simpleLogicAppeal.isStalemate(fenNotation));
        assertTrue(simpleLogicAppeal.getMoves(fenNotation).isEmpty());
    }


}
