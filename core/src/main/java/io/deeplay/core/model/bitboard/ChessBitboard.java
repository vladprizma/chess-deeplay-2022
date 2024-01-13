package io.deeplay.core.model.bitboard;

import io.deeplay.core.logic.BitUtils;
import io.deeplay.core.model.Side;

import static io.deeplay.core.logic.BitboardPatternsInitializer.blackCells;
import static io.deeplay.core.logic.BitboardPatternsInitializer.whiteCells;

/**
 * Класс хранит обобщенную информацию о шахматной доске.
 * Так же отвечает на вопрос "Относительно кого считаем допустимые ходы".
 */
public class ChessBitboard {

    private final Side processingSide;
    private final SideBitboards processingSideBitboards;
    private final SideBitboards opponentSideBitboards;
    private long enPassantFile;

    // Получается из комбинации полей выше
    private final long opponentPieces;
    private final long processingSidePieces;
    private final long occupied;
    private final long empty;
    private CheckData processingSideCheckData;

    // Права на рокировку
    private boolean whiteKingSideCastlingRight;
    private boolean whiteQueenSideCastlingRight;
    private boolean blackKingSideCastlingRight;
    private boolean blackQueenSideCastlingRight;

    private int countFiguresThatCanMove;

    /**
     * Не коммутативный конструктор.
     * Поменяв порядок параметров местами, логика будет работать относительной другой стороны.
     *
     * @param processingSideBitboards обрабатываемая сторона.
     * @param opponentSideBitboards   сторона, относительно которой будет производиться расчёт допустимых ходов.
     */
    public ChessBitboard(final SideBitboards processingSideBitboards,
                         final SideBitboards opponentSideBitboards) {
        this.processingSideBitboards = processingSideBitboards;
        this.processingSide = processingSideBitboards.getSide();
        this.opponentSideBitboards = opponentSideBitboards;
        this.opponentPieces = opponentSideBitboards.orOperationOnAllBitboards();
        this.processingSidePieces = processingSideBitboards.orOperationOnAllBitboards();
        this.occupied = opponentPieces | processingSidePieces;
        this.empty = ~occupied;
        this.enPassantFile = 0L;
    }

    public boolean isOneBishop() {
        return BitUtils.bitCount(
                processingSideBitboards.getBishops() | opponentSideBitboards.getBishops()) == 1;
    }

    public boolean isOneKnight() {
        return BitUtils.bitCount(
                processingSideBitboards.getKnights() | opponentSideBitboards.getKnights()) == 1;
    }

    public boolean isLeftBishopsOnAlikeCellColors() {
        long allBishops = processingSideBitboards.getBishops() | opponentSideBitboards.getBishops();
        return (allBishops & whiteCells) == allBishops || (allBishops & blackCells) == allBishops;
    }

    public Side getProcessingSide() {
        return processingSide;
    }

    public void setEnPassantFile(final long enPassantFile) {
        this.enPassantFile = enPassantFile;
    }

    public SideBitboards getProcessingSideBitboards() {
        return processingSideBitboards;
    }

    public SideBitboards getOpponentSideBitboards() {
        return opponentSideBitboards;
    }

    public long getProcessingSidePieces() {
        return processingSidePieces;
    }

    public long getEnPassantFile() {
        return enPassantFile;
    }

    public long getOpponentPieces() {
        return opponentPieces;
    }

    public CheckData getProcessingSideCheckData() {
        return processingSideCheckData;
    }

    public long getOccupied() {
        return occupied;
    }

    public long getEmpty() {
        return empty;
    }

    public void setProcessingSideCheckData(final CheckData processingSideCheckData) {
        this.processingSideCheckData = processingSideCheckData;
    }

    public boolean isWhiteKingSideCastlingRight() {
        return whiteKingSideCastlingRight;
    }

    public void setWhiteKingSideCastlingRight(boolean whiteKingSideCastlingRight) {
        this.whiteKingSideCastlingRight = whiteKingSideCastlingRight;
    }

    public boolean isWhiteQueenSideCastlingRight() {
        return whiteQueenSideCastlingRight;
    }

    public void setWhiteQueenSideCastlingRight(boolean whiteQueenSideCastlingRight) {
        this.whiteQueenSideCastlingRight = whiteQueenSideCastlingRight;
    }

    public boolean isBlackKingSideCastlingRight() {
        return blackKingSideCastlingRight;
    }

    public void setBlackKingSideCastlingRight(boolean blackKingSideCastlingRight) {
        this.blackKingSideCastlingRight = blackKingSideCastlingRight;
    }

    public boolean isBlackQueenSideCastlingRight() {
        return blackQueenSideCastlingRight;
    }

    public void setBlackQueenSideCastlingRight(boolean blackQueenSideCastlingRight) {
        this.blackQueenSideCastlingRight = blackQueenSideCastlingRight;
    }

    public int getCountFiguresThatCanMove() {
        return countFiguresThatCanMove;
    }

    public void setCountFiguresThatCanMove(int countFiguresThatCanMove) {
        this.countFiguresThatCanMove = countFiguresThatCanMove;
    }

    @Override
    public String toString() {
        return "ChessBitboard{" +
                "processingSide=" + processingSide +
                ", processingSideBitboards=" + processingSideBitboards +
                ", opponentSideBitboards=" + opponentSideBitboards +
                ", enPassantFile=" + enPassantFile +
                ", opponentPieces=" + opponentPieces +
                ", processingSidePieces=" + processingSidePieces +
                ", occupied=" + occupied +
                ", empty=" + empty +
                ", processingSideCheckData=" + processingSideCheckData +
                ", whiteKingSideCastlingRight=" + whiteKingSideCastlingRight +
                ", whiteQueenSideCastlingRight=" + whiteQueenSideCastlingRight +
                ", blackKingSideCastlingRight=" + blackKingSideCastlingRight +
                ", blackQueenSideCastlingRight=" + blackQueenSideCastlingRight +
                '}';
    }
}
