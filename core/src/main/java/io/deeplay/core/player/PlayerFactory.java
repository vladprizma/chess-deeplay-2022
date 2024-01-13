package io.deeplay.core.player;

import io.deeplay.core.model.Side;
import io.deeplay.core.player.chadBot.Minimax;
import io.deeplay.core.player.evgenbot.EvgenBot;
import io.deeplay.core.player.vladbot.namedbots.AlisaBot;
import io.deeplay.core.player.vladbot.namedbots.EvaluationBot;

public class PlayerFactory {
    public static Player createPlayer(final PlayerType playerType, final Side side) {
        switch (playerType) {
            case HUMAN:
                return new HumanPlayer(side);
            case RANDOM_BOT:
                return new RandomBot(side);
            case EVGEN_BOT:
                return new EvgenBot(side);
            case ALICE_BOT:
                return new AlisaBot(side);
            case CHAD_BOT:
                return new Minimax(side, 5);
            case EVALUATION_BOT:
                return new EvaluationBot(side);
            default:
                throw new UnsupportedOperationException("Unsupported player type - " + playerType);
        }
    }
}

