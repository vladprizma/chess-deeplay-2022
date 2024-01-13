package io.deeplay.interaction.serverToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.core.model.GameStatus;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("GameOverResponse")
public class GameOverResponse extends Command {
    private final boolean isGameOvered;
    private final GameStatus gameStatus;
    private final String errorMessage;

    @JsonCreator
    public GameOverResponse(@JsonProperty("isGameOvered") final boolean isGameOvered,
                            @JsonProperty("gameStatus") final GameStatus gameStatus,
                            @JsonProperty("errorMessage") final String errorMessage) {
        this.isGameOvered = isGameOvered;
        this.gameStatus = gameStatus;
        this.errorMessage = errorMessage;
    }

    public GameOverResponse(@JsonProperty("isGameOvered") final boolean isGameOvered,
                            @JsonProperty("gameStatus") final GameStatus gameStatus) {
        this(isGameOvered, gameStatus, "");
    }

    public GameOverResponse() {
        this.isGameOvered = false;
        this.gameStatus = GameStatus.INACTIVE;
        this.errorMessage = null;
    }

    @JsonProperty("isGameOvered")
    public boolean isGameOvered() {
        return isGameOvered;
    }

    @JsonProperty("gameStatus")
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    @JsonProperty("errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.GAME_OVER_RESPONSE;
    }

    @Override
    public String toString() {
        return "GameOverResponse{" +
                "isGameOvered=" + isGameOvered +
                ", gameStatus=" + gameStatus +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
