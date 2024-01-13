package io.deeplay.core.player.evgenbot;

import io.deeplay.core.model.GameInfo;

/**
 * Интерфейс функции оценки текущего состояния игры.
 */
public interface EvaluationFunction {
    int evaluate(GameInfo gameInfo);
}