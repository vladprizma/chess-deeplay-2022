package io.deeplay.interaction.clientToServer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.PlayerType;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("StartGameRequest")
public class StartGameRequest extends Command {
    private final Side side;
    private final PlayerType enemyType;
    public StartGameRequest() {
        this.side = null;
        this.enemyType = null;
    }

    public StartGameRequest(final Side side, final PlayerType enemyType) {
        this.side = side;
        this.enemyType = enemyType;
    }

    public Side getSide() {
        return side;
    }

    public PlayerType getEnemyType() {
        return enemyType;
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.START_GAME_REQUEST;
    }

    @Override
    public String toString() {
        return "StartGameRequest{" +
                "side=" + side +
                ", enemyType=" + enemyType +
                '}';
    }
}
