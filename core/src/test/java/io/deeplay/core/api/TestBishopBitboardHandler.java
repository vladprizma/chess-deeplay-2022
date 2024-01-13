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

public class TestBishopBitboardHandler {

    private final static SimpleLogicAppeal simpleLogicAppeal = new SimpleLogic();

    @Test
    public void testGetBishopMovesPinned() {
        String fenNotation = "8/2Q5/8/1N2b3/8/6k1/2K5/8 b - - 0 1";

        BitUtils.BitIndex startingPosition = E5_IDX;

        Set<MoveInfo> queenMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_BISHOP),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_BISHOP),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(C7_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.B_BISHOP)
        ).collect(Collectors.toSet());

        assertEquals(queenMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_BISHOP
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetBishopMovesPinnedVertically() {
        String fenNotation = "8/2r5/8/6k1/2B5/8/2K5/8 w - - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_BISHOP
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetBishopMovesPinnedHorizontally() {
        String fenNotation = "8/8/8/R1b3k1/8/8/2K5/8 b - - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_BISHOP
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetBishopMovesAtStartingPosition() {
        String fenNotation = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_BISHOP
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == F1_IDX.ordinal()
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetBishopMovesMiddleGame() {
        String fenNotation = "5rnk/K1P5/4B3/1p3P2/1Np5/8/3Q4/6R1 w - - 0 1";

        BitUtils.BitIndex startingPosition = E6_IDX;

        Set<MoveInfo> queenMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D7_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_BISHOP),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(C8_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_BISHOP),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F7_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_BISHOP),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(G8_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_BISHOP),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_BISHOP),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(C4_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_BISHOP)
        ).collect(Collectors.toSet());

        assertEquals(queenMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_BISHOP
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetBishopMovesClamped() {
        String fenNotation = "8/8/8/R3Q3/1k6/2b5/1r1p1K2/8 b - - 0 1";

        BitUtils.BitIndex startingPosition = C3_IDX;

        Set<MoveInfo> queenMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_BISHOP),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E5_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.B_BISHOP)
        ).collect(Collectors.toSet());

        assertEquals(queenMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_BISHOP
                ).collect(Collectors.toSet()));
    }

}
