package io.deeplay.core.player.vladbot.namedbots;

import io.deeplay.core.evaluation.vladevaluations.PeSTO;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.vladbot.multithread.BreadthAlphaBetaPruningBot;

/**
 * Многопоточный бот обхода в ширину с ограничением по времени. Функция оценки - PeSTO.
 * Бот назван в честь Алисы, Team Assistant всех трёх команд java.
 */
public class AlisaBot extends BreadthAlphaBetaPruningBot {
    private final String PLAYER_NAME = this.getClass().getSimpleName();

    public AlisaBot(final Side side, final int maxDepth, final long worktimeInMilliseconds) {
        super(side, new PeSTO(), maxDepth, worktimeInMilliseconds);
    }

    public AlisaBot(final Side side, final int maxDepth) {
        super(side, maxDepth);
    }

    public AlisaBot(final Side side, final long worktimeInMilliseconds) {
        super(side, worktimeInMilliseconds);
    }

    public AlisaBot(final Side side) {
        super(side);
    }

    @Override
    public String getName() {
        return PLAYER_NAME + "Depth=" + getMaxDepth() +
                "Evaluation=" + getEvaluation().getName() +
                "TimeLimit=" + getWorktimeInMilliseconds();
    }
}
