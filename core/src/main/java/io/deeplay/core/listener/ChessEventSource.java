package io.deeplay.core.listener;

import io.deeplay.core.model.GameStatus;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс служит для хранения слушателей и уведомления каждого из них.
 * Следует использовать наследника GameInfoGroup
 */
public abstract class ChessEventSource {
    protected final List<ChessListener> listeners;

    public ChessEventSource() {
        listeners = new ArrayList<>();
    }

    public void addListener(ChessListener listener) {
        this.listeners.add(listener);
    }

    public void gameStarted() {
        listeners.forEach(ChessListener::gameStarted);
    }

    public void playerSeated(final Side side) {
        listeners.forEach(listener -> listener.playerSeated(side));
    }

    /**
     * Игрок совершил ход
     * @param side ходившая сторона
     * @param moveInfo информация о совершенном ходе
     */
    public void playerActed(final Side side, final MoveInfo moveInfo) {
        listeners.forEach(listener -> listener.playerActed(side, moveInfo));
    }

    public void offerDraw(final Side side) {
        listeners.forEach(listener -> listener.offerDraw(side));
    }

    public void acceptDraw(final Side side) {
        listeners.forEach(listener -> listener.acceptDraw(side));
    }

    public void playerRequestsTakeBack(final Side side) {
        listeners.forEach(listener -> listener.playerRequestsTakeBack(side));
    }

    public void playerAgreesTakeBack(final Side side) {
        listeners.forEach(listener -> listener.playerAgreesTakeBack(side));
    }

    public void playerResigned(final Side side) {
        listeners.forEach(listener -> listener.playerResigned(side));
    }

    public void draw() {
        listeners.forEach(ChessListener::draw);
    }

    public void playerWon(final Side side) {
        listeners.forEach(listener -> listener.playerWon(side));
    }

    /**
     * Событие конца игры.
     */
    public void gameOver(final GameStatus gameStatus) {
        listeners.forEach(listener -> listener.gameOver(gameStatus));
    }
}
