package io.deeplay.core.statistics;

import io.deeplay.core.model.*;

import java.util.*;

/**
 * Класс для сбора статистики одной партии.
 */
public class OneGameStatistics {

    // "развитие фигур", "как много фигур двинулись как минимум 1 раз?" - посчитать количество движений фигурами
    // Нужна мапа из которой будут удаляться походившие фигуры
    private final Map<Figure, Integer> countPieceMoves;

    private final List<Float> firstPlayerMovesTime;
    private final List<Float> secondPlayerMovesTime;
    private final Map<MoveType, Integer> countFirstPlayerUsedMoves;
    private final Map<MoveType, Integer> countSecondPlayerUsedMoves;
    private final List<Figure> firstPlayerUsedFiguresSequence;
    private final List<Figure> secondPlayerUsedFiguresSequence;

    // общее
    private final List<Figure> piecesLeftAfterGameEnd;
    private GameStatus gameStatus;
    private int countHalfTurns;
    private int countTurns;

    public OneGameStatistics() {
        this.countPieceMoves = new EnumMap<>(Figure.class);
        this.firstPlayerMovesTime = new ArrayList<>();
        this.secondPlayerMovesTime = new ArrayList<>();
        this.countFirstPlayerUsedMoves = new HashMap<>();
        this.countSecondPlayerUsedMoves = new HashMap<>();
        this.firstPlayerUsedFiguresSequence = new ArrayList<>();
        this.secondPlayerUsedFiguresSequence = new ArrayList<>();
        this.piecesLeftAfterGameEnd = new ArrayList<>();
        this.gameStatus = GameStatus.ACTIVE;
        this.countHalfTurns = 0;
        this.countTurns = 0;
    }

    public void updateStatisticsAfterFirstPlayerMove(final MoveInfo moveInfo, final float timeMark) {
        addFirstPlayerUsedFiguresSequence(moveInfo.getFigure());
        countFirstPlayerUsedMoves(moveInfo.getMoveType());
        addFirstPlayerMovesTime(timeMark);

        countPieceMoves(moveInfo.getFigure());
        countHalfTurn();
    }

    public void updateStatisticsAfterSecondPlayerMove(final MoveInfo moveInfo, final float timeMark) {
        addSecondPlayerUsedFiguresSequence(moveInfo.getFigure());
        countSecondPlayerUsedMoves(moveInfo.getMoveType());
        addSecondPlayerMovesTime(timeMark);

        countPieceMoves(moveInfo.getFigure());
        countHalfTurn();
    }

    public void addGameEndStatistics(final GameInfo gameInfo) {
        addPiecesLeftAfterGameEnd(gameInfo);
        setGameStatus(gameInfo.getGameStatus());
    }

    public void addPiecesLeftAfterGameEnd(final GameInfo gameInfo) {
        for (BoardCell[] boardCellRow : gameInfo.getBoard().getBoard())
            for (BoardCell boardCellColumn : boardCellRow)
                if (boardCellColumn.getFigure() != Figure.NONE)
                    piecesLeftAfterGameEnd.add(boardCellColumn.getFigure());
    }

    public void addFirstPlayerUsedFiguresSequence(final Figure figure) {
        firstPlayerUsedFiguresSequence.add(figure);
    }

    public void addSecondPlayerUsedFiguresSequence(final Figure figure) {
        secondPlayerUsedFiguresSequence.add(figure);
    }

    public void countFirstPlayerUsedMoves(final MoveType moveType) {
        countFirstPlayerUsedMoves.putIfAbsent(moveType, 0);
        countFirstPlayerUsedMoves.put(moveType, countFirstPlayerUsedMoves.get(moveType) + 1);
    }

    public void countSecondPlayerUsedMoves(final MoveType moveType) {
        countSecondPlayerUsedMoves.putIfAbsent(moveType, 0);
        countSecondPlayerUsedMoves.put(moveType, countSecondPlayerUsedMoves.get(moveType) + 1);
    }

    public void addFirstPlayerMovesTime(final float timeMark) {
        firstPlayerMovesTime.add(timeMark);
    }

    public void addSecondPlayerMovesTime(final float timeMark) {
        secondPlayerMovesTime.add(timeMark);
    }

    public void countPieceMoves(final Figure figure) {
        countPieceMoves.putIfAbsent(figure, 0);
        countPieceMoves.put(figure, countPieceMoves.get(figure) + 1);
    }

    public void countHalfTurn() {
        countHalfTurns++;
        if (countHalfTurns % 2 == 0)
            countTurn();
    }

    private void countTurn() {
        countTurns++;
    }

    public Map<Figure, Integer> getCountPieceMoves() {
        return countPieceMoves;
    }

    public List<Float> getFirstPlayerMovesTime() {
        return firstPlayerMovesTime;
    }

    public List<Float> getSecondPlayerMovesTime() {
        return secondPlayerMovesTime;
    }

    public Map<MoveType, Integer> getCountFirstPlayerUsedMoves() {
        return countFirstPlayerUsedMoves;
    }

    public Map<MoveType, Integer> getCountSecondPlayerUsedMoves() {
        return countSecondPlayerUsedMoves;
    }

    public List<Figure> getFirstPlayerUsedFiguresSequence() {
        return firstPlayerUsedFiguresSequence;
    }

    public List<Figure> getSecondPlayerUsedFiguresSequence() {
        return secondPlayerUsedFiguresSequence;
    }

    public List<Figure> getPiecesLeftAfterGameEnd() {
        return piecesLeftAfterGameEnd;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(final GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getCountHalfTurns() {
        return countHalfTurns;
    }

    public int getCountTurns() {
        return countTurns;
    }
}
