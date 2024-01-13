package io.deeplay.core.logic;

import io.deeplay.core.model.MoveType;
import io.deeplay.core.model.Side;
import io.deeplay.core.model.bitboard.ChessBitboard;
import io.deeplay.core.model.bitboard.MoveBitboard;
import io.deeplay.core.model.bitboard.PieceBitboard;
import io.deeplay.core.model.bitboard.SideBitboards;

import java.util.HashSet;
import java.util.Set;

import static io.deeplay.core.logic.BitUtils.*;


/**
 * Класс содержит методы, которые производят вычисления каждый раз когда нужно, в отличие от BitboardPatternsInitializer
 */
public class BitboardDynamicPatterns {
    // Маски клеток которые не должны находиться под атакой
    private static final long whiteKingSideShouldBeSafeMask = 7L << 4;
    private static final long whiteQueenSideShouldBeSafeMask = 7L << 2;
    private static final long blackKingSideShouldBeSafeMask = 7L << 60;
    private static final long blackQueenSideShouldBeSafeMask = 7L << 58;

    // Маски позиций которые должны быть пусты
    private static final long whiteKingSideShouldBeEmptySquares = 3L << 5;
    private static final long whiteQueenSideShouldBeEmptySquares = 7L << 1;
    private static final long blackKingSideShouldBeEmptySquares = 3L << 61;
    private static final long blackQueenSideShouldBeEmptySquares = 7L << 57;

    public static Set<MoveBitboard> possibleWhiteKingMoves(final ChessBitboard chessBitboard, final int from) { // TODO: from не используется
        if (chessBitboard.getProcessingSide() != Side.WHITE)
            throw new IllegalArgumentException("Calculation method for white pieces is impossible for black pieces.");
        Set<MoveBitboard> moves = new HashSet<>();
        final long occupied = chessBitboard.getOccupied();
        final long notMyPieces = ~chessBitboard.getProcessingSidePieces();
        final long notOpponentPieces = ~chessBitboard.getOpponentPieces();
        final long underAttack = chessBitboard.getProcessingSideCheckData().getAllAttacks();
        final PieceBitboard kingPieceBitboard = chessBitboard.getProcessingSideBitboards().getKingPieceBitboards();
        final long kingMoves = kingPieceBitboard.getMovesUnderRestrictions(chessBitboard);
        for (long possibleMove : BitUtils.segregatePositions(kingMoves)) {
            if (containsSameBits(possibleMove, chessBitboard.getOpponentPieces()))
                moves.add(new MoveBitboard(MoveType.USUAL_ATTACK, possibleMove));
            if (containsSameBits(possibleMove, notMyPieces & notOpponentPieces))
                moves.add(new MoveBitboard(MoveType.USUAL_MOVE, possibleMove));
        }
        if (chessBitboard.isWhiteKingSideCastlingRight()
                && !containsSameBits(whiteKingSideShouldBeSafeMask, underAttack)
                && !containsSameBits(whiteKingSideShouldBeEmptySquares, occupied))
            moves.add(new MoveBitboard(MoveType.CASTLE_SHORT, 1L << 6)); // конечная позиция короля после рокировки
        if (chessBitboard.isWhiteQueenSideCastlingRight()
                && !containsSameBits(whiteQueenSideShouldBeSafeMask, underAttack)
                && !containsSameBits(whiteQueenSideShouldBeEmptySquares, occupied))
            moves.add(new MoveBitboard(MoveType.CASTLE_LONG, 1L << 2));

        return moves;
    }

