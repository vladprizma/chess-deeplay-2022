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

public class TestQueenBitboardHandler {

    private final static SimpleLogicAppeal simpleLogicAppeal = new SimpleLogic();

    @Test
    public void testGetQueenMovesAtStartingPosition() {
        String fenNotation = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_QUEEN
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetQueenMovesAtMiddleGamePosition() {
        String fenNotation = "k7/N3r1R1/P1P1b2P/1P2Q1b1/4P3/1nN3K1/8/3r1r2 w - - 0 1";

        BitUtils.BitIndex startingPosition = E5_IDX;

        Set<MoveInfo> queenMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E6_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(C5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(G5_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(C7_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(B8_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN)
        ).collect(Collectors.toSet());

        assertEquals(queenMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_QUEEN
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetQueenMoves() {
        String fenNotation = "8/8/3pnB2/R3QK2/4pn2/2B1k3/8/8 w - - 0 1";

        BitUtils.BitIndex startingPosition = E5_IDX;

        Set<MoveInfo> queenMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D6_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E6_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(B5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(C5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E4_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F4_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_QUEEN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_QUEEN)
        ).collect(Collectors.toSet());

        assertEquals(queenMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_QUEEN
                ).collect(Collectors.toSet()));
    }
    
}
