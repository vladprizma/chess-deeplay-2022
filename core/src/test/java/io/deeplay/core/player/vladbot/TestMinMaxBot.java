package io.deeplay.core.player.vladbot;

import io.deeplay.core.evaluation.vladevaluations.PeSTO;
import io.deeplay.core.model.*;
import org.junit.Test;

import static io.deeplay.core.logic.BitUtils.BitIndex.*;
import static org.junit.Assert.assertEquals;

public class TestMinMaxBot {

    private final static boolean CACHE_LOGIC_CALCULATIONS = true;
    private final static boolean CLEAR_LOGIC_CACHE = false;

    @Test
    public void testMateInThreeDepthFive1() {
        GameInfo gameInfo = new GameInfo("3B1nr1/5k2/6N1/3KP1PB/4p3/8/6b1/7R w - - 0 1",
                CACHE_LOGIC_CALCULATIONS, CLEAR_LOGIC_CACHE);

        MinMaxBot minMaxBot = new MinMaxBot(Side.WHITE, new PeSTO(), 5);
        MoveInfo expectedMove = new MoveInfo(
                new Coord(G6_IDX.ordinal()), new Coord(H8_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_KNIGHT);
        assertEquals(expectedMove, minMaxBot.getAnswer(gameInfo));

        gameInfo = new GameInfo("3B1nrN/6k1/8/3KP1PB/4p3/8/6b1/7R w - - 2 2",
                CACHE_LOGIC_CALCULATIONS, CLEAR_LOGIC_CACHE);
        expectedMove = new MoveInfo(
                new Coord(D8_IDX.ordinal()), new Coord(F6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_BISHOP);
        assertEquals(expectedMove, minMaxBot.getAnswer(gameInfo));

        gameInfo = new GameInfo("5nrN/7k/5B2/3KP1PB/4p3/8/6b1/7R w - - 4 3",
                CACHE_LOGIC_CALCULATIONS, CLEAR_LOGIC_CACHE);
        expectedMove = new MoveInfo(
                new Coord(H5_IDX.ordinal()), new Coord(G6_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.W_BISHOP);
        assertEquals(expectedMove, minMaxBot.getAnswer(gameInfo));
    }

    @Test
    public void testMateInThreeDepthFive2() {
        GameInfo gameInfo = new GameInfo("8/2p1k1r1/2Q1p3/3pPp1p/Bp1P1B2/6PK/1PP1q2P/8 b - - 6 37",
                CACHE_LOGIC_CALCULATIONS, CLEAR_LOGIC_CACHE);
        MinMaxBot minMaxBot = new MinMaxBot(Side.BLACK, new PeSTO(), 5);
        MoveInfo expectedMove = new MoveInfo(
                new Coord(E2_IDX.ordinal()), new Coord(F1_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_QUEEN);
        assertEquals(expectedMove, minMaxBot.getAnswer(gameInfo));

        gameInfo = new GameInfo("8/2p1k1r1/2Q1p3/3pPp1p/Bp1P1B1K/6P1/1PP4P/5q2 b - - 8 38",
                CACHE_LOGIC_CALCULATIONS, CLEAR_LOGIC_CACHE);
        expectedMove = new MoveInfo(
                new Coord(G7_IDX.ordinal()), new Coord(G4_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_ROOK);
        assertEquals(expectedMove, minMaxBot.getAnswer(gameInfo));

        gameInfo = new GameInfo("8/2p1k3/2Q1p3/3pPp1K/Bp1P1Br1/6P1/1PP4P/5q2 b - - 0 39",
                CACHE_LOGIC_CALCULATIONS, CLEAR_LOGIC_CACHE);
        expectedMove = new MoveInfo(
                new Coord(F1_IDX.ordinal()), new Coord(H3_IDX.ordinal()), MoveType.USUAL_MOVE, Figure.B_QUEEN);
        assertEquals(expectedMove, minMaxBot.getAnswer(gameInfo));
    }
/*
// Эту штуку лучше не выпускать с минимаксом...
    @Test
    public void testMateInFiveDepthSevenVariation() {
        GameInfo gameInfo = new GameInfo("3rr1k1/pp3ppp/3b4/2p5/2Q5/6qP/PPP1B1P1/R1B2K1R b - - 0 1",CACHE_LOGIC_CALCULATIONS, CLEAR_LOGIC_CACHE);
        SelfPlay selfPlay = new SelfPlay(new MinMaxBot(Side.WHITE, new PeSTO(), 7),
                new MinMaxBot(Side.BLACK, new PeSTO(), 1), gameInfo);
        selfPlay.play();
    }
*/
}
