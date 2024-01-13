package io.deeplay.core.logic.newlogic;

import io.deeplay.core.logic.BitUtils;
import io.deeplay.core.logic.BitboardDynamicPatterns;
import io.deeplay.core.logic.BitboardPatternsInitializer;
import io.deeplay.core.model.*;
import io.deeplay.core.model.bitboard.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.deeplay.core.logic.BitUtils.containsSameBits;
import static io.deeplay.core.logic.BitUtils.inBetween;
import static java.util.Map.entry;

/**
 * Класс, который обрабатывает всю логику на основе битбордов.
 * Чтобы использовать его, необходимо обратиться к FENParser.parseFENToBitboards(...), либо в SimpleLogic.
 * Вся основная информация в процессе обработки хранится в классах:
 * ChessBitboard, SideBitboards, PieceBitboard, CheckData.
 */
public class SimpleBitboardHandler {
    /**
     * chessBitboard - содержит всю информацию о доске которая может понадобиться для вычисления ходов.
     * ignoredPiece - снятие фигур указанных(в маске битборда) позиций на занятых(occupied) клетках,
     * нужно линейно-ходящим фигурам.
     * restriction - ограничения на возможные ходы, нужно для получения множества допустимых ходов при связности.
     * from - позиция фигуры, ходы которой мы считаем.
     * return - битборд допустимых ходов.
     */
    public static final QuadFunction<ChessBitboard, Long, Long, Integer, Long> getRookMovesBitboard =
            (chessBitboard, ignoredPiece, restriction, from) -> {
                final MagicBoard magic = BitboardPatternsInitializer.rookMagicBoards[from];
                return magic.moveBoards[(int) (((chessBitboard.getOccupied() ^ ignoredPiece) & magic.blockerMask) *
                        BitboardPatternsInitializer.ROOK_MAGIC_NUMBERS[from] >>> magic.shift)] & restriction;
            };
    public static final QuadFunction<ChessBitboard, Long, Long, Integer, Long> getBishopMovesBitboard =
            (chessBitboard, ignoredPiece, restriction, from) -> {
                final MagicBoard magic = BitboardPatternsInitializer.bishopMagicBoards[from];
                return magic.moveBoards[(int) (((chessBitboard.getOccupied() ^ ignoredPiece) & magic.blockerMask) *
                        BitboardPatternsInitializer.BISHOP_MAGIC_NUMBERS[from] >>> magic.shift)] & restriction;
            };
    public static final QuadFunction<ChessBitboard, Long, Long, Integer, Long> getQueenMovesBitboard =
            (chessBitboard, ignoredPiece, restriction, from) ->
                    getBishopMovesBitboard.apply(chessBitboard, ignoredPiece, restriction, from)
                            | getRookMovesBitboard.apply(chessBitboard, ignoredPiece, restriction, from);
    public static final QuadFunction<ChessBitboard, Long, Long, Integer, Long> getKnightMovesBitboard =
            (chessBitboard, ignoredPiece, restriction, from) ->
                    BitboardPatternsInitializer.knightMoveBitboards[from] & restriction;
    public static final QuadFunction<ChessBitboard, Long, Long, Integer, Long> getKingMovesBitboard =
            (chessBitboard, ignoredPiece, restriction, from) ->
                    BitboardPatternsInitializer.kingMoveBitboards[from] & restriction;
    public static final QuadFunction<ChessBitboard, Long, Long, Integer, Long> getWhitePawnMovesBitboard =
            (chessBitboard, ignoredPiece, restriction, from) ->
                    BitboardDynamicPatterns.possibleWhitePawnMovesBitboard(chessBitboard, from) & restriction;
    public static final QuadFunction<ChessBitboard, Long, Long, Integer, Long> getBlackPawnMovesBitboard =
            (chessBitboard, ignoredPiece, restriction, from) ->
                    BitboardDynamicPatterns.possibleBlackPawnMovesBitboard(chessBitboard, from) & restriction;
    public static final BiFunction<ChessBitboard, PieceBitboard, Set<MoveInfo>>
            getPawnWrappedMovesGenerator =
            (chessBitboard, pieceBitboard) -> {
                Set<MoveInfo> moveInfos = new HashSet<>();
                Set<MoveBitboard> moves = getPawnSideWrappedUpMoves(chessBitboard, pieceBitboard.getPositionIndex()); // получили ходы без ограничений
                for (MoveBitboard moveBitboard : moves)
                    if (containsSameBits(moveBitboard.getMoveBitboard(), pieceBitboard.getAllRestrictionsBitboard()) // выбираем ходы удовлетворяющие ограничениям
                            && !containsSameBits(moveBitboard.getMoveBitboard(),
                            chessBitboard.getOpponentSideBitboards().getKing())) // Короля оппонента срубать нельзя
                        moveInfos.add(new MoveInfo(new Coord(pieceBitboard.getPositionIndex()),
                                new Coord(Long.numberOfTrailingZeros(moveBitboard.getMoveBitboard())),
                                moveBitboard.getMoveType(), pieceBitboard.getFigure()));
                return moveInfos;
            };

