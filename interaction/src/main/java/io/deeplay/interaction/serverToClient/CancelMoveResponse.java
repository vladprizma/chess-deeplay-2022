package io.deeplay.interaction.serverToClient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("CancelMoveResponse")
public class CancelMoveResponse extends Command {
    public CancelMoveResponse() {
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.CANCEL_MOVE_RESPONSE;
    }

    @Override
    public String toString() {
        return "CancelMoveResponse{" +
                "}";
    }
}
