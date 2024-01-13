package io.deeplay.interaction.serverToClient;

import com.fasterxml.jackson.annotation.*;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("GetAnswer")
public class GetAnswer extends Command {

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.GET_ANSWER;
    }

    @Override
    public String toString() {
        return "GetAnswer{" +
                '}';
    }
}
