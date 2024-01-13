package io.deeplay.interaction.clientToServer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("Pong")
public class Pong extends Command {

    public Pong() {
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.PONG;
    }

    @Override
    public String toString() {
        return "Pong{" +
                "}";
    }
}
