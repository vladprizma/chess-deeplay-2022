package io.deeplay.core.player.chadBot;

import io.deeplay.core.model.GameInfo;

public abstract class EvalFunction {
    abstract public Double eval (final GameInfo gameInfo);
}
