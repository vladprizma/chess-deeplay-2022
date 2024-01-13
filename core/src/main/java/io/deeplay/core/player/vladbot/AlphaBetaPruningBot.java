package io.deeplay.core.player.vladbot;

import io.deeplay.core.evaluation.vladevaluations.Evaluation;
import io.deeplay.core.evaluation.vladevaluations.PeSTO;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;

import java.util.ArrayList;
import java.util.List;

// with MVVLVA move ordering
public class AlphaBetaPruningBot extends VBot {
    private final String PLAYER_NAME = this.getClass().getSimpleName();

    public AlphaBetaPruningBot(final Side side, final Evaluation evaluation, final int maxDepth) {
        super(side, evaluation, maxDepth);
    }

    public AlphaBetaPruningBot(final Side side, final Evaluation evaluation) {
        this(side, evaluation, 5);
    }

    public AlphaBetaPruningBot(final Side side) {
        this(side, new PeSTO());
    }

    public AlphaBetaPruningBot(final Side side, final int maxDepth) {
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
        final int alpha = Integer.MIN_VALUE;
        final int beta = Integer.MAX_VALUE;
        final List<EvaluatedMove> evaluatedMoves = new ArrayList<>();
        for (final MoveInfo move : sortWithMVVLVA(gameInfo)) {
            final GameInfo virtualGameInfo = gameInfo.copy(move);
            evaluatedMoves
                    .add(new EvaluatedMove(move, alphaBetaMin(virtualGameInfo, alpha, beta, getMaxDepth() - 1)));
        }
        return getGreedyDecision(evaluatedMoves);
    }

    protected final int alphaBetaMax(final GameInfo gameInfo,
                                     int alpha,
                                     final int beta,
                                     final int depthLeft) {
        if (depthLeft == 0) return evaluate(gameInfo, depthLeft);
        for (final MoveInfo move : sortWithMVVLVA(gameInfo)) {
            final int currentScore = alphaBetaMin(gameInfo.copy(move), alpha, beta, depthLeft - 1);
            if (currentScore >= beta)
                return beta;   // fail hard beta-cutoff
            if (currentScore > alpha)
                alpha = currentScore; // alpha acts like max in MiniMax
        }
        return alpha;
    }

    protected final int alphaBetaMin(final GameInfo gameInfo,
                                     final int alpha,
                                     int beta,
                                     final int depthLeft) {
        if (depthLeft == 0) return evaluate(gameInfo, depthLeft);
        for (final MoveInfo move : sortWithMVVLVA(gameInfo)) {
            final int currentScore = alphaBetaMax(gameInfo.copy(move), alpha, beta, depthLeft - 1);
            if (currentScore <= alpha)
                return alpha; // fail hard alpha-cutoff
            if (currentScore < beta)
                beta = currentScore; // beta acts like min in MiniMax
        }
        return beta;
    }
}
