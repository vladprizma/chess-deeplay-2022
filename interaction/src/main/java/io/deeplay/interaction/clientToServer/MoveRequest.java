package io.deeplay.interaction.clientToServer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("MoveRequest")
public class MoveRequest extends Command {
    private final MoveInfo moveInfo;

    public MoveRequest(final MoveInfo moveInfo) {
        this.moveInfo = moveInfo;
    }

    public MoveRequest() {
        this.moveInfo = null;
    }

    public MoveInfo getMoveInfo() {
        return moveInfo;
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.MOVE_REQUEST;
    }

    @Override
    public String toString() {
        return "MoveRequest{" +
                "moveInfo=" + moveInfo +
                '}';
    }
}