    public static Set<MoveBitboard> possibleBlackKingMoves(final ChessBitboard chessBitboard, final int from) {
        if (chessBitboard.getProcessingSide() != Side.BLACK)
            throw new IllegalArgumentException("Calculation method for black pieces is impossible for white pieces.");
        Set<MoveBitboard> moves = new HashSet<>();
        final long occupied = chessBitboard.getOccupied();
        final long notMyPieces = ~chessBitboard.getProcessingSidePieces();
        final long notOpponentPieces = ~chessBitboard.getOpponentPieces();
        final long underAttack = chessBitboard.getProcessingSideCheckData().getAllAttacks();
        final PieceBitboard kingPieceBitboard = chessBitboard.getProcessingSideBitboards().getKingPieceBitboards();
        final long kingMoves = kingPieceBitboard.getMovesUnderRestrictions(chessBitboard);
        for (long possibleMove : BitUtils.segregatePositions(kingMoves)) {
            if (containsSameBits(possibleMove, chessBitboard.getOpponentPieces()))
                moves.add(new MoveBitboard(MoveType.USUAL_ATTACK, possibleMove));
            if (containsSameBits(possibleMove, notMyPieces & notOpponentPieces))
                moves.add(new MoveBitboard(MoveType.USUAL_MOVE, possibleMove));
        }
        if (chessBitboard.isBlackKingSideCastlingRight()
                && !containsSameBits(blackKingSideShouldBeSafeMask, underAttack)
                && !containsSameBits(blackKingSideShouldBeEmptySquares, occupied))
            moves.add(new MoveBitboard(MoveType.CASTLE_SHORT, 1L << 62)); // конечная позиция короля после рокировки
        if (chessBitboard.isBlackQueenSideCastlingRight()
                && !containsSameBits(blackQueenSideShouldBeSafeMask, underAttack)
                && !containsSameBits(blackQueenSideShouldBeEmptySquares, occupied))
            moves.add(new MoveBitboard(MoveType.CASTLE_LONG, 1L << 58));

        return moves;
    }

