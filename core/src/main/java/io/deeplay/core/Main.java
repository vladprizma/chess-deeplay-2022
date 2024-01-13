package io.deeplay.core;

import io.deeplay.core.model.Side;
import io.deeplay.core.player.vladbot.EvaluationBot;

//   mvn clean compile exec:java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        SelfPlay selfPlay = new SelfPlay(
                new EvaluationBot(Side.WHITE), new EvaluationBot(Side.BLACK));
        selfPlay.play();

        // Человек против бота

//        GameInfo gameInfo = new GameInfo("r7/3RK1k1/4P3/8/8/8/8/8 w - - 0 1");
//        SelfPlay selfPlay = new SelfPlay(new HumanPlayer(Side.WHITE), new RandomBot(Side.BLACK, 0L), gameInfo);
//        selfPlay.play();

        // Невозможность рокировки
//        SelfPlay selfPlay = new SelfPlay(new HumanPlayer(Side.WHITE), new HumanPlayer(Side.BLACK), gameInfo);
//        selfPlay.play();

        // превращение пешки пат
//        GameInfo gameInfo = new GameInfo("8/1P6/8/2K5/8/8/k7/8 w - - 0 1");
//        SelfPlay selfPlay = new SelfPlay(new HumanPlayer(Side.WHITE), new HumanPlayer(Side.BLACK), gameInfo);
//        selfPlay.play();

        // ход связанной фигуры
//        GameInfo gameInfo = new GameInfo("r1bqkbnr/ppp2ppp/2np4/1B2p3/4P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 0 1");
//        SelfPlay selfPlay = new SelfPlay(new HumanPlayer(Side.WHITE), new HumanPlayer(Side.BLACK), gameInfo);
//        selfPlay.play();

        // двойной шах
//        GameInfo gameInfo = new GameInfo("r1bqkbnr/ppp2ppp/3p4/1N2p3/Q3P3/8/PPPP1PPP/RNB2RK1 w kq - 0 1");
//        SelfPlay selfPlay = new SelfPlay(new HumanPlayer(Side.WHITE), new HumanPlayer(Side.BLACK), gameInfo);
//        selfPlay.play();
    }
}