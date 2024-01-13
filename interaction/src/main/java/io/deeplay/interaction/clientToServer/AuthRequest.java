package io.deeplay.interaction.clientToServer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.core.model.Side;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("AuthRequest")
public class AuthRequest extends Command {
    private final Side side;

    public AuthRequest(final Side side) {
        this.side = side;
    }

    public AuthRequest() {
        this.side = null;
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.AUTH_REQUEST;
    }

    public Side getSide() {
        return side;
    }

    @Override
    public String toString() {
        return "AuthRequest{" +
                "side='" + side + '\'' +
                '}';
    }
}
