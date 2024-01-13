package io.deeplay.core.player.vladbot.namedbots;

import io.deeplay.core.evaluation.vladevaluations.PeSTO;
import io.deeplay.core.model.Side;

/**
 * Оценочный бот, ходы производятся на основе оценки доски. Функция оценки - PeSTO.
 */
public class EvaluationBot extends io.deeplay.core.player.vladbot.EvaluationBot {

    private final String PLAYER_NAME = this.getClass().getSimpleName();

    public EvaluationBot(final Side side) {
        super(side, new PeSTO());
    }

    @Override
    public String getName() {
        return PLAYER_NAME + "Evaluation=" + getEvaluation().getName();
    }

}
