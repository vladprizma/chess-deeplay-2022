package io.deeplay.core.api;

import io.deeplay.core.logic.BitUtils;
import io.deeplay.core.model.Coord;
import io.deeplay.core.model.Figure;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.MoveType;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.deeplay.core.logic.BitUtils.BitIndex.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestKnightBitboardHandler {

    private final static SimpleLogicAppeal simpleLogicAppeal = new SimpleLogic();

    @Test
    public void testGetMovesHorizontallyPinnedKnight() {
        String fenNotation = "8/2R5/8/8/1r2N2K/8/8/3k4 w - - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KNIGHT
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetMovesVerticallyPinnedKnight() {
        String fenNotation = "8/3R4/8/8/3n4/8/8/K2k4 b - - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_KNIGHT
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetWhiteKnightMovesAtStartingPosition() {
        String fenNotation = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        BitUtils.BitIndex startingPosition = B1_IDX;

        Set<MoveInfo> knightMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(A3_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(C3_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KNIGHT)
        ).collect(Collectors.toSet());

        assertEquals(knightMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KNIGHT
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetBlackKnightMovesAtStartingPosition() {
        String fenNotation = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";

        BitUtils.BitIndex startingPosition = G8_IDX;

        Set<MoveInfo> knightMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(H6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_KNIGHT)
        ).collect(Collectors.toSet());

        assertEquals(knightMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_KNIGHT
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetWhiteKnightMovesAttacking() {
        /*
         * Check possible F7 knight's moves after
         * 1.e4e5 2.Nf3Nc6 3.Bc4Nf6 4.Ng5h6 5.Nxf7Nxe4
         * Expected:D8 D6 E5 G5 H6 H8
         */
        String fenNotation = "r1bqkb1r/pppp1Np1/2n4p/4p3/2B1n3/8/PPPP1PPP/RNBQK2R w KQkq - 0 1";

        BitUtils.BitIndex startingPosition = F7_IDX;

        Set<MoveInfo> knightMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(H8_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D8_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E5_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(G5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(H6_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_KNIGHT)
        ).collect(Collectors.toSet());

        assertEquals(knightMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KNIGHT
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetBlackKnightMovesAttacking() {
        /*
         * Check possible C6 knight's moves after
         * 1.e4e5 2.Nf3Nc6 3.Bc4Nf6 4.Ng5h6 5.Nxf7Nxe4 6.Nxd8
         * Expected:B8 D8 E7 D4 B4 A5
         */
        String fenNotation = "r1bNkb1r/pppp2p1/2n4p/4p3/2B1n3/8/PPPP1PPP/RNBQK2R b KQkq - 0 1";

        BitUtils.BitIndex startingPosition = C6_IDX;

        Set<MoveInfo> knightMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(B8_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D8_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.B_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E7_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(B4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_KNIGHT),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(A5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_KNIGHT)
        ).collect(Collectors.toSet());

        assertEquals(knightMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_KNIGHT
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

}
