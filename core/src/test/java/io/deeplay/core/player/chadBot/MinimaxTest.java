package io.deeplay.core.player.chadBot;

import io.deeplay.core.model.*;
import io.deeplay.core.player.Player;
import org.junit.Test;

import static io.deeplay.core.model.MoveType.USUAL_ATTACK;
import static io.deeplay.core.model.MoveType.USUAL_MOVE;
import static org.junit.Assert.*;

public class MinimaxTest {

    @Test
    public void getAnswer() {
        GameInfo gameInfo = new GameInfo("rnbqkbnr/pppp1p1p/6p1/4p2Q/4P3/8/PPPP1PPP/RNB1KBNR w KQkq - 0 1");
        Player player = new Minimax(Side.WHITE, 3);
        MoveInfo moveInfo = new MoveInfo(new Coord(7, 4), new Coord(4, 4), USUAL_ATTACK, Figure.W_QUEEN);
        assertEquals(moveInfo, player.getAnswer(gameInfo));

//      Мат в 2 хода
        gameInfo = new GameInfo("1nbr3k/ppppp1pp/5p2/6N1/2Q5/1B6/PPPPPPPP/RNB1K2R w - - 0 1");
        moveInfo = new MoveInfo(new Coord(2, 3), new Coord(6, 7), USUAL_MOVE, Figure.W_QUEEN);
        assertEquals(moveInfo, player.getAnswer(gameInfo));

//        Мат в 3 хода.
        player = new Minimax(Side.BLACK, 5);
        gameInfo = new GameInfo("r2q1b1r/ppp3k1/8/3Bp3/6P1/5Qp1/PPPP1PP1/R4RK1 b - - 0 1");
        moveInfo = new MoveInfo(new Coord(7, 7), new Coord(7, 0), USUAL_MOVE, Figure.B_ROOK);
        assertEquals(moveInfo, player.getAnswer(gameInfo));
    }
}