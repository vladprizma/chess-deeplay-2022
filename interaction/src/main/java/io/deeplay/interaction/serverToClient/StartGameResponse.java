package io.deeplay.interaction.serverToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("StartGameResponse")
public class StartGameResponse extends Command {
    private final boolean isGameStarted;
    private String errorMessage;

    public StartGameResponse(final boolean isGameStarted) {
        this.isGameStarted = isGameStarted;
    }

    @JsonCreator
    public StartGameResponse(
            @JsonProperty("isGameStarted") final boolean isGameStarted,
            @JsonProperty("errorMessage") final String errorMessage) {
        this.isGameStarted = isGameStarted;
        this.errorMessage = errorMessage;
    }

    public StartGameResponse() {
        this.isGameStarted = false;
    }

    @JsonProperty("isGameStarted")
    public boolean isGameStarted() {
        return isGameStarted;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.START_GAME_RESPONSE;
    }

    @Override
    public String toString() {
        return "StartGameResponse{" +
                "isGameStarted=" + isGameStarted +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
