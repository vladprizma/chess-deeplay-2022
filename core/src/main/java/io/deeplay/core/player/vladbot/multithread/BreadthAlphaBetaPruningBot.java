package io.deeplay.core.player.vladbot.multithread;

import io.deeplay.core.evaluation.vladevaluations.Evaluation;
import io.deeplay.core.evaluation.vladevaluations.PeSTO;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.vladbot.AlphaBetaPruningBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;

/**
 * Поиск в ширину на основе runnable.
 */
public class BreadthAlphaBetaPruningBot extends AlphaBetaPruningBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(BreadthAlphaBetaPruningBot.class);
    private final String PLAYER_NAME = this.getClass().getSimpleName();
    private final static long TIME_FOR_EVALUATION_MILLISECONDS = 200L;
    private final long worktimeInMilliseconds;

    public BreadthAlphaBetaPruningBot(final Side side,
                                      final Evaluation evaluation,
                                      final int maxDepth,
                                      final long worktimeInMilliseconds) {
        super(side, evaluation, maxDepth);
        this.worktimeInMilliseconds = worktimeInMilliseconds;
    }

    public BreadthAlphaBetaPruningBot(final Side side, final Evaluation evaluation) {
        this(side, evaluation, 5, 5000);
    }

    public BreadthAlphaBetaPruningBot(final Side side, final int maxDepth) {
        this(side, new PeSTO(), maxDepth, 5000);
    }

    public BreadthAlphaBetaPruningBot(final Side side, final long worktimeInMilliseconds) {
        this(side, new PeSTO(), 5, worktimeInMilliseconds);
    }

    public BreadthAlphaBetaPruningBot(final Side side) {
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
        final ConcurrentHashMap<MoveInfo, EvaluatedMove> bestMovesUnderTimeLimit = new ConcurrentHashMap<>();
        final int alpha = Integer.MIN_VALUE;
        final int beta = Integer.MAX_VALUE;
        final List<MoveInfo> moves = sortWithMVVLVA(gameInfo);
        final ExecutorService es = Executors.newFixedThreadPool(min(8, moves.size()));
        int iterativeDeepening = 1;
        final Map<MoveInfo, GameInfo> giVirtualCopies = new HashMap<>();
        final long timeWorkError = 15;
        final long finalWaiting = 10;
        final long chillTime = worktimeInMilliseconds / timeWorkError;
        final long workTime = worktimeInMilliseconds - chillTime;
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + workTime;
        while (iterativeDeepening < getMaxDepth()) {
            final long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime) {
                break;
            }
            for (final MoveInfo move : moves) {
                if (!giVirtualCopies.containsKey(move))
                    giVirtualCopies.put(move, gameInfo.copy(move));
                es.execute(new AlphaBetaPruningThread(bestMovesUnderTimeLimit, giVirtualCopies.get(move), move,
                        iterativeDeepening - 1, alpha, beta, endTime));
            }
            iterativeDeepening++;
        }
        try {
            es.awaitTermination(workTime + chillTime / finalWaiting, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Поток был прерван: ", e);
        }
        es.shutdownNow();
        return getGreedyDecision(bestMovesUnderTimeLimit.values());
    }

    private int alphaBetaMaxThreaded(final ConcurrentHashMap<MoveInfo, EvaluatedMove> bestMovesUnderTimeLimit,
                                     final GameInfo gameInfo,
                                     final MoveInfo moveInfo,
                                     int alpha,
                                     final int beta,
                                     final int depthLeft,
                                     final long endTime,
                                     boolean isTimeout) {
        long currentTime = System.currentTimeMillis();
        if (beta != Integer.MAX_VALUE && currentTime >= endTime - TIME_FOR_EVALUATION_MILLISECONDS) {
            isTimeout = true;
        }
        if (isTimeout || depthLeft == 0) return evaluate(gameInfo, depthLeft);
        for (final MoveInfo move : sortWithMVVLVA(gameInfo)) {
            final int currentScore = alphaBetaMinThreaded(bestMovesUnderTimeLimit,
                    gameInfo.copy(move), moveInfo, alpha, beta, depthLeft - 1, endTime, isTimeout);
            if (currentScore >= beta)
                return beta;
            if (currentScore > alpha)
                alpha = currentScore;
        }
        return alpha;
    }

    private int alphaBetaMinThreaded(final ConcurrentHashMap<MoveInfo, EvaluatedMove> bestMovesUnderTimeLimit,
                                     final GameInfo gameInfo,
                                     final MoveInfo moveInfo,
                                     final int alpha,
                                     int beta,
                                     final int depthLeft,
                                     final long endTime,
                                     boolean isTimeout) {
        long currentTime = System.currentTimeMillis();
        if (alpha != Integer.MIN_VALUE && currentTime >= endTime - TIME_FOR_EVALUATION_MILLISECONDS) {
            isTimeout = true;
        }
        if (isTimeout || depthLeft == 0) return evaluate(gameInfo, depthLeft);
        for (final MoveInfo move : sortWithMVVLVA(gameInfo)) {
            final int currentScore = alphaBetaMaxThreaded(bestMovesUnderTimeLimit,
                    gameInfo.copy(move), moveInfo, alpha, beta, depthLeft - 1, endTime, isTimeout);
            if (currentScore <= alpha)
                return alpha;
            if (currentScore < beta)
                beta = currentScore;
        }
        return beta;
    }

    private final class AlphaBetaPruningThread implements Runnable {
        private final ConcurrentHashMap<MoveInfo, EvaluatedMove> bestMovesUnderTimeLimit;
        private final GameInfo gameInfo;
        private final MoveInfo moveInfo;
        private final int depthLeft;
        private final int alpha;
        private final int beta;
        private final long endTime;

        public AlphaBetaPruningThread(final ConcurrentHashMap<MoveInfo, EvaluatedMove> bestMovesUnderTimeLimit,
                                      final GameInfo gameInfo,
                                      final MoveInfo moveInfo,
                                      final int depthLeft,
                                      final int alpha,
                                      final int beta,
                                      final long endTime) {
            this.bestMovesUnderTimeLimit = bestMovesUnderTimeLimit;
            this.gameInfo = gameInfo;
            this.moveInfo = moveInfo;
            this.depthLeft = depthLeft;
            this.alpha = alpha;
            this.beta = beta;
            this.endTime = endTime;
        }

        @Override
        public void run() {
            EvaluatedMove evaluatedMove = new EvaluatedMove(moveInfo,
                    alphaBetaMinThreaded(bestMovesUnderTimeLimit, gameInfo, moveInfo, alpha, beta, depthLeft,
                            endTime, false));
            if (bestMovesUnderTimeLimit.contains(moveInfo)) {
                EvaluatedMove prevEvaluatedMove = bestMovesUnderTimeLimit.get(moveInfo);
                final EvaluatedMove bestEvaluation =
                        prevEvaluatedMove.getScore() > evaluatedMove.getScore() ?
                                prevEvaluatedMove : evaluatedMove;
                bestMovesUnderTimeLimit.put(moveInfo, bestEvaluation);
            } else {
                bestMovesUnderTimeLimit.put(moveInfo, evaluatedMove);
            }
        }
    }

}
