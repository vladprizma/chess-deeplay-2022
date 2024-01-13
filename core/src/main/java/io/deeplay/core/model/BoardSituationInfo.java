package io.deeplay.core.model;

/**
 * Содержит информацию о ситуации на доске относительно ходящей стороны.
 */
public class BoardSituationInfo {

    private final boolean isCheck;
    private final boolean isMate;
    private final boolean isStalemate;
    private final boolean isDrawByPieceShortage;

    public BoardSituationInfo(final boolean isCheck,
                              final boolean isMate,
                              final boolean isStalemate,
                              final boolean isDrawByPieceShortage) {
        this.isCheck = isCheck;
        this.isMate = isMate;
        this.isStalemate = isStalemate;
        this.isDrawByPieceShortage = isDrawByPieceShortage;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public boolean isMate() {
        return isMate;
    }

    public boolean isStalemate() {
        return isStalemate;
    }

    public boolean isDrawByPieceShortage() {
        return isDrawByPieceShortage;
    }

}
