package io.deeplay.core.model.bitboard;

public class CheckData {
    /**
     * Здесь находятся все клетки под атакой противника, без учёта своей фигуры короля(чтобы король не мог отступить
     * под x-ray атаку той же линейно-ходящей фигуры), в виде битборда.
     * Нужно, для того чтобы отсечь множество допустимых ходов короля.
     */
    private final long allAttacks;
    private final CheckType checkType;
    /**
     * Если checkType = ONE, то в переменной содержится информация о местоположении угрозы королю
     * и клетках которые она атакует в виде битборда.
     * Достаточно объединить множество ходов AND данное поле(множество клеток под атакой на пути к королю
     * (включая угрожающую фигуру)) и мы получим клетки, встав на которые мы защитим короля от шаха.
     */
    private final long threatPieceBitboard;
    /**
     * Если checkType = ONE, то в переменной содержится информация о местоположении угрозы королю в виде битборда.
     * Поле нужно, чтобы найти связанные фигуры(поиск таких фигур основан на идее "убрал-проверил на шах"),
     * т.к. оно гарантирует что уже есть шах, оно будет мешать проверке на связанность.
     */
    private final long threateningPiecePositionBitboard;

    public CheckData(final CheckType checkType,
                     final long threatPieceBitboard,
                     final long threateningPiecePositionBitboard,
                     final long allAttacks) {
        this.checkType = checkType;
        this.threatPieceBitboard = threatPieceBitboard;
        this.threateningPiecePositionBitboard = threateningPiecePositionBitboard;
        this.allAttacks = allAttacks;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public long getThreatPieceBitboard() {
        return threatPieceBitboard;
    }

    public long getAllAttacks() {
        return allAttacks;
    }

    public long getThreateningPiecePositionBitboard() {
        return threateningPiecePositionBitboard;
    }

    @Override
    public String toString() {
        return "CheckData{" +
                "checkType=" + checkType +
                ", threatPieceBitboard=" + threatPieceBitboard +
                ", threateningPiecePositionBitboard=" + threateningPiecePositionBitboard +
                ", allAttacks=" + allAttacks +
                '}';
    }
}