    public static QuadFunction<ChessBitboard, Long, Long, Integer, Long> getPawnFunction(final Side side) {
        return side == Side.WHITE ? getWhitePawnMovesBitboard : getBlackPawnMovesBitboard;
    }

    public static CheckData getCheckData(final ChessBitboard chessBitboard) {
        final long myKingBitboard = chessBitboard.getProcessingSideBitboards().getKing();
        final int kingIndex = chessBitboard.getProcessingSideBitboards().getKingPieceBitboards().getPositionIndex();
        int countChecks = 0;
        long pieceMoves;
        long allAttacks = 0L;
        long threateningPiecePositionBitboard = 0L;
        // тут учитывается, позиция короля под атакой (главное не срубить короля случайно)
        long threatPieceBitboard = 0L;
        for (PieceBitboard pieceBitboard : chessBitboard.getOpponentSideBitboards().getPieceBitboards()) {
            pieceMoves = pieceBitboard.getMovesWithIgnoredPiece(chessBitboard, myKingBitboard);
            // В pieceMoves могли попасть ходы пешек, а должны только атаки
            if (pieceBitboard.getFigure() == Figure.W_PAWN || pieceBitboard.getFigure() == Figure.B_PAWN)
                pieceMoves = sidePawnAttacks.get(pieceBitboard.getSide()).get(pieceBitboard.getPositionIndex());
            if (containsSameBits(pieceMoves, myKingBitboard)) {
                countChecks++;
                threatPieceBitboard |= inBetween(kingIndex, pieceBitboard.getPositionIndex());
                threateningPiecePositionBitboard |= pieceBitboard.getPositionBitboard();
            }
            allAttacks |= pieceMoves;
        }
        allAttacks |= chessBitboard.getOpponentSideBitboards().getKingPieceBitboards().getAllMovesBitboard();
        if (countChecks > 1) {
            // 0L в threatPieceBitboard и threateningPiecePositionBitboard,
            // чтобы остальные фигуры не могли спасти короля, т.к. при двойном шахе может походить только король
            return new CheckData(CheckType.TWO, 0L, 0L, allAttacks);
        }
        if (countChecks == 1)
            return new CheckData(CheckType.ONE,
                    threatPieceBitboard ^ myKingBitboard,
                    threateningPiecePositionBitboard,
                    allAttacks);
        // ~0L, т.к. нету угрожающей фигуры, поэтому фигуры могут ходить ПО ВСЕМ клеткам
        return new CheckData(CheckType.NONE, ~0L, 0L, allAttacks);
    }

