package io.deeplay.core.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChessBoard {
    public static final int BOARD_SIZE = 8;
    static final int NUMBER_OF_SLASHES_IN_FEN_STRING = 7;
    public static final String DEFAULT_FEN_STRING = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private String fen;
    private final BoardCell[][] board;
    Logger logger = LoggerFactory.getLogger(ChessBoard.class);
    private ChessBoard previousChessBoard;
    private MoveInfo moveInfo = null;
    private String castleAvailable;
    private int movesWithoutAttackOrPawnMove;
    private int moveCounter;
    private String pawnLongMoveInfo;
    private Side whoseMove = Side.WHITE;


    /**
     * Default constructor to create default board;
     */
    public ChessBoard() {
        this(DEFAULT_FEN_STRING);
    }

    /**
     * Constructor to create custom start board from FEN string
     *
     * @param fen FEN string.
     */
    public ChessBoard(final String fen) {
        if (fen.length() - fen.replace("/", "").length() != NUMBER_OF_SLASHES_IN_FEN_STRING) {
            throw new RuntimeException("Wrong FEN string");
        }
        this.fen = fen;
        previousChessBoard = null;
        board = new BoardCell[BOARD_SIZE][BOARD_SIZE];
        String[] splitFiguresFromProperties = fen.split(" ", 2);
        String unzippedFenWithoutProperties = unzipFen(splitFiguresFromProperties[0]).replace("/", "");
        for (int i = 0; i < unzippedFenWithoutProperties.length(); i++) {
            board[BOARD_SIZE - 1 - i / BOARD_SIZE][i % BOARD_SIZE] = new BoardCell(
                    MapsStorage.SYMBOLS_TO_FIGURE.get(String.valueOf(unzippedFenWithoutProperties.charAt(i))));
        }
        String properties = splitFiguresFromProperties[1];
        setProperties(properties);
    }

    public ChessBoard(final ChessBoard chessBoard) {
        this.fen = chessBoard.fen;
        BoardCell[][] newBoard = new BoardCell[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                newBoard[i][j] = new BoardCell(chessBoard.board[i][j].getFigure());
            }
        }
        this.board = newBoard;
        this.previousChessBoard = chessBoard.previousChessBoard;
        this.moveInfo = chessBoard.moveInfo;
        this.castleAvailable = chessBoard.castleAvailable;
        this.movesWithoutAttackOrPawnMove = chessBoard.movesWithoutAttackOrPawnMove;
        this.moveCounter = chessBoard.moveCounter;
        this.pawnLongMoveInfo = chessBoard.pawnLongMoveInfo;
        this.whoseMove = chessBoard.whoseMove;
    }

    /**
     * Устанавливает параметры из строки fen переменным.
     *
     * @param properties параметры из строки ФЕН
     */
    private void setProperties(final String properties) {
        String[] param = properties.split(" ");
        whoseMove = "w".equals(param[0]) ? Side.WHITE : Side.BLACK;
        castleAvailable = param[1];
        pawnLongMoveInfo = param[2];
        movesWithoutAttackOrPawnMove = Integer.parseInt(param[3]);
        moveCounter = Integer.parseInt(param[4]);
    }

    // Получает из переменных параметры строки fen.
    private String getProperties() {
        return " " +
                whoseMove + " " +
                castleAvailable + " " +
                pawnLongMoveInfo + " " +
                movesWithoutAttackOrPawnMove + " " +
                moveCounter;
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

    public String zipFen(final String fen) {
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

    public void updateBoard(final MoveInfo moveInfo) {
        previousChessBoard = this.copy();
        this.moveInfo = moveInfo;
        pawnLongMoveInfo = "-";

        Coord from = moveInfo.getCellFrom();
        Coord to = moveInfo.getCellTo();

        switch (moveInfo.getMoveType()) {
            case USUAL_MOVE:
                processUsualMove(moveInfo, to);
                break;
            case USUAL_ATTACK:
                processUsualAttack(moveInfo, from, to);
                break;
            case PAWN_ATTACK:
                processPawnAttack(moveInfo, from, to);
                break;
            case PAWN_LONG_MOVE:
                processPawnLongMove(moveInfo, from, to);
                break;
            case PAWN_ON_GO_ATTACK:
                processPawnOnGoAttack(moveInfo, to);
                break;
            case PAWN_TO_FIGURE_ATTACK:
            case PAWN_TO_FIGURE:
                processPawnToFigure(moveInfo, to);
                break;
            case CASTLE_LONG:
                processCastleLong(moveInfo, from, to);
                break;
            case CASTLE_SHORT:
                processCastleShort(moveInfo, from, to);
        }
        updateMoveCounters(moveInfo);
        updateCastle(moveInfo);
        whoseMove = Side.otherSide(whoseMove);
        board[from.getRow()][from.getColumn()] = new BoardCell(Figure.NONE);
        updateFen();
    }

    private void processUsualMove(final MoveInfo moveInfo, final Coord to) {
        if (board[to.getRow()][to.getColumn()].getFigure() == Figure.NONE) {
            board[to.getRow()][to.getColumn()].setFigure(moveInfo.getFigure());
        } else {
            throw new RuntimeException("Usual move tries to get occupied cell");
        }
    }

    private void processUsualAttack(final MoveInfo moveInfo, final Coord from, final Coord to) {
        if (MapsStorage.WHITE_FIGURES.contains(board[from.getRow()][from.getColumn()].getFigure()) &&
                MapsStorage.BLACK_FIGURES.contains(board[to.getRow()][to.getColumn()].getFigure()) ||
                MapsStorage.BLACK_FIGURES.contains(board[from.getRow()][from.getColumn()].getFigure()) &&
                        MapsStorage.WHITE_FIGURES.contains(board[to.getRow()][to.getColumn()].getFigure())) {
            board[to.getRow()][to.getColumn()].setFigure(moveInfo.getFigure());
        } else {
            throw new RuntimeException("Attempt to attack empty cell or own figure");
        }
    }

    private void processPawnAttack(final MoveInfo moveInfo, final Coord from, final Coord to) {
        if (Math.abs(from.getColumn() - to.getColumn()) == 1 &&
                Math.abs(from.getRow() - to.getRow()) == 1) {
            board[to.getRow()][to.getColumn()].setFigure(moveInfo.getFigure());
        } else {
            throw new RuntimeException("Illegal pawn attack");
        }
    }

    private void processPawnLongMove(final MoveInfo moveInfo, final Coord from, final Coord to) {
        if (from.getColumn() - to.getColumn() == 0 &&
                Math.abs(from.getRow() - to.getRow()) == 2 &&
                board[to.getRow()][to.getColumn()].getFigure() == Figure.NONE) {
            board[to.getRow()][to.getColumn()].setFigure(moveInfo.getFigure());
            pawnLongMoveInfo = MapsStorage.NUMBERS_TO_LETTERS.get(moveInfo.getCellFrom().getColumn()) +
                    (whoseMove == Side.WHITE ? 3 : 6);
        } else {
            throw new RuntimeException("Illegal long pawn move");
        }
    }

    private void processPawnOnGoAttack(final MoveInfo moveInfo, final Coord to) {
        if (previousChessBoard.moveInfo.getMoveType() == MoveType.PAWN_LONG_MOVE &&
                previousChessBoard.moveInfo.getCellTo().getColumn() - to.getColumn() == 0) {
            board[to.getRow()][to.getColumn()].setFigure(moveInfo.getFigure());
            if (MapsStorage.BLACK_FIGURES.contains(moveInfo.getFigure())) {
                board[3][to.getColumn()] = new BoardCell(Figure.NONE);
            } else {
                board[4][to.getColumn()] = new BoardCell(Figure.NONE);
            }
        } else {
            throw new RuntimeException("Illegal pawn on go attack");
        }
    }

    private void processPawnToFigure(final MoveInfo moveInfo, final Coord to) {
        if (moveInfo.getPromoteTo() == null) {
            board[to.getRow()][to.getColumn()].setFigure(whoseMove == Side.WHITE ? Figure.W_QUEEN : Figure.B_QUEEN);
        } else {
            board[to.getRow()][to.getColumn()].setFigure(moveInfo.getPromoteTo());
        }
    }

    private void processCastleShort(final MoveInfo moveInfo, final Coord from, final Coord to) {
        if ((moveInfo.getFigure() == Figure.B_KING ||
                moveInfo.getFigure() == Figure.W_KING) &&
                to.getColumn() - from.getColumn() == 2) {
            board[to.getRow()][to.getColumn()].setFigure(moveInfo.getFigure());
            // Теперь перемещаем ладью
            board[to.getRow()][5].setFigure(board[to.getRow()][7].getFigure());
            board[to.getRow()][7].setFigure(Figure.NONE);
        } else {
            throw new RuntimeException("Wrong short castling");
        }
    }

    private void processCastleLong(final MoveInfo moveInfo, final Coord from, final Coord to) {
        if ((moveInfo.getFigure() == Figure.B_KING ||
                moveInfo.getFigure() == Figure.W_KING) &&
                from.getColumn() - to.getColumn() == 2) {
            board[to.getRow()][to.getColumn()].setFigure(moveInfo.getFigure());
            // Теперь перемещаем ладью
            board[to.getRow()][3].setFigure(board[to.getRow()][0].getFigure());
            board[to.getRow()][0].setFigure(Figure.NONE);
        } else {
            throw new RuntimeException("Wrong long castling");
        }
    }

    private void updateCastle(final MoveInfo moveInfo) {
        if (moveInfo.getFigure() == Figure.B_KING) {
            castleAvailable = castleAvailable.replace("k", "");
            castleAvailable = castleAvailable.replace("q", "");
        } else if (moveInfo.getFigure() == Figure.W_KING) {
            castleAvailable = castleAvailable.replace("K", "");
            castleAvailable = castleAvailable.replace("Q", "");
        }

        if (board[0][0].getFigure() != Figure.W_ROOK) {
            castleAvailable = castleAvailable.replace("Q", "");
        }

        if (board[0][7].getFigure() != Figure.W_ROOK) {
            castleAvailable = castleAvailable.replace("K", "");
        }

        if (board[7][0].getFigure() != Figure.B_ROOK) {
            castleAvailable = castleAvailable.replace("q", "");
        }

        if (board[7][7].getFigure() != Figure.B_ROOK) {
            castleAvailable = castleAvailable.replace("k", "");
        }

        if (castleAvailable.length() == 0) {
            castleAvailable = "-";
        }
    }

    private void updateMoveCounters(final MoveInfo moveInfo) {
        if (moveInfo.getFigure() != Figure.B_PAWN && moveInfo.getFigure() != Figure.W_PAWN &&
                moveInfo.getMoveType() != MoveType.USUAL_ATTACK) {
            movesWithoutAttackOrPawnMove++;
        } else {
            movesWithoutAttackOrPawnMove = 0;
        }
        moveCounter += whoseMove == Side.WHITE ? 0 : 1;
    }

    public String getFEN() {
        return fen;
    }

    public void updateFen() {
        String str = this.toString();
        str = str.replace('\n', '/');
        str = str.substring(0, str.length() - 1);
        str = str.replace('-', '1');
        str = str.replace(" ", "");
        str = zipFen(str);
        str += getProperties();
        fen = str;
    }

    public BoardCell[][] getBoard() {
        return board;
    }

    public Side getWhoseMove() {
        return whoseMove;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        StringBuilder row = new StringBuilder();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                row.append(MapsStorage.FIGURE_TO_SYMBOL.get(board[i][j].getFigure())).append(" ");
            }
            row.append("\n");
            result.insert(0, row);
            row.delete(0, row.length());
        }
        return result.toString();
    }


    public boolean isThreefoldRepetition() {
        try {
            return this.toString().equals(previousChessBoard.previousChessBoard.previousChessBoard.previousChessBoard.toString()) &&
                    previousChessBoard.toString().equals
                            (previousChessBoard.previousChessBoard.previousChessBoard.previousChessBoard.previousChessBoard.toString()) &&
                    previousChessBoard.previousChessBoard.toString().equals
                            (previousChessBoard.previousChessBoard.previousChessBoard.previousChessBoard.previousChessBoard.previousChessBoard.toString());

        } catch (Exception e) {
            return false;
        }

    }

    public int getMovesWithoutAttackOrPawnMove() {
        return movesWithoutAttackOrPawnMove;
    }

    /**
     * @return clone
     */
    protected ChessBoard copy() {
        return new ChessBoard(this);
    }
}
