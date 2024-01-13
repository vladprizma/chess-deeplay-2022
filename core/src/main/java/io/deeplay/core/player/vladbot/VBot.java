package io.deeplay.core.player.vladbot;

import io.deeplay.core.evaluation.vladevaluations.Evaluation;
import io.deeplay.core.evaluation.vladevaluations.PeSTO;
import io.deeplay.core.model.*;
import io.deeplay.core.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class VBot extends Player {
    private static final Logger LOGGER = LoggerFactory.getLogger(VBot.class);

    private final Evaluation evaluation;
    private final int maxDepth;

    public VBot(final Side side) {
        this(side, new PeSTO(), 1);
    }

    public VBot(final Side side, final Evaluation evaluation, final int maxDepth) {
        super(side);
        this.evaluation = evaluation;
        if (maxDepth < 1)
            throw new IllegalArgumentException("Tree search based bot cannot look on depth < 1,  maxDepth:" + maxDepth);
        this.maxDepth = maxDepth;
        LOGGER.info("Для {} установлена оценочная функция - {}, с глубиной - {}", this, evaluation, maxDepth);
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    public abstract MoveInfo getAnswer(GameInfo gameInfo);

    public static List<MoveInfo> sortWithMVVLVA(final GameInfo gameInfo) {
        final List<MoveInfo> moves = new ArrayList<>(gameInfo.getAvailableMoves());
        final List<MoveInfo> sortedMoves = filterAndSortAttackMovesWithMVVLVA(moves, gameInfo);
        moves.removeAll(sortedMoves);
        sortedMoves.addAll(moves); // добавляем остальные виды ходов в конец списка
        return sortedMoves;
    }

    /**
     * @param moves    список ходов
     * @param gameInfo
     * @return оставляет в списке только атаки, сортирует и возвращает их
     */
    private static List<MoveInfo> filterAndSortAttackMovesWithMVVLVA(final Collection<MoveInfo> moves,
                                                                     final GameInfo gameInfo) {
        final Comparator<Figure> figureComparator = new VBot.SortByMVVLVA();
        return moves.stream()
                .filter(move -> MapsStorage.ATTACKS.contains(move.getMoveType()))
                .sorted(Comparator.comparingInt(move -> figureComparator
                        .compare(move.getFigure(),
                                gameInfo.getChessBoard()[move.getCellTo().getRow()][move.getCellTo().getColumn()].getFigure())))
                .collect(Collectors.toList());
    }

    @Override
    public abstract String getName();

    protected MoveInfo getGreedyDecision(final Collection<EvaluatedMove> evaluatedMoves) {
        return evaluatedMoves.stream().max(Comparator.comparingInt(e -> e.score)).orElseThrow().moveInfo;
    }

    protected boolean isMateByBot(final GameStatus gameStatus) {
        return gameStatus ==
                (getSide() == Side.WHITE ? GameStatus.WHITE_WON : GameStatus.BLACK_WON);
    }

    protected boolean isMateByOpponent(final GameStatus gameStatus) {
        return gameStatus ==
                (getSide() == Side.WHITE ? GameStatus.BLACK_WON : GameStatus.WHITE_WON);
    }

    public int evaluate(final GameInfo gameInfo, final int depthLeft) {
        final int sideCoeff = getSide() == Side.WHITE ? 1 : -1;
        if (MapsStorage.END_GAME_BY_RULES.contains(gameInfo.getGameStatus())) {
            return sideCoeff * evaluateByEndGameStatus(gameInfo.getGameStatus(), depthLeft);
        }
        return sideCoeff * getEvaluation().evaluateBoard(gameInfo.getBoard());
    }

    protected int evaluateByEndGameStatus(final GameStatus gameStatus, final int depthLeft) {
        if (gameStatus == GameStatus.WHITE_WON) {
            return Integer.MAX_VALUE - (getMaxDepth() - depthLeft); // отнимаем оставшуюся глубину, чтобы кратчайшие маты ценились больше
        }
        if (gameStatus == GameStatus.BLACK_WON) {
            return Integer.MIN_VALUE + 1 + (getMaxDepth() - depthLeft); // + 1 - чтобы при отрицании не было переполнения типа
        }
        if (gameStatus == GameStatus.STALEMATE
                || gameStatus == GameStatus.INSUFFICIENT_MATING_MATERIAL
                || gameStatus == GameStatus.THREEFOLD_REPETITION
                || gameStatus == GameStatus.FIFTY_MOVES_RULE) {
            return 0;
        }
        throw new IllegalArgumentException("Impossible end game reason by rules: " + gameStatus);
    }

    /**
     * Позволяет избежать Horizon Effect.
     *
     * @param gameInfo
     * @param alpha
     * @param beta
     * @return оценка хода
     */
    protected int quiesce(final GameInfo gameInfo, int alpha, final int beta, final int depthLeft) {
        int score = evaluate(gameInfo, depthLeft);
        if (score >= beta)
            return beta;
        if (score > alpha)
            alpha = score;

        final List<MoveInfo> attackMoves = filterAndSortAttackMovesWithMVVLVA(gameInfo.getAvailableMoves(), gameInfo);
        for (MoveInfo attackMove : attackMoves) {
            final GameInfo virtualGameInfo = gameInfo.copy(attackMove);
            score = -quiesce(virtualGameInfo, -beta, -alpha, depthLeft);
            if (score >= beta)
                return beta;
            if (score > alpha)
                alpha = score;
        }
        return alpha;
    }

    protected static final class EvaluatedMove {
        final MoveInfo moveInfo;
        final int score;

        public EvaluatedMove(final MoveInfo moveInfo, final int score) {
            this.moveInfo = moveInfo;
            this.score = score;
        }

        public MoveInfo getMoveInfo() {
            return moveInfo;
        }

        public int getScore() {
            return score;
        }
    }

    /**
     * Использовать только когда в MoveInfo атакующий ход.
     * Ставит все АТАКУЮЩИЕ ходы в приоритет, тихие ходы можно просто дополнить в конец списка.
     * Comparator для сортировки важности ходов - MVVLVA ("most valuable victim - least valuable attacker" -
     * самая ценная жертва - наименее ценный нападающий).
     * Например, ход в котором пешка срубает ферзя противника должен цениться больше,
     * т.к. потерять пешку в размен будет не так страшно.
     */
    private final static class SortByMVVLVA implements Comparator<Figure> {
        @Override
        public int compare(Figure figure1, Figure figure2) {
            return -(score(figure2) - score(figure1));
        }

        private int score(Figure figure) {
            return Math.abs(figure.getWeight());
        }
    }

}
