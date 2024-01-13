package io.deeplay.interaction.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.deeplay.interaction.Command;

import java.io.IOException;

public class CommandSerializator {
    private static ObjectMapper objectMapper;

    static {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
    }

    public static Command deserializeByteArray(final byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, Command.class);
    }

    public static Command deserializeByteArray(final byte[] bytes, final int offset, final int len) throws IOException {
        return objectMapper.readValue(bytes, offset, len, Command.class);
    }

    public static byte[] serializeCommand(final Command command) throws IOException {
        return objectMapper.writeValueAsBytes(command);
    }
}
