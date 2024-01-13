package io.deeplay.interaction.serverToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("DrawResponse")
public class DrawResponse extends Command {
    private final boolean isDrawAgreed;

    @JsonCreator()
    public DrawResponse(@JsonProperty("isDrawAgreed") final boolean isDrawAgreed) {
        this.isDrawAgreed = isDrawAgreed;
    }

    public DrawResponse() {
        this.isDrawAgreed = false;
    }

    @JsonProperty("isDrawAgreed")
    public boolean isDrawAgreed() {
        return isDrawAgreed;
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.DRAW_RESPONSE;
    }

    @Override
    public String toString() {
        return "DrawResponse{" +
                ", isDrawAgreed=" + isDrawAgreed +
                '}';
    }
}
