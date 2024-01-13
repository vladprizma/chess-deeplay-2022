package io.deeplay.core.player.evgenbot;

import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EvgenBot extends Player {

    private static final int MAX_DEPTH = 10;
    private static final int DEFAULT_DEPTH = 3;
    private final EvaluationFunction function;
    private final int depth;

    public EvgenBot(final Side side, final EvaluationFunction function, final int depth) {
        super(side);
        this.function = function;
        if (depth < 1) {
            throw new IllegalArgumentException("The depth cannot be less than 1");
        }
        if (depth > MAX_DEPTH) {
            throw new IllegalArgumentException("The depth cannot be more than " + MAX_DEPTH);
        }
        this.depth = depth;
    }

    public EvgenBot(final Side side) {
        this(side, new SimpleEvaluationFunction(), DEFAULT_DEPTH);
    }

    public EvgenBot(final Side side, final EvaluationFunction function) {
        this(side, function, DEFAULT_DEPTH);
    }

    public EvgenBot(final Side side, final int depth) {
        this(side, new SimpleEvaluationFunction(), depth);
    }

    @Override
    public MoveInfo getAnswer(final GameInfo gameInfo) {
        List<EvaluatedMove> evaluatedMoves = new ArrayList<>();
        for (MoveInfo moveInfo : gameInfo.getAvailableMoves()) {
            GameInfo clonedGameInfo = gameInfo.copy(moveInfo);
            evaluatedMoves.add(new EvaluatedMove(moveInfo, evaluateMin(clonedGameInfo, depth - 1)));
        }
        return getGreedyDecision(evaluatedMoves);
    }

    private int evaluateMin(final GameInfo gameInfo, final int currentDepth) {
        if (currentDepth == 0) return function.evaluate(gameInfo);
        int minScore = Integer.MAX_VALUE;
        for (final MoveInfo moveInfo : gameInfo.getAvailableMoves()) {
            final GameInfo clonedGameInfo = gameInfo.copy(moveInfo);
            final int currentScore = evaluateMax(clonedGameInfo, currentDepth - 1);
            if (currentScore < minScore) {
                minScore = currentScore;
            }
        }
        return minScore;
    }

    private int evaluateMax(final GameInfo gameInfo, final int currentDepth) {
        if (currentDepth == 0) return function.evaluate(gameInfo);
        int maxScore = Integer.MIN_VALUE;
        for (final MoveInfo moveInfo : gameInfo.getAvailableMoves()) {
            final GameInfo clonedGameInfo = gameInfo.copy(moveInfo);
            final int currentScore = evaluateMin(clonedGameInfo, currentDepth - 1);
            if (currentScore > maxScore) {
                maxScore = currentScore;
            }
        }
        return maxScore;
    }

    private MoveInfo getGreedyDecision(final List<EvaluatedMove> moves) {
        return moves.stream().max(Comparator.comparingInt(m -> m.evaluation)).get().moveInfo;
    }

    @Override
    public String getName() {
        return "EvgenBot Minmax with SimpleEvaluationFunction";
    }

    private static final class EvaluatedMove {
        final MoveInfo moveInfo;
        final int evaluation;

        private EvaluatedMove(final MoveInfo moveInfo, final int evaluation) {
            this.moveInfo = moveInfo;
            this.evaluation = evaluation;
        }
    }
}
