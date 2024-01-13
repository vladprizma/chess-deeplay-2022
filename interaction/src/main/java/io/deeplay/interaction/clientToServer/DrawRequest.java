package io.deeplay.interaction.clientToServer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("DrawRequest")
public class DrawRequest extends Command {

    public DrawRequest() {
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.DRAW_REQUEST;
    }

    @Override
    public String toString() {
        return "DrawRequest{" +
                "}";
    }
}
