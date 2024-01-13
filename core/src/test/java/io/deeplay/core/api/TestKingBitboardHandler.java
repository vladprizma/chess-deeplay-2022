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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestKingBitboardHandler {

    private final static SimpleLogicAppeal simpleLogicAppeal = new SimpleLogic();

    @Test
    public void testGetMovesKingDuel() {
        String fenNotation = "8/8/8/8/2k5/8/3K4/8 b - - 0 1";

        assertEquals(6, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_KING).count());
    }

    @Test
    public void testGetWhiteKingMovesAtStartingPosition() {
        /*
         * Check possible E1 king's moves in default position
         */
        String fenNotation = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().anyMatch(pieceMoves -> pieceMoves.getFigure() != Figure.W_KING));
    }

    @Test
    public void testGetBlackKingMovesAtStartingPosition() {
        String fenNotation = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";

        assertTrue(simpleLogicAppeal.getMoves(fenNotation)
                .stream().anyMatch(pieceMoves -> pieceMoves.getFigure() != Figure.B_KING));
    }

    @Test
    public void testGetKingMovesAfterPawnInFrontOfKingMove() {
        /*
         * Check possible E1 king's moves after e4e5
         */
        String fenNotation = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 1";

        Set<MoveInfo> kingMoves = Stream.of(
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(E2_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING)
        ).collect(Collectors.toSet());

        assertEquals(1, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).count());
        assertEquals(kingMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).collect(Collectors.toSet()));
    }

    @Test
    public void testGetKingMovesSurroundedByOwnPieces() {
        /*
         * Check possible G8 king's moves after 1. e4 e5 2. Nf3 d5 3. exd5 e4 4. Qe2 Nf6 5. d3 Qxd5 6. Nbd2 Nc6
         * 7. dxe4 Qh5 8. Qb5 Bc5 9. e5 Nd7 10. e6 fxe6 11. Nb3 a6 12. Qc4 Be7 13. Be2 Nb6 14. Qe4 Qf5
         * 15. Qxf5 exf5 16. O-O O-O 17. Bf4 Nd5 18. Bc4 Be6 19. Bg5
         */
        String fenNotation = "r4rk1/1pp1b1pp/p1n1b3/3n1pB1/2B5/1N3N2/PPP2PPP/R4RK1 b - - 7 19";

        Set<MoveInfo> kingMoves = Stream.of(
                new MoveInfo(new Coord(G8_IDX.ordinal()), new Coord(H8_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_KING),
                new MoveInfo(new Coord(G8_IDX.ordinal()), new Coord(F7_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_KING)
        ).collect(Collectors.toSet());

        assertEquals(2, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_KING).count());
        assertEquals(kingMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.B_KING).collect(Collectors.toSet()));
    }

    @Test
    public void testGetKingMovesSurroundedByAttackedSquares() {
        String fenNotation = "8/8/8/3K4/8/8/8/k1r1r3 w - - 0 1";

        Set<MoveInfo> kingMoves = Stream.of(
                new MoveInfo(new Coord(D5_IDX.ordinal()), new Coord(D4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(D5_IDX.ordinal()), new Coord(D6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING)
        ).collect(Collectors.toSet());

        assertEquals(2, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).count());
        assertEquals(kingMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).collect(Collectors.toSet()));
    }

    @Test
    public void testGetKingMovesCantAttackOpponentPieceUnderCoveredWithAttack() {
        String fenNotation = "8/8/8/8/5n2/k7/6p1/6K1 w - - 0 1";

        Set<MoveInfo> kingMoves = Stream.of(
                new MoveInfo(new Coord(G1_IDX.ordinal()), new Coord(F2_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(G1_IDX.ordinal()), new Coord(H2_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING)
        ).collect(Collectors.toSet());

        assertEquals(2, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).count());
        assertEquals(kingMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).collect(Collectors.toSet()));

        fenNotation = "8/8/8/8/8/k7/6p1/6K1 w - - 0 1"; // Теперь без коня прикрывающего пешку
        kingMoves.add(
                new MoveInfo(new Coord(G1_IDX.ordinal()), new Coord(G2_IDX.ordinal()), MoveType.USUAL_ATTACK, Figure.W_KING));

        assertEquals(3, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).count());
        assertEquals(kingMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).collect(Collectors.toSet()));
    }

    @Test
    public void testGetKingMovesCastlingPossibleOnQueenSide() {
        String fenNotation = "4k3/8/8/8/5r2/8/8/R3K2R w KQ - 0 1";

        Set<MoveInfo> kingMoves = Stream.of(
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(D1_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(E2_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(D2_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(C1_IDX.ordinal()), MoveType.CASTLE_LONG, Figure.W_KING)
        ).collect(Collectors.toSet());

        assertEquals(4, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).count());
        assertEquals(kingMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).collect(Collectors.toSet()));
    }

    @Test
    public void testGetKingMovesCastlingPossibleOnKingSide() {
        String fenNotation = "4k3/8/8/8/2r5/8/8/R3K2R w KQ - 0 1";

        Set<MoveInfo> kingMoves = Stream.of(
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(D1_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(D2_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(E2_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(F1_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(F2_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KING),
                new MoveInfo(new Coord(E1_IDX.ordinal()), new Coord(G1_IDX.ordinal()), MoveType.CASTLE_SHORT, Figure.W_KING)
        ).collect(Collectors.toSet());

        assertEquals(6, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).count());
        assertEquals(kingMoves, simpleLogicAppeal.getMoves(fenNotation)
                .stream().filter(pieceMoves -> pieceMoves.getFigure() == Figure.W_KING).collect(Collectors.toSet()));
    }


}
