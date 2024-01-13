package io.deeplay.core.listener;

import io.deeplay.core.model.GameStatus;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;

/**
 * Интерфейс должен быть имплементирован каждым слушателем.
 * Функции представляют собой внутриигровые события.
 */
public interface ChessListener {
    /**
     * Игра началась
     */
    void gameStarted();

    /**
     * Игрок занял сторону
     *
     * @param side Сторона за которую сел игрок
     */
    void playerSeated(final Side side);

    /**
     * Игрок сделал ход
     *
     * @param side     Ходившая сторона
     * @param moveInfo Информация о сделанном ходе
     */
    void playerActed(final Side side, final MoveInfo moveInfo);

    /**
     * Игрок предложил ничью
     *
     * @param side сторона, которая предложила ничью
     */
    void offerDraw(final Side side);

    /**
     * Игрок согласился на ничью
     *
     * @param side сторона игрока, согласившегося на ничью
     */
    void acceptDraw(final Side side);

    /**
     * Запрос отмены хода
     *
     * @param side сторона, запросившая отмену хода
     */
    void playerRequestsTakeBack(final Side side);

    /**
     * Отмена хода принята
     *
     * @param side сторона подтвердившая отмену хода
     */
    void playerAgreesTakeBack(final Side side);

    /**
     * Игрок сдался
     *
     * @param side сторона сдавшегося игрока
     */
    void playerResigned(final Side side);

    /**
     * Произошла ничья
     */
    void draw();

    /**
     * Игрок победил
     *
     * @param side сторона победившего игрока
     */
    void playerWon(final Side side);

    /**
     * Событие окончания игры
     */
    void gameOver(final GameStatus gameStatus);
}
