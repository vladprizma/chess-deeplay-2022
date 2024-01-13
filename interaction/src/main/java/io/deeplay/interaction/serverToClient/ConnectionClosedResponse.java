package io.deeplay.interaction.serverToClient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("ConnectionClosedResponse")
public class ConnectionClosedResponse extends Command {
    private String errorMessage;

    public ConnectionClosedResponse() {
    }

    public ConnectionClosedResponse(final String errorMessage) {
        this.errorMessage = errorMessage;
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
        return CommandType.CONNECTION_CLOSED_RESPONSE;
    }

    @Override
    public String toString() {
        return "ConnectionClosedResponse{" +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
