package io.deeplay.core.api;

import io.deeplay.core.logic.BitUtils;
import io.deeplay.core.model.Coord;
import io.deeplay.core.model.Figure;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.MoveType;
import io.deeplay.core.parser.FENParser;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestFen {

    @Test
    public void testPromotionWhitePawn() {
        String fenNotation = "4r3/3P4/8/8/7K/8/1k5p/6N1 w - - 0 1";
        MoveInfo moveInfo = new MoveInfo(
                new Coord(BitUtils.BitIndex.D7_IDX.ordinal()),
                new Coord(BitUtils.BitIndex.E8_IDX.ordinal()),
                MoveType.PAWN_TO_FIGURE_ATTACK, Figure.W_PAWN, Figure.W_ROOK);

        String expectedFenNotation = "4R3/8/8/8/7K/8/1k5p/6N1 w - - 0 1";
        assertEquals(expectedFenNotation, FENParser.promotePawnToFigureUpdateFen(fenNotation, moveInfo));

        moveInfo = new MoveInfo(
                new Coord(BitUtils.BitIndex.D7_IDX.ordinal()),
                new Coord(BitUtils.BitIndex.D8_IDX.ordinal()),
                MoveType.PAWN_TO_FIGURE, Figure.W_PAWN, Figure.W_KNIGHT);
        expectedFenNotation = "3Nr3/8/8/8/7K/8/1k5p/6N1 w - - 0 1";
        assertEquals(expectedFenNotation, FENParser.promotePawnToFigureUpdateFen(fenNotation, moveInfo));
    }

    @Test
    public void testPromotionBlackPawn() {
        String fenNotation = "3r1q2/4P3/8/8/1K4k1/8/p7/1R6 b - - 0 1";
        MoveInfo moveInfo = new MoveInfo(
                new Coord(BitUtils.BitIndex.A2_IDX.ordinal()),
                new Coord(BitUtils.BitIndex.A1_IDX.ordinal()),
                MoveType.PAWN_TO_FIGURE, Figure.B_PAWN, Figure.B_QUEEN);

        String expectedFenNotation = "3r1q2/4P3/8/8/1K4k1/8/8/qR6 b - - 0 1";
        assertEquals(expectedFenNotation, FENParser.promotePawnToFigureUpdateFen(fenNotation, moveInfo));
    }

    @Test
    public void testRegexLongCastling() {
        String fenNotation = "4k2r/8/8/8/8/8/8/R3K3 b - - 0 1";
        String unzipped = FENParser.unzipFen(fenNotation);

        assertTrue(unzipped.split("/")[7].split(" ")[0].matches("^[rR]1{3}[kK]\\S+"));
        assertFalse(unzipped.split("/")[7].matches("^[rR]1{3}[kK]\\S+"));
    }

    @Test
    public void testRegexShortCastling() {
        String fenNotation = "4k2r/8/8/8/8/8/8/R3K3 b - - 0 1";
        String unzipped = FENParser.unzipFen(fenNotation);

        assertTrue(unzipped.split("/")[0].matches("\\S+[kK]1{2}[rR]$"));
    }

    @Test
    public void testRegexBlackCastling() {
        String fenNotation = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
        String unzipped = FENParser.unzipFen(fenNotation);

        assertTrue(unzipped.split("/")[0].matches("^[r]1{3}[k]\\S+")); // длинная рокировка
        assertTrue(unzipped.split("/")[0].matches("\\S+[k]1{2}[r]$")); // короткая рокировка
    }

    @Test
    public void testRegexWhiteCastling() {
        String fenNotation = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
        String unzipped = FENParser.unzipFen(fenNotation);

        assertTrue(unzipped.split("/")[7].split(" ")[0].matches("^[R]1{3}[K]\\S+")); // длинная рокировка
        assertTrue(unzipped.split("/")[7].split(" ")[0].matches("\\S+[K]1{2}[R]$")); // короткая рокировка
    }

    @Test
    public void testBlackKingSideCastling() {
        String fenNotation = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        MoveInfo moveInfo = new MoveInfo(
                new Coord(BitUtils.BitIndex.E8_IDX.ordinal()),
                new Coord(BitUtils.BitIndex.G8_IDX.ordinal()),
                MoveType.CASTLE_SHORT, Figure.B_KING);

        String expectedFenNotation = "r4rk1/8/8/8/8/8/8/R3K2R w KQ - 0 1";
        assertEquals(expectedFenNotation, FENParser.castlingUpdateFen(fenNotation, moveInfo));
    }

    @Test
    public void testBlackQueenSideCastling() {
        String fenNotation = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        MoveInfo moveInfo = new MoveInfo(
                new Coord(BitUtils.BitIndex.E8_IDX.ordinal()),
                new Coord(BitUtils.BitIndex.C8_IDX.ordinal()),
                MoveType.CASTLE_SHORT, Figure.B_KING);

        String expectedFenNotation = "2kr3r/8/8/8/8/8/8/R3K2R w KQ - 0 1";
        assertEquals(expectedFenNotation, FENParser.castlingUpdateFen(fenNotation, moveInfo));
    }

    @Test
    public void testWhiteKingSideCastling() {
        String fenNotation = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        MoveInfo moveInfo = new MoveInfo(
                new Coord(BitUtils.BitIndex.E1_IDX.ordinal()),
                new Coord(BitUtils.BitIndex.G1_IDX.ordinal()),
                MoveType.CASTLE_SHORT, Figure.W_KING);
        /*
        MoveInfo moveInfo = new MoveInfo(
                new Coord(BitUtils.BitIndex.E1_IDX.ordinal()),
                new Coord(BitUtils.BitIndex.G1_IDX.ordinal()),
                MoveType.CASTLE_SHORT, Figure.B_KING); <- не ругается на то что король чужой стороны, почему-то String.matches не учитывает регистр...
         */

        String expectedFenNotation = "r3k2r/8/8/8/8/8/8/R4RK1 w kq - 0 1";
        assertEquals(expectedFenNotation, FENParser.castlingUpdateFen(fenNotation, moveInfo));
    }

    @Test
    public void testWhiteQueenSideCastling() {
        String fenNotation = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        MoveInfo moveInfo = new MoveInfo(
                new Coord(BitUtils.BitIndex.E1_IDX.ordinal()),
                new Coord(BitUtils.BitIndex.C1_IDX.ordinal()),
                MoveType.CASTLE_SHORT, Figure.W_KING);

        String expectedFenNotation = "r3k2r/8/8/8/8/8/8/2KR3R w kq - 0 1";
        assertEquals(expectedFenNotation, FENParser.castlingUpdateFen(fenNotation, moveInfo));
    }

    @Test
    public void testTest() {
        String fenNotation = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        assertFalse(FENParser.getCastlingRights(fenNotation).contains("Kq")); // классический contains от String
        assertTrue(FENParser.containsUnordered(FENParser.getCastlingRights(fenNotation), "Kq"));
    }

}
