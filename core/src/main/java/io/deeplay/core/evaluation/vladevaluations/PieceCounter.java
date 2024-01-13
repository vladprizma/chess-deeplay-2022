package io.deeplay.core.evaluation.vladevaluations;

import io.deeplay.core.model.*;

import java.util.Set;

/**
 * Функция оценки основанная только на количестве фигур.
 * Данная оценка плоха тем, что не развивает фигуры, поэтому теряет большинство потенциала.
 */
public class PieceCounter implements Evaluation {
    private final static String EVALUATION_NAME = "PieceCounter";

    @Override
    public int evaluateBoard(final ChessBoard chessBoard) {
        return countPiecesForSide(chessBoard, Side.WHITE) - countPiecesForSide(chessBoard, Side.BLACK);
    }

    @Override
    public String getName() {
        return EVALUATION_NAME;
    }

    /**
     * Считает количество фигур стороны.
     * Можно использовать в качестве оценки основанной на количестве фигур.
     *
     * @param side сторона фигуры которой будут считаться.
     * @return количество фигур стороны side.
     */
    int countPiecesForSide(final ChessBoard chessBoard, final Side side) {
        final Set<Figure> sideFigures = side == Side.WHITE ? MapsStorage.WHITE_FIGURES : MapsStorage.BLACK_FIGURES;
        int countPieces = 0;
        for (BoardCell[] boardCellRow : chessBoard.getBoard())
            for (BoardCell boardCellColumn : boardCellRow)
                if (sideFigures.contains(boardCellColumn.getFigure()))
                    countPieces++;
        return countPieces;

    }

}