    public static Set<MoveBitboard> possibleWhitePawnMoves(final ChessBitboard chessBitboard, final int from) {
        if (chessBitboard.getProcessingSide() != Side.WHITE)
            throw new IllegalArgumentException("Calculation method for white pieces is impossible for black pieces.");
        final long pawnToMoveBitboard = 1L << from;
        final long notMyPieces = chessBitboard.getOpponentPieces();
        final long opponentPawns = chessBitboard.getOpponentSideBitboards().getPawns();
        final long occupied = chessBitboard.getOccupied();
        final long empty = chessBitboard.getEmpty();
        final long enPassantFile = chessBitboard.getEnPassantFile();

        Set<MoveBitboard> moves = new HashSet<>();
        long satisfyingMoves = (pawnToMoveBitboard << 9) & notMyPieces & occupied & ~MASK_RANK_8 & ~MASK_FILE_A; // capture right
        moves.add(new MoveBitboard(MoveType.PAWN_ATTACK, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard << 7) & notMyPieces & occupied & ~MASK_RANK_8 & ~MASK_FILE_H; // capture left
        moves.add(new MoveBitboard(MoveType.PAWN_ATTACK, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard << 8) & empty & ~MASK_RANK_8;// move 1 forward
        moves.add(new MoveBitboard(MoveType.USUAL_MOVE, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard << 16) & empty & (empty << 8) & MASK_RANK_4; // move 2 forward
        moves.add(new MoveBitboard(MoveType.PAWN_LONG_MOVE, satisfyingMoves));

        //Promotion
        satisfyingMoves = (pawnToMoveBitboard << 9) & notMyPieces & occupied & MASK_RANK_8 & ~MASK_FILE_A; // pawn promotion by capture right
        moves.add(new MoveBitboard(MoveType.PAWN_TO_FIGURE_ATTACK, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard << 7) & notMyPieces & occupied & MASK_RANK_8 & ~MASK_FILE_H; // pawn promotion by capture left
        moves.add(new MoveBitboard(MoveType.PAWN_TO_FIGURE_ATTACK, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard << 8) & empty & MASK_RANK_8; // pawn promotion by move 1 forward
        moves.add(new MoveBitboard(MoveType.PAWN_TO_FIGURE, satisfyingMoves));

        if (enPassantFile != 0L) {
            // en passant right
            // shows destination, not piece to remove
            satisfyingMoves = (pawnToMoveBitboard << 9) & (opponentPawns << 8) & MASK_RANK_6 & ~MASK_FILE_A & enPassantFile;
            if (satisfyingMoves != 0) {
                moves.add(new MoveBitboard(MoveType.PAWN_ON_GO_ATTACK, satisfyingMoves));
            }
            // en passant left
            satisfyingMoves = (pawnToMoveBitboard << 7) & (opponentPawns << 8) & MASK_RANK_6 & ~MASK_FILE_H & enPassantFile;
            if (satisfyingMoves != 0) {
                moves.add(new MoveBitboard(MoveType.PAWN_ON_GO_ATTACK, satisfyingMoves));
            }
        }
        return moves;
    }

    public static Set<MoveBitboard> possibleBlackPawnMoves(final ChessBitboard chessBitboard, final int from) {
        if (chessBitboard.getProcessingSide() != Side.BLACK)
            throw new IllegalArgumentException("Calculation method for black pieces is impossible for white pieces.");
        final long pawnToMoveBitboard = 1L << from;
        final long notMyPieces = chessBitboard.getOpponentPieces();
        final long opponentPawns = chessBitboard.getOpponentSideBitboards().getPawns();
        final long occupied = chessBitboard.getOccupied();
        final long empty = chessBitboard.getEmpty();
        final long enPassantFile = chessBitboard.getEnPassantFile();

        Set<MoveBitboard> moves = new HashSet<>();
        long satisfyingMoves = (pawnToMoveBitboard >>> 9) & notMyPieces & occupied & ~MASK_RANK_1 & ~MASK_FILE_H; // capture right
        moves.add(new MoveBitboard(MoveType.PAWN_ATTACK, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard >>> 7) & notMyPieces & occupied & ~MASK_RANK_1 & ~MASK_FILE_A; // capture left
        moves.add(new MoveBitboard(MoveType.PAWN_ATTACK, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard >>> 8) & empty & ~MASK_RANK_1; // move 1 forward
        moves.add(new MoveBitboard(MoveType.USUAL_MOVE, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard >>> 16) & empty & (empty >>> 8) & MASK_RANK_5; // move 2 forward
        moves.add(new MoveBitboard(MoveType.PAWN_LONG_MOVE, satisfyingMoves));

        //Promotion
        satisfyingMoves = (pawnToMoveBitboard >>> 9) & notMyPieces & occupied & MASK_RANK_1 & ~MASK_FILE_H; // pawn promotion by capture right
        moves.add(new MoveBitboard(MoveType.PAWN_TO_FIGURE_ATTACK, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard >>> 7) & notMyPieces & occupied & MASK_RANK_1 & ~MASK_FILE_A; // pawn promotion by capture left
        moves.add(new MoveBitboard(MoveType.PAWN_TO_FIGURE_ATTACK, satisfyingMoves));

        satisfyingMoves = (pawnToMoveBitboard >>> 8) & empty & MASK_RANK_1;// pawn promotion by move 1 forward
        moves.add(new MoveBitboard(MoveType.PAWN_TO_FIGURE, satisfyingMoves));

        if (enPassantFile != 0L) {
            // en passant right
            satisfyingMoves = (pawnToMoveBitboard >>> 9) & (opponentPawns >>> 8) & MASK_RANK_3 & ~MASK_FILE_H & enPassantFile;
            if (satisfyingMoves != 0) {
                moves.add(new MoveBitboard(MoveType.PAWN_ON_GO_ATTACK, satisfyingMoves));
            }
            // en passant left
            satisfyingMoves = (pawnToMoveBitboard >>> 7) & (opponentPawns >>> 8) & MASK_RANK_3 & ~MASK_FILE_A & enPassantFile;
            if (satisfyingMoves != 0) {
                moves.add(new MoveBitboard(MoveType.PAWN_ON_GO_ATTACK, satisfyingMoves));
            }
        }
        return moves;
    }

    public static long possibleWhitePawnMovesBitboard(final ChessBitboard chessBitboard, final int from) {
        SideBitboards blackSideBitboard = chessBitboard.getOpponentSideBitboards();
        if (blackSideBitboard.getSide() != Side.BLACK) {
            blackSideBitboard = chessBitboard.getProcessingSideBitboards();
        }
        final long pawnToMoveBitboard = 1L << from;
        final long blackPieces = blackSideBitboard.orOperationOnAllBitboards();
        final long blackPawns = blackSideBitboard.getPawns();
        final long occupied = chessBitboard.getOccupied();
        final long empty = chessBitboard.getEmpty();
        final long enPassantFile = chessBitboard.getEnPassantFile();

        long allMoves = (pawnToMoveBitboard << 9) & blackPieces & occupied & ~MASK_RANK_8 & ~MASK_FILE_A;
        allMoves |= (pawnToMoveBitboard << 7) & blackPieces & occupied & ~MASK_RANK_8 & ~MASK_FILE_H;
        allMoves |= (pawnToMoveBitboard << 8) & empty & ~MASK_RANK_8;
        allMoves |= (pawnToMoveBitboard << 16) & empty & (empty << 8) & MASK_RANK_4;
        allMoves |= (pawnToMoveBitboard << 9) & blackPieces & occupied & MASK_RANK_8 & ~MASK_FILE_A;
        allMoves |= (pawnToMoveBitboard << 7) & blackPieces & occupied & MASK_RANK_8 & ~MASK_FILE_H;
        allMoves |= (pawnToMoveBitboard << 8) & empty & MASK_RANK_8;
        if (enPassantFile != 0L) {
            allMoves |= (pawnToMoveBitboard << 9) & (blackPawns << 8) & MASK_RANK_6 & ~MASK_FILE_A & enPassantFile;
            allMoves |= (pawnToMoveBitboard << 7) & (blackPawns << 8) & MASK_RANK_6 & ~MASK_FILE_H & enPassantFile;
        }
        return allMoves;
    }

    public static long possibleBlackPawnMovesBitboard(final ChessBitboard chessBitboard, final int from) {
        SideBitboards whiteSideBitboard = chessBitboard.getOpponentSideBitboards();
        if (whiteSideBitboard.getSide() != Side.WHITE) {
            whiteSideBitboard = chessBitboard.getProcessingSideBitboards();
        }
        final long pawnToMoveBitboard = 1L << from;
        final long whitePieces = whiteSideBitboard.orOperationOnAllBitboards();
        final long whitePawns = whiteSideBitboard.getPawns();
        final long occupied = chessBitboard.getOccupied();
        final long empty = chessBitboard.getEmpty();
        final long enPassantFile = chessBitboard.getEnPassantFile();

        long allMoves = (pawnToMoveBitboard >>> 9) & whitePieces & occupied & ~MASK_RANK_1 & ~MASK_FILE_H; // capture right
        allMoves |= (pawnToMoveBitboard >>> 7) & whitePieces & occupied & ~MASK_RANK_1 & ~MASK_FILE_A; // capture left
        allMoves |= (pawnToMoveBitboard >>> 8) & empty & ~MASK_RANK_1; // move 1 forward
        allMoves |= (pawnToMoveBitboard >>> 16) & empty & (empty >>> 8) & MASK_RANK_5; // move 2 forward
        allMoves |= (pawnToMoveBitboard >>> 9) & whitePieces & occupied & MASK_RANK_1 & ~MASK_FILE_H; // pawn promotion by capture right
        allMoves |= (pawnToMoveBitboard >>> 7) & whitePieces & occupied & MASK_RANK_1 & ~MASK_FILE_A; // pawn promotion by capture left
        allMoves |= (pawnToMoveBitboard >>> 8) & empty & MASK_RANK_1;// pawn promotion by move 1 forward
        if (enPassantFile != 0L) {
            allMoves |= (pawnToMoveBitboard >>> 9) & (whitePawns >>> 8) & MASK_RANK_3 & ~MASK_FILE_H & enPassantFile;
            allMoves |= (pawnToMoveBitboard >>> 7) & (whitePawns >>> 8) & MASK_RANK_3 & ~MASK_FILE_A & enPassantFile;
        }
        return allMoves;
    }
}
