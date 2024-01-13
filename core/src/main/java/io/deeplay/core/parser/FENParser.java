package io.deeplay.core.parser;

import com.google.common.base.Joiner;
import io.deeplay.core.logic.BitUtils;
import io.deeplay.core.model.Figure;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.MoveType;
import io.deeplay.core.model.Side;
import io.deeplay.core.model.bitboard.ChessBitboard;
import io.deeplay.core.model.bitboard.ChessBitboardFactory;
import io.deeplay.core.model.bitboard.SideBitboards;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;

public class FENParser {
    private static final int BOARD_SIZE = 8; // 8 x 8
    // Маленькие буквы - фигуры черных, большие - белых
    private static final Set<Character> allPiecesCharacterRepresentation =
            Stream.of('p', 'n', 'b', 'r', 'q', 'k', 'P', 'N', 'B', 'R', 'Q', 'K')
                    .collect(Collectors.toUnmodifiableSet());
    private static final Map<Character, Long> fileCharToBitboardRepresentation =
            Map.ofEntries(
                    entry('a', BitUtils.MASK_FILE_A),
                    entry('b', BitUtils.MASK_FILE_B),
                    entry('c', BitUtils.MASK_FILE_C),
                    entry('d', BitUtils.MASK_FILE_D),
                    entry('e', BitUtils.MASK_FILE_E),
                    entry('f', BitUtils.MASK_FILE_F),
                    entry('g', BitUtils.MASK_FILE_G),
                    entry('h', BitUtils.MASK_FILE_H)
            );
    private static final Map<Character, Side> charToSideRepresentation =
            Map.ofEntries(
                    entry('w', Side.WHITE),
                    entry('b', Side.BLACK)
            );
    private static final Map<Figure, String> fileCharToFigureRepresentation =
            Map.ofEntries(
                    entry(Figure.B_PAWN, "p"),
                    entry(Figure.B_KNIGHT, "n"),
                    entry(Figure.B_BISHOP, "b"),
                    entry(Figure.B_ROOK, "r"),
                    entry(Figure.B_QUEEN, "q"),
                    entry(Figure.B_KING, "k"),
                    entry(Figure.W_PAWN, "P"),
                    entry(Figure.W_KNIGHT, "N"),
                    entry(Figure.W_BISHOP, "B"),
                    entry(Figure.W_ROOK, "R"),
                    entry(Figure.W_QUEEN, "Q"),
                    entry(Figure.W_KING, "K")
            );

