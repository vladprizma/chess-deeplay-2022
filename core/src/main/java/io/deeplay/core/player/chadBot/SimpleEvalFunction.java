package io.deeplay.core.player.chadBot;

import io.deeplay.core.model.Figure;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.GameStatus;

public class SimpleEvalFunction extends EvalFunction{
    @Override
    public Double eval(GameInfo gameInfo) {
        GameStatus gameStatus = gameInfo.getGameStatus();
        double evaluationValue = 0;
        if (gameStatus == GameStatus.BLACK_WON) {
            return -100000.0;
        } else if (gameStatus == GameStatus.WHITE_WON) {
            return 100000.0;
        } else if (gameStatus == GameStatus.DRAW ||
        gameStatus == GameStatus.FIFTY_MOVES_RULE ||
        gameStatus == GameStatus.STALEMATE ||
        gameStatus == GameStatus.THREEFOLD_REPETITION) {
            return 0.0;
        }
        for (Figure figure : gameInfo.getAllFigures()) {
            evaluationValue += figure.getWeight();
        }
        return evaluationValue;
    }
}
