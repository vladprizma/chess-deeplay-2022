package io.deeplay.core.player.vladbot.namedbots;

import io.deeplay.core.evaluation.vladevaluations.PieceValue;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.vladbot.multithread.BreadthAlphaBetaPruningBot;

/**
 * Многопоточный бот обхода в ширину с ограничением по времени. Функция оценки - вес фигур.
 */
public class BreadthAlphaBetaPruningBotWithPieceValues extends BreadthAlphaBetaPruningBot {
    private final String PLAYER_NAME = this.getClass().getSimpleName();

    public BreadthAlphaBetaPruningBotWithPieceValues(final Side side,
                                                     final int maxDepth,
                                                     final long worktimeInMilliseconds) {
        super(side, new PieceValue(), maxDepth, worktimeInMilliseconds);
    }

    public BreadthAlphaBetaPruningBotWithPieceValues(final Side side, final int maxDepth) {
        super(side, maxDepth);
    }

    public BreadthAlphaBetaPruningBotWithPieceValues(final Side side, final long worktimeInMilliseconds) {
        super(side, worktimeInMilliseconds);
    }

    public BreadthAlphaBetaPruningBotWithPieceValues(final Side side) {
        super(side);
    }

    @Override
    public String getName() {
        return PLAYER_NAME + "Depth=" + getMaxDepth() +
                "Evaluation=" + getEvaluation().getName() +
                "TimeLimit=" + getWorktimeInMilliseconds();
    }

}
