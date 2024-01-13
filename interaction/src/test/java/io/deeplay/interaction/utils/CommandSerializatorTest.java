package io.deeplay.interaction.utils;

import com.fasterxml.jackson.core.JsonParseException;
import io.deeplay.core.model.*;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.clientToServer.AuthRequest;
import io.deeplay.interaction.clientToServer.MoveRequest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CommandSerializatorTest {

    @Test
    public void testSerializeCommand() throws IOException {
        Command command = new AuthRequest(Side.WHITE);
        byte[] bytes = CommandSerializator.serializeCommand(command);
        Command result = CommandSerializator.deserializeByteArray(bytes);

        Assert.assertEquals(command.getClass(), result.getClass());
        Assert.assertEquals(command.getCommandType(), result.getCommandType());
        Assert.assertEquals(((AuthRequest) command).getSide(), ((AuthRequest) result).getSide());
    }


    @Test
    public void testDeserializeCommandException() {
        String str = "Java";
        byte[] bytes = str.getBytes();

        Assert.assertThrows(JsonParseException.class, () -> {
            CommandSerializator.deserializeByteArray(bytes);
        });
    }

    @Test
    public void testParseByteArrayToCommand() throws Exception {
        MoveRequest moveRequest = new MoveRequest(new MoveInfo(new Coord(5), new Coord(17),
                MoveType.PAWN_LONG_MOVE, Figure.W_PAWN));
        String result = new String(CommandSerializator.serializeCommand(moveRequest));
        System.out.println(result);

        Assert.assertTrue(result.contains("MoveRequest"));
        Assert.assertTrue(result.contains("5"));
        Assert.assertTrue(result.contains("W_PAWN"));
    }
}