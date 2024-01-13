package io.deeplay.interaction.serverToClient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("MoveResponse")
public class MoveResponse extends Command {
    private final MoveInfo moveInfo;
    private final Side side;

    public MoveResponse(final MoveInfo moveInfo, final Side side) {
        this.moveInfo = moveInfo;
        this.side = side;
    }

    private MoveResponse() {
        this.moveInfo = null;
        this.side = null;
    }

    public MoveInfo getMoveInfo() {
        return moveInfo;
    }

    public Side getSide() {
        return side;
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.MOVE_RESPONSE;
    }

    @Override
    public String toString() {
        return "MoveResponse{" +
                "moveInfo=" + moveInfo +
                ", side=" + side +
                '}';
    }
}
