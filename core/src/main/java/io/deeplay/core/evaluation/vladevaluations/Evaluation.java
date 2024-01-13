package io.deeplay.core.evaluation.vladevaluations;

import io.deeplay.core.model.ChessBoard;

/**
 * Интерфейс оценочной функции.
 */
public interface Evaluation {

    /**
     * Оценивает ситуацию в процессе игры. Наилучшая оценка учитывает наибольшее количество факторов,
     * от количества и ценности фигур до изолированных пешек, связанных пешек, сдвоенных пешек, безопасности короля
     * и т.д. Стратегии награждаются, а ошибки караются итоговой оценкой.
     * См. <a href="https://www.chessprogramming.org/Evaluation"> Evaluation </a>
     *
     * @param chessBoard расстановка фигур с новым ходом
     * @return если > 0, то положение выгодно для белых, если < 0, то для чёрных.
     */
    int evaluateBoard(final ChessBoard chessBoard);

    String getName();

}