    public static final BiFunction<ChessBitboard, PieceBitboard, Set<MoveInfo>> getWrappedMovesGenerator =
            (chessBitboard, pieceBitboard) -> {
                Set<MoveInfo> movesInfo = new HashSet<>();
                final long notMyPieces = ~chessBitboard.getProcessingSidePieces();
                final long notOpponentPieces = ~chessBitboard.getOpponentPieces();
                for (long possibleMove :
                        BitUtils.segregatePositions(pieceBitboard.getMovesUnderRestrictions(chessBitboard))) {

                    if (containsSameBits(possibleMove, chessBitboard.getOpponentPieces()
                            ^ chessBitboard.getOpponentSideBitboards().getKing())) // Короля оппонента срубать нельзя
                        movesInfo.add(new MoveInfo(new Coord(pieceBitboard.getPositionIndex()),
                                new Coord(Long.numberOfTrailingZeros(possibleMove)),
                                MoveType.USUAL_ATTACK, pieceBitboard.getFigure()));
                    if (containsSameBits(possibleMove, notMyPieces & notOpponentPieces))
                        movesInfo.add(new MoveInfo(new Coord(pieceBitboard.getPositionIndex()),
                                new Coord(Long.numberOfTrailingZeros(possibleMove)),
                                MoveType.USUAL_MOVE, pieceBitboard.getFigure()));
                }
                return movesInfo;
            };
    public static final BiFunction<ChessBitboard, PieceBitboard, Set<MoveInfo>>
            getKingWrappedMovesGenerator =
            (chessBitboard, pieceBitboard) -> {
                Set<MoveInfo> moveInfos = new HashSet<>();
                Set<MoveBitboard> moves = getKingSideWrappedUpMoves(chessBitboard, pieceBitboard.getPositionIndex());
                for (MoveBitboard moveBitboard : moves)
                    if (!containsSameBits(moveBitboard.getMoveBitboard(),
                            chessBitboard.getOpponentSideBitboards().getKing())) // Короля оппонента срубать нельзя
                        moveInfos.add(new MoveInfo(new Coord(pieceBitboard.getPositionIndex()),
                                new Coord(Long.numberOfTrailingZeros(moveBitboard.getMoveBitboard())),
                                moveBitboard.getMoveType(), pieceBitboard.getFigure()));
                return moveInfos;
            };
    /**
     * Пешки двигаются по-разному, в зависимости от стороны, поэтому нужно учитывать какая сторона будет двигать пешку.
     * В мапе хранятся только АТАКИ пешек, остальные виды ПЕРЕДВИЖЕНИЙ пешек, считаются отдельно,
     * в классе {@link io.deeplay.core.logic.BitboardDynamicPatterns}.
     */
    private static final Map<Side, List<Long>> sidePawnAttacks = new EnumMap<>(
            Map.ofEntries(
                    entry(Side.WHITE, BitboardPatternsInitializer.whitePawnMoveBitboards),
                    entry(Side.BLACK, BitboardPatternsInitializer.blackPawnMoveBitboards)
            ));

