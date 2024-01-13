package io.deeplay.core.player.vladbot.multithread;

import io.deeplay.core.evaluation.vladevaluations.Evaluation;
import io.deeplay.core.evaluation.vladevaluations.PeSTO;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.vladbot.AlphaBetaPruningBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;

// with MVVLVA move ordering and multiThread

/**
 * Поиск в глубину на основе runnable
 */
public class DepthAlphaBetaPruningBot extends AlphaBetaPruningBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepthAlphaBetaPruningBot.class);
    private final String PLAYER_NAME = this.getClass().getSimpleName();
    private final long worktimeInMilliseconds;

    public DepthAlphaBetaPruningBot(final Side side,
                                    final Evaluation evaluation,
                                    final int maxDepth,
                                    final long worktimeInMilliseconds) {
        super(side, evaluation, maxDepth);
        this.worktimeInMilliseconds = worktimeInMilliseconds;
    }

    public DepthAlphaBetaPruningBot(final Side side, final Evaluation evaluation) {
        this(side, evaluation, 5, 5000);
    }

    public DepthAlphaBetaPruningBot(final Side side, final int maxDepth) {
        this(side, new PeSTO(), maxDepth, 5000);
    }

    public DepthAlphaBetaPruningBot(final Side side, final long worktimeInMilliseconds) {
        this(side, new PeSTO(), 5, worktimeInMilliseconds);
    }

    public DepthAlphaBetaPruningBot(final Side side) {
        this(side, new PeSTO());
    }

    @Override
    public MoveInfo getAnswer(final GameInfo gameInfo) {
        return evaluateBestMove(gameInfo);
    }

    @Override
    public String getName() {
        return PLAYER_NAME + "Depth=" + getMaxDepth() +
                "Evaluation=" + getEvaluation().getName() +
                "TimeLimit=" + worktimeInMilliseconds;
    }

    public long getWorktimeInMilliseconds() {
        return worktimeInMilliseconds;
    }

    private MoveInfo evaluateBestMove(final GameInfo gameInfo) {
        final ConcurrentLinkedQueue<EvaluatedMove> bestMovesUnderTimeLimit = new ConcurrentLinkedQueue<>();
        final int alpha = Integer.MIN_VALUE;
        final int beta = Integer.MAX_VALUE;
        final List<MoveInfo> moves = sortWithMVVLVA(gameInfo);
        final ExecutorService es = Executors.newFixedThreadPool(min(8, moves.size()));
        for (final MoveInfo move : moves) {
            final GameInfo virtualGameInfo = gameInfo.copy(move);
            es.execute(new AlphaBetaPruningThread(bestMovesUnderTimeLimit, virtualGameInfo, move,
                    getMaxDepth() - 1, alpha, beta));
        }
        try {
            Thread.sleep(worktimeInMilliseconds - worktimeInMilliseconds / 5);
            es.shutdownNow();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Поток был прерван: ", e);
        }
        return getGreedyDecision(bestMovesUnderTimeLimit);
    }

    private int alphaBetaMaxThreaded(final ConcurrentLinkedQueue<EvaluatedMove> bestMovesUnderTimeLimit,
                                     final GameInfo gameInfo,
                                     final MoveInfo moveInfo,
                                     int alpha,
                                     final int beta,
                                     final int depthLeft) {
        if (depthLeft == 0) return evaluate(gameInfo, depthLeft);
        if (beta != Integer.MAX_VALUE && Thread.interrupted()) {
            bestMovesUnderTimeLimit.add(new EvaluatedMove(moveInfo, beta));
        }
        for (final MoveInfo move : sortWithMVVLVA(gameInfo)) {
            final int currentScore = alphaBetaMinThreaded(bestMovesUnderTimeLimit,
                    gameInfo.copy(move), moveInfo, alpha, beta, depthLeft - 1);
            if (currentScore >= beta)
                return beta;
            if (currentScore > alpha)
                alpha = currentScore;
        }
        return alpha;
    }

    private int alphaBetaMinThreaded(final ConcurrentLinkedQueue<EvaluatedMove> bestMovesUnderTimeLimit,
                                     final GameInfo gameInfo,
                                     final MoveInfo moveInfo,
                                     final int alpha,
                                     int beta,
                                     final int depthLeft) {
        if (depthLeft == 0) return evaluate(gameInfo, depthLeft);
        if (alpha != Integer.MIN_VALUE && Thread.interrupted()) {
            bestMovesUnderTimeLimit.add(new EvaluatedMove(moveInfo, alpha));
        }
        for (final MoveInfo move : sortWithMVVLVA(gameInfo)) {
            final int currentScore = alphaBetaMaxThreaded(bestMovesUnderTimeLimit,
                    gameInfo.copy(move), moveInfo, alpha, beta, depthLeft - 1);
            if (currentScore <= alpha)
                return alpha;
            if (currentScore < beta)
                beta = currentScore;
        }
        return beta;
    }

    private final class AlphaBetaPruningThread implements Runnable {
        private final ConcurrentLinkedQueue<EvaluatedMove> bestMovesUnderTimeLimit;
        private final GameInfo gameInfo;
        private final MoveInfo moveInfo;
        private final int depthLeft;
        private final int alpha;
        private final int beta;

        public AlphaBetaPruningThread(final ConcurrentLinkedQueue<EvaluatedMove> bestMovesUnderTimeLimit,
                                      final GameInfo gameInfo,
                                      final MoveInfo moveInfo,
                                      final int depthLeft,
                                      final int alpha,
                                      final int beta) {
            this.bestMovesUnderTimeLimit = bestMovesUnderTimeLimit;
            this.gameInfo = gameInfo;
            this.moveInfo = moveInfo;
            this.depthLeft = depthLeft;
            this.alpha = alpha;
            this.beta = beta;
        }

        @Override
        public void run() {
            bestMovesUnderTimeLimit.add(new EvaluatedMove(moveInfo,
                    alphaBetaMinThreaded(bestMovesUnderTimeLimit, gameInfo, moveInfo, alpha, beta, depthLeft)));
        }
    }

}
