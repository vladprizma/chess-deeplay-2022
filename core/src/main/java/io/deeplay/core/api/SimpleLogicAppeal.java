package io.deeplay.core.api;

import io.deeplay.core.model.BoardSituationInfo;
import io.deeplay.core.model.MoveInfo;

import java.util.Set;

/**
 * Упрощенный интерфейс обращения к логике
 */
public interface SimpleLogicAppeal {
    /**
     * @param fenNotation нотация Форсайта — Эдвардса
     * @return возвращает true если стороне с текущим ходом - мат
     */
    boolean isMate(final String fenNotation);

    /**
     * @param fenNotation нотация Форсайта — Эдвардса
     * @return возвращает true если одна из сторон не имеет возможности совершить ход
     */
    boolean isStalemate(final String fenNotation);

    /**
     * Ничья из-за нехватки фигур.
     *
     * @param fenNotation нотация Форсайта — Эдвардса
     * @return возвращает true в 4-x случаях: остались только фигуры короля против короля, короля+слона и короля,
     * короля+коня и короля, короля+слона* и короля+слона* (* - ВСЕ слоны на клетках одного цвета)
     */
    boolean isDrawByPieceShortage(final String fenNotation);

    /**
     * @param fenNotation нотация Форсайта — Эдвардса
     * @return возвращает все возможные ходы для стороны с предстоящим ходом, если игра закончилась set = 0
     */
    Set<MoveInfo> getMoves(final String fenNotation);

    /**
     * @param fenNotation нотация Форсайта — Эдвардса
     * @return возвращает информацию о ситуации на доске, о том мат ли противнику, пат или ничья,
     * относительно ходящей стороны
     */
    BoardSituationInfo getBoardSituationInfo(final String fenNotation);

}
