package io.deeplay.interaction.clientToServer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("ProtocolVersionRequest")
public class ProtocolVersionRequest extends Command {
    private final String protocolVersion;

    public ProtocolVersionRequest(final String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public ProtocolVersionRequest() {
        this.protocolVersion = null;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @JsonIgnore
    @Override
    public CommandType getCommandType() {
        return CommandType.PROTOCOL_VERSION_REQUEST;
    }

    @Override
    public String toString() {
        return "ProtocolVersionRequest{" +
                "protocolVersion='" + protocolVersion + '\'' +
                '}';
    }
}
