package io.deeplay.core.api;

import io.deeplay.core.logic.newlogic.SimpleBitboardHandler;
import io.deeplay.core.model.BoardSituationInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.bitboard.CheckType;
import io.deeplay.core.model.bitboard.ChessBitboard;
import io.deeplay.core.model.bitboard.SideBitboards;
import io.deeplay.core.parser.FENParser;

import java.util.Set;

import static io.deeplay.core.logic.newlogic.SimpleBitboardHandler.getCurrentProcessingSideAllMoves;


/**
 * Класс реализует интерфейс логики на основе обработчика битбордов - SimpleBitboardHandler.
 */
public class SimpleLogic implements SimpleLogicAppeal {

    @Override
    public boolean isMate(final String fenNotation) {
        ChessBitboard currentSideChessBitboard = FENParser.parseFENToBitboards(fenNotation);
        opponentShouldNotBeInCheck(currentSideChessBitboard);
        // мат проверяем только для нашей стороны (т.к. отсекается случай когда противник под шахом, а ход наш)
        return isMate(currentSideChessBitboard);

    }

    private boolean isMate(final ChessBitboard currentSideChessBitboard) {
        if (currentSideChessBitboard.getProcessingSideCheckData().getCheckType() == CheckType.TWO &&
                currentSideChessBitboard.getProcessingSideBitboards()
                        .getKingPieceBitboards().getMovesUnderRestrictions(currentSideChessBitboard) == 0) {
            return true;
        }

        return currentSideChessBitboard.getProcessingSideCheckData().getCheckType() == CheckType.ONE &&
                currentSideChessBitboard.getCountFiguresThatCanMove() == 0;
    }

    @Override
    public boolean isStalemate(final String fenNotation) {
        ChessBitboard currentSideChessBitboard = FENParser.parseFENToBitboards(fenNotation);
        opponentShouldNotBeInCheck(currentSideChessBitboard);

        // Для противника считать пат не надо, т.к. если мы ходим, то пат противнику может пропасть

        return isStalemate(currentSideChessBitboard);
    }

    private boolean isStalemate(final ChessBitboard currentSideChessBitboard) {
        return currentSideChessBitboard.getProcessingSideCheckData().getCheckType() == CheckType.NONE
                && currentSideChessBitboard.getCountFiguresThatCanMove() == 0;
    }

    @Override
    public boolean isDrawByPieceShortage(final String fenNotation) {
        ChessBitboard currentSideChessBitboard = FENParser.parseFENToBitboards(fenNotation);
        opponentShouldNotBeInCheck(currentSideChessBitboard);

        return isDrawByPieceShortage(currentSideChessBitboard);
    }

    private boolean isDrawByPieceShortage(final ChessBitboard currentSideChessBitboard) {
        final boolean isBishopsAndKingsLeft = (currentSideChessBitboard.getProcessingSideBitboards().getKing() |
                currentSideChessBitboard.getProcessingSideBitboards().getBishops() |
                currentSideChessBitboard.getOpponentSideBitboards().getKing() |
                currentSideChessBitboard.getOpponentSideBitboards().getBishops()) ==
                currentSideChessBitboard.getOccupied();

        final boolean isKnightsAndKingsLeft = (currentSideChessBitboard.getProcessingSideBitboards().getKing() |
                currentSideChessBitboard.getProcessingSideBitboards().getKnights() |
                currentSideChessBitboard.getOpponentSideBitboards().getKing() |
                currentSideChessBitboard.getOpponentSideBitboards().getKnights()) ==
                currentSideChessBitboard.getOccupied();

        if ((currentSideChessBitboard.getProcessingSideBitboards().getKing() |
                currentSideChessBitboard.getOpponentSideBitboards().getKing()) ==
                currentSideChessBitboard.getOccupied())
            return true;

        if (currentSideChessBitboard.isOneBishop() && isBishopsAndKingsLeft)
            return true;

        if (currentSideChessBitboard.isOneKnight() && isKnightsAndKingsLeft)
            return true;
        return currentSideChessBitboard.isLeftBishopsOnAlikeCellColors() && isBishopsAndKingsLeft;
    }

    @Override
    public Set<MoveInfo> getMoves(final String fenNotation) {
        ChessBitboard currentSideChessBitboard = FENParser.parseFENToBitboards(fenNotation);
        opponentShouldNotBeInCheck(currentSideChessBitboard);

        return getCurrentProcessingSideAllMoves(currentSideChessBitboard);
    }

    @Override
    public BoardSituationInfo getBoardSituationInfo(final String fenNotation) {
        ChessBitboard currentSideChessBitboard = FENParser.parseFENToBitboards(fenNotation);
        opponentShouldNotBeInCheck(currentSideChessBitboard);
        final boolean isCheck = currentSideChessBitboard.getProcessingSideCheckData().getCheckType() == CheckType.ONE
                || currentSideChessBitboard.getProcessingSideCheckData().getCheckType() == CheckType.TWO;

        return new BoardSituationInfo(
                isCheck,
                isMate(currentSideChessBitboard),
                isStalemate(currentSideChessBitboard),
                isDrawByPieceShortage(currentSideChessBitboard)
        );
    }

    private void opponentShouldNotBeInCheck(final ChessBitboard currentSideChessBitboard) {
        SideBitboards currentTurnSideBitboards = currentSideChessBitboard.getProcessingSideBitboards();
        SideBitboards opponentSideBitboards = currentSideChessBitboard.getOpponentSideBitboards();

        ChessBitboard opponentChessBitboard = new ChessBitboard(currentTurnSideBitboards, opponentSideBitboards);
        opponentChessBitboard.setProcessingSideCheckData(SimpleBitboardHandler
                .getCheckData(new ChessBitboard(opponentSideBitboards, currentTurnSideBitboards)));

        if (opponentChessBitboard.getProcessingSideCheckData().getCheckType() == CheckType.ONE ||
                opponentChessBitboard.getProcessingSideCheckData().getCheckType() == CheckType.TWO) {
            throw new IllegalArgumentException("Opponent is in check but it's our turn which is impossible");
        }
    }


}
