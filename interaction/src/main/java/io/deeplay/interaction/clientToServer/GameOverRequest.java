package io.deeplay.interaction.clientToServer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.core.model.GameStatus;
import io.deeplay.core.model.Side;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

// Если клиент закончил игру раньше(например - сдался), надо уведомлять об этом
@JsonTypeName("GameOverRequest")
public class GameOverRequest extends Command {
    private final Side side;
    private final GameStatus gameStatus;

    public GameOverRequest(final Side side, final GameStatus gameStatus) {
        this.side = side;
        this.gameStatus = gameStatus;
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.GAME_OVER_REQUEST;
    }

    public Side getSide() {
        return side;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    @Override
    public String toString() {
        return "GameOverRequest{" +
                "side=" + side +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
