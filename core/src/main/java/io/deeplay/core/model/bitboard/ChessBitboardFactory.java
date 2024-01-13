package io.deeplay.core.model.bitboard;

import io.deeplay.core.logic.newlogic.SimpleBitboardHandler;
import io.deeplay.core.model.Side;

import java.util.Map;

public class ChessBitboardFactory {
    // ChessBitboard изменяет состояние внутри(внутри SideBitboards),
    // это можно исправить если все методы с ChessBitboard
    // перекрутить на SideBitboards, но это займет много времени
    public static ChessBitboard createChessBitboard(final Map<Side, SideBitboards> sideBitboards,
                                                    final Side currentTurnSide,
                                                    final long enPassant,
                                                    final String castlingRights) {
        SideBitboards currentTurnSideBitboards = sideBitboards.get(currentTurnSide);
        SideBitboards opponentSideBitboards = sideBitboards.get(Side.otherSide(currentTurnSide));

        ChessBitboard currentTurnChessBitboard = new ChessBitboard(currentTurnSideBitboards, opponentSideBitboards);
        currentTurnChessBitboard.setEnPassantFile(enPassant);
        currentTurnChessBitboard
                .setWhiteKingSideCastlingRight(castlingRights.contains("K"));
        currentTurnChessBitboard
                .setWhiteQueenSideCastlingRight(castlingRights.contains("Q"));
        currentTurnChessBitboard
                .setBlackKingSideCastlingRight(castlingRights.contains("k"));
        currentTurnChessBitboard
                .setBlackQueenSideCastlingRight(castlingRights.contains("q"));
        // Создаем фигуры и считаем возможные ходы
        currentTurnSideBitboards.initializePieceBitboards(currentTurnChessBitboard);
        opponentSideBitboards.initializePieceBitboards(currentTurnChessBitboard);
        // Собираем информацию о шахе, изменяет ChessBitboard
        currentTurnChessBitboard
                .setProcessingSideCheckData(SimpleBitboardHandler.getCheckData(currentTurnChessBitboard));

        // Добавляем информацию о возможных ходах внутри ChessBitboard, изменяет ChessBitboard
        SimpleBitboardHandler.countAllPossibleMoves(currentTurnChessBitboard);
        return currentTurnChessBitboard;
    }

}
