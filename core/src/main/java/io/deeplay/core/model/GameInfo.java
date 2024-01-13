package io.deeplay.core.model;

import io.deeplay.core.api.SimpleLogic;
import io.deeplay.core.api.SimpleLogicAppeal;
import io.deeplay.core.api.SimpleLogicCache;
import io.deeplay.core.listener.ChessAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameInfo extends ChessAdapter {
    private GameStatus gameStatus;
    private static final int BOARD_SIZE = 8;
    ChessBoard board;
    SimpleLogicAppeal logic;
    boolean whiteIsPresent;
    boolean blackIsPresent;
    final boolean clearCacheAfterGame;

    /**
     * Стандартный конструктор
     */
    public GameInfo() {
        this(ChessBoard.DEFAULT_FEN_STRING);
    }

    /**
     * Конструктор для заданного расположения фигур.
     *
     * @param fen стартовая позиция
     */
    public GameInfo(final String fen) {
        this(fen, false, false);
    }

    /**
     * Конструктор для заданного расположения фигур с возможностью отключить кэширование вычислений логики.
     *
     * @param fen                 стартовая позиция
     * @param cacheLogic          флаг установки кэширования вычислений логики, кэширование будет влиять на скорость работы ботов.
     * @param clearCacheAfterGame флаг обновления кэширования после каждой игры.
     */
    public GameInfo(final String fen, final boolean cacheLogic, final boolean clearCacheAfterGame) {
        gameStatus = GameStatus.INACTIVE;
        if (cacheLogic) {
            logic = new SimpleLogicCache();
            this.clearCacheAfterGame = clearCacheAfterGame;
        } else {
            logic = new SimpleLogic();
            if (clearCacheAfterGame)
                throw new IllegalArgumentException("Cant clear cache of non-cacheable logic");
            this.clearCacheAfterGame = false;
        }
        board = new ChessBoard(fen);
        whiteIsPresent = false;
        blackIsPresent = false;
    }

    public GameInfo(final GameInfo gameInfo) {
        this.gameStatus = gameInfo.gameStatus;
        this.board = gameInfo.board.copy();
        this.logic = gameInfo.logic;
        this.whiteIsPresent = gameInfo.whiteIsPresent;
        this.blackIsPresent = gameInfo.blackIsPresent;
        this.clearCacheAfterGame = gameInfo.clearCacheAfterGame;
    }

    public void resetGame() {
        gameStatus = GameStatus.INACTIVE;
        board = new ChessBoard(ChessBoard.DEFAULT_FEN_STRING);
        if (clearCacheAfterGame)
            logic = new SimpleLogicCache();
    }

    @Override
    public void gameStarted() {
        if (blackIsPresent && whiteIsPresent) {
            gameStatus = GameStatus.ACTIVE;
        } else {
            throw new RuntimeException("Not enough players to play.");
        }
    }

    public BoardCell[][] getChessBoard() {
        return board.getBoard();
    }

    public ChessBoard getBoard() {
        return board;
    }

    public boolean isMate() {
        return logic.isMate(board.getFEN());
    }

    public boolean isStalemate() {
        return logic.isStalemate(board.getFEN());
    }

    public boolean isDrawByPieceShortage() {
        return logic.isDrawByPieceShortage(board.getFEN());
    }

    public boolean isThreefoldRepetition() {
        return board.isThreefoldRepetition();
    }

    public boolean isMovesWithoutAttackOrPawnMove() {
        return board.getMovesWithoutAttackOrPawnMove() > 99;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(final GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getFenBoard() {
        return board.getFEN();
    }

    /**
     * Обновляет доску, после обновления проверяет достаточно ли материала для мата и было ли трехкратное повторение.
     *
     * @param moveInfo Информация о совершенном ходе
     */
    public void updateBoard(final MoveInfo moveInfo) {
        board.updateBoard(moveInfo);
        checkMatingMaterial();
        if (logic.isMate(board.getFEN())) {
            gameStatus = whoseMove() == Side.WHITE ? GameStatus.BLACK_WON : GameStatus.WHITE_WON;
        }
        if (logic.isStalemate(board.getFEN())) {
            gameStatus = GameStatus.STALEMATE;
        }
        if (board.isThreefoldRepetition()) {
            gameStatus = GameStatus.THREEFOLD_REPETITION;
        }
        // Проверка на правило 50 ходов.
        if (board.getMovesWithoutAttackOrPawnMove() > 99) {
            gameStatus = GameStatus.FIFTY_MOVES_RULE;
        }
    }

    /**
     * Проверяет достаточно ли материала для мата. Если нет, меняет статус игры.
     */
    private void checkMatingMaterial() {
        if (logic.isDrawByPieceShortage(board.getFEN())) {
            gameStatus = GameStatus.INSUFFICIENT_MATING_MATERIAL;
        }
    }

    /**
     * Возвращает сторону, которая должна сделать ход.
     *
     * @return Сторону, которая должна сделать следующий ход
     */
    public Side whoseMove() {
        return board.getWhoseMove();
    }

    public boolean isGameOver() {
        return gameStatus == GameStatus.ACTIVE;
    }

    /**
     * Возвращает список всех возможных ходов для конкретной стороны side.
     *
     * @return возможные ходы
     */
    public Set<MoveInfo> getAvailableMoves() {
        return logic.getMoves(board.getFEN());
    }

    /**
     * Возвращает список оставшихся фигур на доске.
     *
     * @return список оставшихся фигур на доске.
     */
    public List<Figure> getAllFigures() {
        List<Figure> allFigures = new ArrayList<>();
        BoardCell[][] boardArray = board.getBoard();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (MapsStorage.BLACK_FIGURES.contains(boardArray[i][j].getFigure()) ||
                        MapsStorage.WHITE_FIGURES.contains(boardArray[i][j].getFigure())) {
                    allFigures.add(boardArray[i][j].getFigure());
                }
            }
        }
        return allFigures;
    }

    /**
     * @param side За какую сторону сел игрок
     */
    @Override
    public void playerSeated(final Side side) {
        if (side == Side.WHITE) {
            if (whiteIsPresent) {
                throw new RuntimeException("White is already present!");
            } else {
                whiteIsPresent = true;
            }
        } else if (side == Side.BLACK) {
            if (blackIsPresent) {
                throw new RuntimeException("Black is already present!");
            } else {
                blackIsPresent = true;
            }
        } else {
            throw new RuntimeException("There is no side like that");
        }
    }

    /**
     * Совершенный ход
     *
     * @param side     ходившая сторона
     * @param moveInfo совершенный ход
     */
    @Override
    public void playerActed(final Side side, final MoveInfo moveInfo) {
        updateBoard(moveInfo);
    }

    @Override
    public void draw() {
        gameStatus = GameStatus.DRAW;
    }

    /**
     * @param side победившая сторона
     */
    @Override
    public void playerWon(final Side side) {
        switch (side) {
            case BLACK:
                gameStatus = GameStatus.BLACK_WON;
                break;
            case WHITE:
                gameStatus = GameStatus.WHITE_WON;
                break;
        }
    }

    public GameInfo copy() {
        return new GameInfo(this);
    }

    public GameInfo copy(final MoveInfo moveInfo) {
        final GameInfo newGameInfo = copy();
        newGameInfo.updateBoard(moveInfo);
        return newGameInfo;
    }
}