    /**
     * Считает все допустимые ходы.
     * В процессе добавляет ограничения на допустимые для хода клетки(PieceBitboard),
     * ограничения учитываются только при вызове метода pieceBitboard.getMovesUnderRestrictions(...).
     *
     * @param chessBitboard для корректной работы должна быть с информацией о шахе (checkData)
     * @return количество фигур которые могут передвигаться, если количество таких фигур - 0, то это мат/пат.
     */
    public static int countAllPossibleMoves(final ChessBitboard chessBitboard) {
        Objects.requireNonNull(chessBitboard.getProcessingSideCheckData());
        Set<Integer> pinnedPieces = new HashSet<>();
        int countFiguresThatCanMove = 0;
        final long processingSideKing = chessBitboard.getProcessingSideBitboards().getKing();
        final int kingIndex = chessBitboard.getProcessingSideBitboards().getKingPieceBitboards().getPositionIndex();
        final long notMyPieces = ~chessBitboard.getProcessingSidePieces();
        long opponentPieceAttack;
        final long threatPieceBitboard = chessBitboard.getProcessingSideCheckData().getThreatPieceBitboard();
        final long threateningPiecePositionBitboard =
                chessBitboard.getProcessingSideCheckData().getThreateningPiecePositionBitboard();
        final Set<Figure> slidingPiecesFigures = Stream.of(
                Figure.W_BISHOP, Figure.W_ROOK, Figure.W_QUEEN,
                Figure.B_BISHOP, Figure.B_ROOK, Figure.B_QUEEN).collect(Collectors.toSet()); // все линейно-ходящие фигуры
        final List<PieceBitboard> opponentSlidingPieces = chessBitboard.getOpponentSideBitboards().getPieceBitboards()
                .stream().filter(piece -> slidingPiecesFigures.contains(piece.getFigure())).collect(Collectors.toList());
        // Считаем связанность.
        // Можно воспринимать каждую фигуру как "стенку", каждую из которых мы по очереди снимаем.
        for (PieceBitboard opponentPieceBitboard : opponentSlidingPieces) {
            if (opponentPieceBitboard.getPositionBitboard() == threateningPiecePositionBitboard)
                continue; // Для фигуры которая поставила шах связанность считать не надо.
            for (PieceBitboard processingSidePieceBitboard :
                    chessBitboard.getProcessingSideBitboards().getPieceBitboards()) {

                opponentPieceAttack = opponentPieceBitboard // xray-атака (атака без учета текущей фигуры)
                        .getMovesWithIgnoredPiece(chessBitboard, processingSidePieceBitboard.getPositionBitboard()); // Снимаем "стенку".
                if (containsSameBits(opponentPieceAttack, processingSideKing)) { // Связанная фигура.
                    // Объединяем возможные ходы фигуры, xray-атаку связывающей фигуры и атаки фигуры которая поставила шах
                    processingSidePieceBitboard.addRestriction(
                            (inBetween(kingIndex, opponentPieceBitboard.getPositionIndex()) ^ processingSideKing)
                                    & threatPieceBitboard); // Если шах, то не должны учитываться другие способы ходить
                    pinnedPieces.add(processingSidePieceBitboard.getPositionIndex());
                    if (processingSidePieceBitboard.getMovesUnderRestrictions(chessBitboard) != 0L) {
                        countFiguresThatCanMove++;
                    }
                }
            }
        }
        // Здесь смотрим фигуры которые не связаны.
        // Объединяем позицию и атаку фигуры поставившей шах с возможностью походить нашей.
        for (PieceBitboard processingSidePieceBitboard :
                chessBitboard.getProcessingSideBitboards().getPieceBitboards()) {

            if (!pinnedPieces.contains(processingSidePieceBitboard.getPositionIndex())) {
                processingSidePieceBitboard.addRestriction(threatPieceBitboard & notMyPieces);
                if (processingSidePieceBitboard.getMovesUnderRestrictions(chessBitboard) != 0L) {
                    countFiguresThatCanMove++;
                }
            }
        }
        // Считаем допустимые ходы для короля
        chessBitboard.getProcessingSideBitboards().getKingPieceBitboards().addRestriction(
                ~chessBitboard.getProcessingSideCheckData().getAllAttacks() // Клетки НЕ под атакой.
                        & ~chessBitboard.getProcessingSidePieces()); // Нельзя нападать на своих.
        if (chessBitboard.getProcessingSideBitboards()
                .getKingPieceBitboards().getMovesUnderRestrictions(chessBitboard) != 0L)
            countFiguresThatCanMove++;

        chessBitboard.setCountFiguresThatCanMove(countFiguresThatCanMove);
        return countFiguresThatCanMove;
    }

    public static Set<MoveBitboard> getPawnSideWrappedUpMoves(final ChessBitboard chessBitboard, final int from) {
        return chessBitboard.getProcessingSide() == Side.WHITE ?
                BitboardDynamicPatterns.possibleWhitePawnMoves(chessBitboard, from) :
                BitboardDynamicPatterns.possibleBlackPawnMoves(chessBitboard, from);
    }

    public static Set<MoveBitboard> getKingSideWrappedUpMoves(final ChessBitboard chessBitboard, final int from) {
        return chessBitboard.getProcessingSide() == Side.WHITE ?
                BitboardDynamicPatterns.possibleWhiteKingMoves(chessBitboard, from) :
                BitboardDynamicPatterns.possibleBlackKingMoves(chessBitboard, from);
    }

    public static Set<MoveInfo> getCurrentProcessingSideAllMoves(final ChessBitboard chessBitboard) {
        Set<MoveInfo> moveInfos = new HashSet<>();
        for (PieceBitboard pieceBitboard : chessBitboard.getProcessingSideBitboards().getPieceBitboards()) {
            moveInfos.addAll(pieceBitboard.getWrappedMovesUnderRestriction(chessBitboard));
        }
        moveInfos.addAll(chessBitboard.getProcessingSideBitboards()
                .getKingPieceBitboards().getWrappedMovesUnderRestriction(chessBitboard));
        return moveInfos;
    }

}
