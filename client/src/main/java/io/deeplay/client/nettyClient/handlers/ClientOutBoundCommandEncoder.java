package io.deeplay.client.nettyClient.handlers;

import io.deeplay.interaction.Command;
import io.deeplay.interaction.utils.CommandSerializator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

public class ClientOutBoundCommandEncoder extends ChannelOutboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientOutBoundCommandEncoder.class);

    /**
     * @param ctx
     * @param msg объект класса Command, который необходимо отправить клиенту
     * @param promise
     *
     * Метод сериализует объект в json, упаковывает в ByteBuf и отправляет клиенту
     */
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) {
        Command command = (Command) msg;
        ByteBuf buf = ctx.alloc().directBuffer();
        try {
            byte[] bytes = CommandSerializator.serializeCommand(command);
            buf.writeBytes(bytes);
            ctx.writeAndFlush(buf);
            LOGGER.info("Отправлена команда серверу: {}", command);
        } catch (IOException e) {
            LOGGER.error("Ошибка при сериализации комманды", e);
        } finally {
            buf.release();
        }
    }
}
