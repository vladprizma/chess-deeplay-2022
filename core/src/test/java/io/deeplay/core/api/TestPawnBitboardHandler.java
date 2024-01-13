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

public class TestPawnBitboardHandler {
    private final static SimpleLogicAppeal simpleLogicAppeal = new SimpleLogic();

    @Test
    public void testGetMovesHorizontallyPinnedBlackPawn() {
        String fenNotation = "8/8/8/2k2p1R/4P1N1/8/8/K7 b - - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == E4_IDX.ordinal()
                ).collect(Collectors.toSet()).isEmpty());
    }

    @Test
    public void testGetMovesVerticallyPinnedWhitePawn() {
        String fenNotation = "k2r4/8/8/2q1n3/3P4/8/8/3K4 w - - 0 1";

        BitUtils.BitIndex startingPosition = D4_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetWhitePawnMovesGameBeginning() {
        String fenNotation = "rnbqkbnr/ppp1p1pp/8/3p1p2/4P3/1P6/P1PP1PPP/RNBQKBNR w KQkq - 0 1";

        BitUtils.BitIndex startingPosition = E4_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D5_IDX.ordinal()), MoveType.PAWN_ATTACK, Figure.W_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F5_IDX.ordinal()), MoveType.PAWN_ATTACK, Figure.W_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E5_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetBlackPawnMovesGameBeginning() {
        String fenNotation = "rnbqkbnr/ppp1p1pp/8/3p1p2/4P3/1P6/P1PP1PPP/RNBQKBNR b KQkq - 0 1";
        BitUtils.BitIndex startingPosition = F5_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E4_IDX.ordinal()), MoveType.PAWN_ATTACK, Figure.B_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(F4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetBlackPawnMovesBeginningPosition() {
        String fenNotation = "rnbqkbnr/ppp1p1pp/8/3p1p2/4P3/1P6/P1PP1PPP/RNBQKBNR b KQkq - 0 1";
        BitUtils.BitIndex startingPosition = H7_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(H6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(H5_IDX.ordinal()), MoveType.PAWN_LONG_MOVE, Figure.B_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetWhitePawnMovesNotAtBeginningPosition() {
        String fenNotation = "rnbqkbnr/ppp1p1pp/8/3p1p2/4P3/1P6/P1PP1PPP/RNBQKBNR w KQkq - 0 1";
        BitUtils.BitIndex startingPosition = B3_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(B4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetWhitePawnMovesAtBeginningPositionWithAbilityToAttack() {
        String fenNotation = "rnbqkbnr/ppp1pppp/1P6/8/8/3p4/P1PPPPPP/RNBQKBNR w KQkq - 0 1";
        BitUtils.BitIndex startingPosition = E2_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E3_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E4_IDX.ordinal()), MoveType.PAWN_LONG_MOVE, Figure.W_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D3_IDX.ordinal()), MoveType.PAWN_ATTACK, Figure.W_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetWhitePawnMovesEnPassantAttackRight() {
        String fenNotation = "rnbqkbnr/ppp1pppp/8/2PpP3/8/8/PP1P1PPP/RNBQKBNR w KQkq d6 0 1";
        BitUtils.BitIndex startingPosition = C5_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D6_IDX.ordinal()), MoveType.PAWN_ON_GO_ATTACK, Figure.W_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(C6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetWhitePawnMovesEnPassantAttackLeft() {
        String fenNotation = "rnbqkbnr/ppp1pppp/8/2PpP3/8/8/PP1P1PPP/RNBQKBNR w KQkq d6 0 1";
        BitUtils.BitIndex startingPosition = E5_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(D6_IDX.ordinal()), MoveType.PAWN_ON_GO_ATTACK, Figure.W_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(E6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetBlackPawnMovesPromotion() {
        String fenNotation = "1nbqkbn1/Pp2p1p1/7p/p5r1/7P/2p5/1PPPPPPp/RNBQKBN1 b - - 0 1";
        BitUtils.BitIndex startingPosition = H2_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(H1_IDX.ordinal()), MoveType.PAWN_TO_FIGURE, Figure.B_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(G1_IDX.ordinal()), MoveType.PAWN_TO_FIGURE_ATTACK, Figure.B_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

    @Test
    public void testGetWhitePawnMovesPromotion() {
        String fenNotation = "1nbqkbn1/Pp2p1p1/7p/p5r1/7P/2p5/1PPPPPPp/RNBQKBN1 w - - 0 1";
        BitUtils.BitIndex startingPosition = A7_IDX;

        Set<MoveInfo> pawnMoves = Stream.of(
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(A8_IDX.ordinal()), MoveType.PAWN_TO_FIGURE, Figure.W_PAWN),
                new MoveInfo(new Coord(startingPosition.ordinal()), new Coord(B8_IDX.ordinal()), MoveType.PAWN_TO_FIGURE_ATTACK, Figure.W_PAWN)
        ).collect(Collectors.toSet());

        assertEquals(pawnMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_PAWN
                        && pieceMoves.getCellFrom().getIndexAsOneDimension() == startingPosition.ordinal()
                ).collect(Collectors.toSet()));
    }

}
