package io.deeplay.core.evaluation.vladevaluations;

import io.deeplay.core.model.*;

import java.util.Set;

/**
 * Функция оценки основанная только на ценности фигур.
 * Данная оценка плоха тем, что не развивает фигуры, поэтому теряет большинство потенциала.
 */
public class PieceValue implements Evaluation {
    private final static String EVALUATION_NAME = "PieceValue";

    @Override
    public int evaluateBoard(final ChessBoard chessBoard) {
        return countPiecesValuesForSide(chessBoard, Side.WHITE) - countPiecesValuesForSide(chessBoard, Side.BLACK);
    }

    @Override
    public String getName() {
        return EVALUATION_NAME;
    }

    /**
     * Считает стоимости фигур стороны.
     * Можно использовать в качестве оценки основанной на стоимости фигур.
     *
     * @param side сторона стоимость фигур которой будут считаться.
     * @return стоимость фигур стороны side.
     */
    int countPiecesValuesForSide(final ChessBoard chessBoard, final Side side) {
        final Set<Figure> sideFigures = side == Side.WHITE ? MapsStorage.WHITE_FIGURES : MapsStorage.BLACK_FIGURES;
        int score = 0;
        for (BoardCell[] boardCellRow : chessBoard.getBoard())
            for (BoardCell boardCellColumn : boardCellRow)
                if (sideFigures.contains(boardCellColumn.getFigure()))
                    score += Math.abs(boardCellColumn.getFigure().getWeight());
        return score;

    }

}