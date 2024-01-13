package io.deeplay.core.player.chadBot;

import io.deeplay.core.api.SimpleLogic;
import io.deeplay.core.api.SimpleLogicAppeal;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.Player;

public class ChadBot extends Player {
    EvalFunction evalFunction;
    SimpleLogicAppeal logic;

    public ChadBot(Side side) {
        super(side);
        evalFunction = new SimpleEvalFunction();
        logic = new SimpleLogic();
    }

    @Override
    public MoveInfo getAnswer(final GameInfo gameInfo) {
        double bestMoveValue = Integer.MIN_VALUE;
        MoveInfo bestMove = null;
        for (MoveInfo move : gameInfo.getAvailableMoves()) {
            double value = evalFunction.eval(gameInfo.copy(move));
            if (logic.isMate(gameInfo.getFenBoard())) {
                return move;
            }
            if (value >= bestMoveValue) {
                bestMoveValue = value;
                bestMove = move;
            }
        }
        return bestMove;
    }

    @Override
    public String getName() {
        return "ChadBot";
    }
}
