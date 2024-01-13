package io.deeplay.interaction.clientToServer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("CancelMoveRequest")
public class CancelMoveRequest extends Command {
    public CancelMoveRequest() {
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.CANCEL_MOVE_REQUEST;
    }

    @Override
    public String toString() {
        return "CancelMoveRequest{" +
                "}";
    }
}