    public static ChessBitboard parseFENToBitboards(final String fen) {
        final String piecePlacement = getPiecePlacement(fen);
        // TODO: проверка на то что есть символы "/pnbrqkPNBRQK" + меньше макс. длины fen, логирование, исключения
        // TODO: извлекаем charAt и считаем '/' а так же количество свободных фигур
        //  если разделителей ('/') будет не 7 штук или sum(свободных клеток + занятых) != 8, то ошибка

        final int lastIndex = BOARD_SIZE * BOARD_SIZE - 1;
        // т.к. нужно знать индекс фигуры для битборда, нужно так же считать пустые клетки нотации FEN
        int backwardPrinting = BOARD_SIZE - 1;
        int rowCount = 0; // считаем начиная с верхней строки (8 ранг на шахматной доске)
        Map<Character, Long> piecesBitboard = new HashMap<>();
        for (char ch : allPiecesCharacterRepresentation) {
            piecesBitboard.put(ch, 0L);
        }
        for (String rank : piecePlacement.split("/")) {
            for (char currentChar : rank.toCharArray()) {
                if (Character.isDigit(currentChar)) {
                    backwardPrinting -= currentChar - '0'; // widening casting
                }
                if (allPiecesCharacterRepresentation.contains(currentChar)) {
                    piecesBitboard.put(currentChar,
                            piecesBitboard.get(currentChar) | (1L << (lastIndex - (rowCount * BOARD_SIZE + backwardPrinting))));
                }
                if (Character.isLetter(currentChar)) {
                    backwardPrinting--;
                }
            }
            backwardPrinting = BOARD_SIZE - 1;
            rowCount++;
        }
        return ChessBitboardFactory.createChessBitboard(Map.ofEntries(
                entry(Side.WHITE, new SideBitboards(piecesBitboard.entrySet().stream()
                        .filter(x -> Character.isUpperCase(x.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), Side.WHITE)),
                entry(Side.BLACK, new SideBitboards(piecesBitboard.entrySet().stream()
                        .filter(x -> Character.isLowerCase(x.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), Side.BLACK))
        ), getTurnSide(fen), getEnPassantFileBitboard(fen), getCastlingRights(fen));
    }

    // Превращение, возвращает обновлённый fen. Не изменяет сторону хода (ChessBoard изменяет).
    // Нет проверок на то что спереди перед пешкой нет никого при условии обычного хода. Это всё в логике.
    // Нет проверок на то что атака/ход. Логика вернёт что нужно, главное сюда не подавать лишнего.
    public static String promotePawnToFigureUpdateFen(final String fen, final MoveInfo moveInfo) {
        Objects.requireNonNull(moveInfo.getPromoteTo());
        validatePromotion(moveInfo);
        final String unzippedFen = unzipFen(fen);
        StringBuilder updateFen = new StringBuilder(unzipFen(fen));
        final String parsePiecePlacement = splitFEN(updateFen.toString()).get(0);
        final int lastIndex = BOARD_SIZE * BOARD_SIZE - 1;
        int backwardPrinting = BOARD_SIZE - 1;
        int rowCount = 0;
        int currentIndexWithSkips;
        int charCount = 0;
        for (String rank : parsePiecePlacement.split("/")) {
            for (char ignored : rank.toCharArray()) {
                currentIndexWithSkips = (lastIndex - (rowCount * BOARD_SIZE + backwardPrinting));
                if (currentIndexWithSkips == moveInfo.getCellFrom().getIndexAsOneDimension()) {
                    validatePieceAtIndexIsOnBoard(unzippedFen, moveInfo.getFigure(), charCount);
                    updateFen.replace(charCount, charCount + 1, "1");
                }
                if (currentIndexWithSkips == moveInfo.getCellTo().getIndexAsOneDimension()) {
                    updateFen.replace(charCount, charCount + 1,
                            fileCharToFigureRepresentation.get(moveInfo.getPromoteTo()));
                }
                backwardPrinting--;
                charCount++;
            }
            charCount++;
            backwardPrinting = BOARD_SIZE - 1;
            rowCount++;
        }
        return zipFen(updateFen.toString());
    }

    private static void validatePieceAtIndexIsOnBoard(final String fen,
                                                      final Figure figure,
                                                      final int fenPositionIndex) {
        if (!fen.substring(fenPositionIndex, fenPositionIndex + 1)
                .equals(fileCharToFigureRepresentation.get(figure)))
            throw new IllegalArgumentException("Piece " + figure + " wasn't fount at given position: "
                    + fenPositionIndex + " with notation: " + fen);
    }

    private static void validatePromotion(final MoveInfo moveInfo) {
        if (moveInfo.getPromoteTo() == Figure.B_KING || moveInfo.getPromoteTo() == Figure.B_PAWN ||
                moveInfo.getPromoteTo() == Figure.W_KING || moveInfo.getPromoteTo() == Figure.W_PAWN)
            throw new IllegalArgumentException("Pawn promotion attempt  " + moveInfo.getPromoteTo());
        if (moveInfo.getFigure() != Figure.B_PAWN && moveInfo.getFigure() != Figure.W_PAWN)
            throw new IllegalArgumentException("Promotion attempt of a piece that isn't a pawn.");
        if (moveInfo.getMoveType() != MoveType.PAWN_TO_FIGURE
                && moveInfo.getMoveType() != MoveType.PAWN_TO_FIGURE_ATTACK)
            throw new IllegalArgumentException("Incorrect move type for promotion: " + moveInfo.getMoveType());
        if (moveInfo.getFigure().getWeight() > 0 && moveInfo.getPromoteTo().getWeight() < 0
                || moveInfo.getFigure().getWeight() < 0 && moveInfo.getPromoteTo().getWeight() > 0) // Если превращение в другой цвет
            throw new IllegalArgumentException
                    ("Promotion attempt from: " + moveInfo.getFigure() + " to: " + moveInfo.getPromoteTo());
    }

    // Проверка на право рокировки, то что клетки атакованы, то что клетки не свободны - не делается. Это есть в логике.
    // Обновляет право на рокировку, однако обновлять право после хода короля(не рокировки)/ладьи, надо отдельно.
    // Не обновляет ход
    public static String castlingUpdateFen(final String fen, final MoveInfo moveInfo) {
        final boolean whiteKingSideCastlingRight =
                moveInfo.getCellTo().getIndexAsOneDimension() > moveInfo.getCellFrom().getIndexAsOneDimension()
                        && moveInfo.getFigure().getWeight() > 0;
        final boolean whiteQueenSideCastlingRight =
                moveInfo.getCellTo().getIndexAsOneDimension() < moveInfo.getCellFrom().getIndexAsOneDimension()
                        && moveInfo.getFigure().getWeight() > 0;
        final boolean blackKingSideCastlingRight =
                moveInfo.getCellTo().getIndexAsOneDimension() > moveInfo.getCellFrom().getIndexAsOneDimension()
                        && moveInfo.getFigure().getWeight() < 0;
        final boolean blackQueenSideCastlingRight =
                moveInfo.getCellTo().getIndexAsOneDimension() < moveInfo.getCellFrom().getIndexAsOneDimension()
                        && moveInfo.getFigure().getWeight() < 0;
        validateCastling(fen, moveInfo, whiteKingSideCastlingRight, whiteQueenSideCastlingRight,
                blackKingSideCastlingRight, blackQueenSideCastlingRight);
        final String unzipped = unzipFen(fen);
        final String parsePiecePlacement = splitFEN(unzipped).get(0);
        final String[] splitBoardOnSlash = parsePiecePlacement.split("/");
        final int lastRow = 7;
        final int firstRow = 0;
        final String whiteKingSideCastlingRegex = "\\S+[K]1{2}[R]$";
        final String whiteQueenSideCastlingRegex = "^[R]1{3}[K]\\S+";
        final String blackKingSideCastlingRegex = "\\S+[k]1{2}[r]$";
        final String blackQueenSideCastlingRegex = "^[r]1{3}[k]\\S+";
        List<String> splitBySpace = new ArrayList<>(List.of(unzipped.split(" ")));
        String castlingRights = splitBySpace.get(2);
        splitBySpace.remove(2);
        String chessBoardPosition = splitBySpace.get(0);
        splitBySpace.remove(0);
        if (whiteKingSideCastlingRight && splitBoardOnSlash[lastRow].matches(whiteKingSideCastlingRegex)) {
            splitBySpace.add(0, chessBoardPosition.replace("K11R", "1RK1"));
            splitBySpace.add(2, castlingRights.replace("K", "").replace("Q", ""));
        }
        if (whiteQueenSideCastlingRight && splitBoardOnSlash[lastRow].matches(whiteQueenSideCastlingRegex)) {
            splitBySpace.add(0, chessBoardPosition.replace("R111K", "11KR1"));
            splitBySpace.add(2, castlingRights.replace("K", "").replace("Q", ""));
        }
        if (blackKingSideCastlingRight && splitBoardOnSlash[firstRow].matches(blackKingSideCastlingRegex)) {
            splitBySpace.add(0, chessBoardPosition.replace("k11r", "1rk1"));
            splitBySpace.add(2, castlingRights.replace("k", "").replace("q", ""));
        }
        if (blackQueenSideCastlingRight && splitBoardOnSlash[firstRow].matches(blackQueenSideCastlingRegex)) {
            splitBySpace.add(0, chessBoardPosition.replace("r111k", "11kr1"));
            splitBySpace.add(2, castlingRights.replace("k", "").replace("q", ""));
        }
        if (splitBySpace.size() < 6)
            throw new IllegalArgumentException("Castling is impossible: " + moveInfo + " with fen notation: " + fen);
        return zipFen(Joiner.on(" ").join(splitBySpace));
    }

    private static void validateCastling(final String fen,
                                         final MoveInfo moveInfo,
                                         final boolean whiteKingSideCastlingRight,
                                         final boolean whiteQueenSideCastlingRight,
                                         final boolean blackKingSideCastlingRight,
                                         final boolean blackQueenSideCastlingRight) {
        final int standardKingPosition = 4;
        final int lastRow = 7;
        final int firstRow = 0;
        if (moveInfo.getFigure() != Figure.B_KING && moveInfo.getFigure() != Figure.W_KING)
            throw new IllegalArgumentException("Castling attempt with a piece: " + moveInfo.getFigure());
        if (moveInfo.getCellFrom().getColumn() != standardKingPosition
                && (moveInfo.getCellFrom().getRow() != lastRow || moveInfo.getCellFrom().getRow() != firstRow))
            throw new IllegalArgumentException("King is not at the beginning position.");
        if (moveInfo.getMoveType() != MoveType.CASTLE_LONG
                && moveInfo.getMoveType() != MoveType.CASTLE_SHORT)
            throw new IllegalArgumentException("Incorrect move type for promotion: " + moveInfo.getMoveType());
        final Set<String> castleRights =
                Arrays.stream(getCastlingRights(fen).split("")).collect(Collectors.toSet());
        if (castleRights.contains("-"))
            throw new IllegalArgumentException("Either side doesn't have castling rights: " + moveInfo);
        if (whiteKingSideCastlingRight && !castleRights.contains("K"))
            throw new IllegalArgumentException("No castling right: " + moveInfo + " with given fen: " + fen);
        if (whiteQueenSideCastlingRight && !castleRights.contains("Q")) // Белые, длинная рокировка
            throw new IllegalArgumentException("No castling right: " + moveInfo + " with given fen: " + fen);
        if (blackKingSideCastlingRight && !castleRights.contains("k")) // Чёрные, короткая рокировка
            throw new IllegalArgumentException("No castling right: " + moveInfo + " with given fen: " + fen);
        if (blackQueenSideCastlingRight && !castleRights.contains("q")) // Чёрные, длинная рокировка
            throw new IllegalArgumentException("No castling right: " + moveInfo + " with given fen: " + fen);
    }

    public static String unzipFen(final String fen) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fen.length(); i++) {
            if (String.valueOf(fen.charAt(i)).matches("[1-8]")) {
                sb.append("1".repeat(Character.getNumericValue(fen.charAt(i))));
            } else {
                sb.append(fen.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String zipFen(final String fen) {
        StringBuilder sb = new StringBuilder();
        int emptyCellCounter = 0;

        for (int i = 0; i < fen.length(); i++) {
            if (fen.charAt(i) == '1') {
                emptyCellCounter++;
                continue;
            }
            if (emptyCellCounter > 0) {
                sb.append(emptyCellCounter);
                emptyCellCounter = 0;
            }
            sb.append(fen.charAt(i));
        }
        if (emptyCellCounter > 0) {
            sb.append(emptyCellCounter);
        }
        return sb.toString();
    }

    // Проверка то что 2 строка содержится в 1, без сортировки(в обычном "qk".contains("KQkq") вернет false)
    public static boolean containsUnordered(final String string, final String substring) {
        int index;
        for (char character : substring.toCharArray()) {
            index = string.lastIndexOf(character);
            if (index == -1)
                return false;
        }
        return true;
    }

    public static long getEnPassantFileBitboard(final String fen) {
        final String parseEnPassantTargetSquare = splitFEN(fen).get(3);
        return fileCharToBitboardRepresentation.getOrDefault(parseEnPassantTargetSquare.charAt(0), 0L);
    }

    public static String getCastlingRights(final String fen) {
        return splitFEN(fen).get(2);
    }

    public static String getPiecePlacement(final String fen) {
        return splitFEN(fen).get(0);
    }

    /**
     * Возвращает 3 части нотации в максимально сжатом формате. Используется как ключ.
     *
     * @param fen
     * @return
     */
    public static String getNotationsFirstFourParts(final String fen) {
        final List<String> fenParts = splitFEN(fen);
        return fenParts.get(0).replace("/", "") + fenParts.get(1) + fenParts.get(2) + fenParts.get(3);
    }

    public static Side getTurnSide(final String fen) {
        final String parseTurnSide = splitFEN(fen).get(1);
        final char turnSide = parseTurnSide.charAt(0);
        if (!charToSideRepresentation.containsKey(turnSide))
            throw new IllegalArgumentException
                    ("Turn side is not 'w' or 'b', but a: " + turnSide);
        return charToSideRepresentation.get(turnSide);
    }

    private static List<String> splitFEN(final String fen) {
        final List<String> parseFenNotation = List.of(fen.split(" "));
        if (parseFenNotation.size() != 6)
            throw new IllegalArgumentException("Incorrect, or incomplete notation, amount of elements in notation isn't equal to 6: " +
                    fen);
        return parseFenNotation;
    }

}
