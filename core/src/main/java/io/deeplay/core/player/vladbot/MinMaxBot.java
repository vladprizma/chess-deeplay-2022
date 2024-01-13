package io.deeplay.core.player.vladbot;

import io.deeplay.core.evaluation.vladevaluations.Evaluation;
import io.deeplay.core.evaluation.vladevaluations.PeSTO;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;

import java.util.ArrayList;
import java.util.List;


public class MinMaxBot extends VBot {
    private final String PLAYER_NAME = this.getClass().getSimpleName();

    public MinMaxBot(final Side side, final Evaluation evaluation, final int maxDepth) {
        super(side, evaluation, maxDepth);
    }

    public MinMaxBot(final Side side, final Evaluation evaluation) {
        this(side, evaluation, 5);
    }

    public MinMaxBot(final Side side) {
        this(side, new PeSTO());
    }

    public MinMaxBot(final Side side, final int maxDepth) {
        this(side, new PeSTO(), maxDepth);
    }

    @Override
    public MoveInfo getAnswer(final GameInfo gameInfo) {
        return evaluateBestMove(gameInfo);
    }

    @Override
    public String getName() {
        return PLAYER_NAME + "Depth=" + getMaxDepth() + "Evaluation=" + getEvaluation().getName();
    }

    private MoveInfo evaluateBestMove(final GameInfo gameInfo) {
        final List<EvaluatedMove> evaluatedMoves = new ArrayList<>();
        for (final MoveInfo move : gameInfo.getAvailableMoves()) {
            final GameInfo virtualGameInfo = gameInfo.copy(move);
            evaluatedMoves
                    .add(new EvaluatedMove(move, minBot(virtualGameInfo, getMaxDepth() - 1)));
        }
        return getGreedyDecision(evaluatedMoves); // max
    }

    int maxBot(final GameInfo gameInfo,
               final int depthLeft) {
        if (depthLeft == 0) return evaluate(gameInfo, depthLeft);
        int maxScore = Integer.MIN_VALUE;
        for (final MoveInfo move : gameInfo.getAvailableMoves()) {
            final GameInfo virtualGameInfo = gameInfo.copy(move);
            final int currentScore = minBot(virtualGameInfo, depthLeft - 1);
            if (currentScore > maxScore) {
                maxScore = currentScore;
            }
        }
        return maxScore;
    }

    int minBot(final GameInfo gameInfo,
               final int depthLeft) {
        if (depthLeft == 0) return evaluate(gameInfo, depthLeft);
        int minScore = Integer.MAX_VALUE;
        for (final MoveInfo move : gameInfo.getAvailableMoves()) {
            final GameInfo virtualGameInfo = gameInfo.copy(move);
            final int currentScore = maxBot(virtualGameInfo, depthLeft - 1);
            if (currentScore < minScore) {
                minScore = currentScore;
            }
        }
        return minScore;
    }

}
