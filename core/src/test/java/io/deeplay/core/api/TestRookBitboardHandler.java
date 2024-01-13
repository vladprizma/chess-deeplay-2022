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

// Мегаполезная штука: https://www.dailychess.com/chess/chess-fen-viewer.php - можно расставлять фигуры + есть FEN
// Можно так же использовать и lichess.org для генерации нотации FEN
public class TestRookBitboardHandler {
    private final static SimpleLogicAppeal simpleLogicAppeal = new SimpleLogic();

    @Test
    public void testGetRookMovesPinned() {
        String fenNotation = "8/8/8/1R1r2k1/8/8/K7/8 b - - 0 1";

        BitUtils.BitIndex startingPosition = D5_IDX;

        Set<MoveInfo> queenMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(B5_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.B_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(C5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_ROOK)
        ).collect(Collectors.toSet());

        assertEquals(queenMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_ROOK
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetRookMovesPinnedDiagonally() {
        String fenNotation = "8/8/8/4Q2K/8/2r5/1k6/8 b - - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_ROOK
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetRookMovesAtStartingPosition() {
        String fenNotation = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_ROOK
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == A1_IDX.ordinal()
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetRookMovesAtEndGamePosition() {
        String fenNotation = "8/K1P1Rp2/8/8/8/4nk2/8/8 w - - 0 1";

        BitUtils.BitIndex startingPosition = E7_IDX;

        Set<MoveInfo> queenMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E8_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D7_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E3_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F7_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_ROOK)
        ).collect(Collectors.toSet());

        assertEquals(queenMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_ROOK
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetRookMovesClamped() {
        String fenNotation = "8/1k2Nrn1/5Q2/8/8/8/5K2/8 b - - 0 1";

        BitUtils.BitIndex startingPosition = F7_IDX;

        Set<MoveInfo> queenMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F8_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F6_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.B_ROOK),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E7_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.B_ROOK)
        ).collect(Collectors.toSet());

        assertEquals(queenMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_ROOK
                ).collect(Collectors.toSet()));
    }

}
