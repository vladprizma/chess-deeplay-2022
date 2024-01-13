package io.deeplay.core.player;

import io.deeplay.core.listener.ChessAdapter;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;

public abstract class Player extends ChessAdapter {
    private Side side;

    public Player(final Side side) {
        this.side = side;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(final Side side) {
        this.side = side;
    }

    public abstract MoveInfo getAnswer(GameInfo gameInfo);

    /**
     * Имя содержит все свойства игрока, если это бот обхода дерева игры, то
     * его максимальную глубину, если это RandomBot, то его seed.
     * Обязательно нужно чтобы в имени не было пробелов и символов: {\, /, *, ?, ", <, >, |}
     * (иначе это создаст проблемы в путях к файлам).
     *
     * @return имя игрока, со всеми свойствами полностью определяющими игрока
     */
    public abstract String getName();

    @Override
    public String toString() {
        return side.toString();
    }
}
