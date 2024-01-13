package io.deeplay.core.player.chadBot;

import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.Player;

public class Minimax extends Player {
    final EvalFunction evalFunction = new SimpleEvalFunction();
    final int depth;

    public Minimax(final Side side, final int depth) {
        super(side);
        this.depth = depth;
    }

    public MoveInfo minimaxRoot(final GameInfo gameInfo, final int depth, final boolean isMaximising) {
        double bestMove = Integer.MIN_VALUE;
        MoveInfo bestMoveInfo = null;
        for (MoveInfo move : gameInfo.getAvailableMoves()) {
            double value = minimax(gameInfo.copy(move), depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !isMaximising);
            if (value >= bestMove) {
                bestMove = value;
                bestMoveInfo = move;
            }
        }
        return bestMoveInfo;
    }
    private Double minimax(final GameInfo gameInfo, final int depth, double alpha, double beta, final boolean isMaximising) {
        if (depth == 0) {
            return evalFunction.eval(gameInfo);
        }

        double bestMoveValue;
        if (isMaximising) {
            bestMoveValue = Integer.MIN_VALUE;
            for (MoveInfo move : gameInfo.getAvailableMoves()) {
                bestMoveValue = Math.max(bestMoveValue, minimax(gameInfo.copy(move), depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, bestMoveValue);
                if (beta <= alpha) {
                    return bestMoveValue;
                }
            }
        } else {
            bestMoveValue = Integer.MAX_VALUE;
            for (MoveInfo move : gameInfo.getAvailableMoves()) {
                bestMoveValue = Math.min(bestMoveValue, minimax(gameInfo.copy(move), depth - 1, alpha, beta, true));
                beta = Math.min(beta, bestMoveValue);
                if (beta <= alpha) {
                    return bestMoveValue;
                }
            }
        }
        return bestMoveValue;
    }

    @Override
    public MoveInfo getAnswer(GameInfo gameInfo) {
        return minimaxRoot(gameInfo, depth, true);
    }

    @Override
    public String getName() {
        return "ChadMinimaxBot";
    }
}
