package io.deeplay.core.listener;

import io.deeplay.core.model.GameInfo;

/**
 * Расширяет класс ChessEventSource, GameInfo первым уведомляется о событиях в игре.
 */
public class GameInfoGroup extends ChessEventSource {
    public GameInfoGroup(GameInfo gameInfo) {
        super();
        listeners.add(gameInfo);
    }
}
