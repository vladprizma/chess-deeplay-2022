package io.deeplay.interaction.serverToClient;

import com.fasterxml.jackson.annotation.*;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;

@JsonTypeName("AuthResponse")
public class AuthResponse extends Command {
    private final boolean isAuthorized;
    private final int userId;
    private String errorMessage;

    @JsonCreator
    public AuthResponse(
            @JsonProperty("isAuthorized") final boolean isAuthorized,
            @JsonProperty("userId") final int userId,
            @JsonProperty("errorMessage") final String errorMessage) {
        this.isAuthorized = isAuthorized;
        this.userId = userId;
        this.errorMessage = errorMessage;
    }

    public AuthResponse(final boolean isAuthorized, final String errorMessage) {
        this.isAuthorized = isAuthorized;
        this.userId = 0;
        this.errorMessage = errorMessage;
    }

    public AuthResponse(final boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        this.userId = 0;
    }

    public AuthResponse() {
        this.isAuthorized = false;
        this.userId = 0;
    }

    @JsonGetter("isAuthorized")
    public boolean isAuthorized() {
        return isAuthorized;
    }

    public int getUserId() {
        return userId;
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
        return CommandType.AUTH_RESPONSE;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "isAuthorized=" + isAuthorized +
                ", userId=" + userId +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
