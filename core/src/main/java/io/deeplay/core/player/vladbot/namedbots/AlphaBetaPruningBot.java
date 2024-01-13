package io.deeplay.core.player.vladbot.namedbots;

import io.deeplay.core.evaluation.vladevaluations.PeSTO;
import io.deeplay.core.model.Side;

/**
 * NOT USER FRIENDLY. Ходы могут считаться 5-40 секунд.
 * Бот с альфа-бета отсечением. Функция оценки - PeSTO.
 */
public class AlphaBetaPruningBot extends io.deeplay.core.player.vladbot.AlphaBetaPruningBot {

    private final String PLAYER_NAME = this.getClass().getSimpleName();

    public AlphaBetaPruningBot(final Side side, final int maxDepth) {
        super(side, new PeSTO(), maxDepth);
    }

    public AlphaBetaPruningBot(final Side side) {
        this(side, 5);
    }

    @Override
    public String getName() {
        return PLAYER_NAME + "Depth=" + getMaxDepth() +
                "Evaluation=" + getEvaluation().getName();
    }

}
