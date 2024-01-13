package io.deeplay.server.handlers;

import io.deeplay.core.model.GameStatus;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.utils.CommandSerializator;
import io.deeplay.server.session.HumanSessionStorage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

public class InboundObjectDecoder extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InboundObjectDecoder.class);

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        LOGGER.info("Подключился новый клиент");
        LOGGER.info("Ожидаем подтверждения версии протокола");
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        try {
            buf.readBytes(bytes);
            Command inputCommand = CommandSerializator.deserializeByteArray(bytes);
            LOGGER.info("Принята команда от клиента {}", inputCommand);
            ctx.fireChannelRead(inputCommand);
        } catch (IOException e) {
            LOGGER.error("Ошибка про десериализации входящего сообщение", e);
        } finally {
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        LOGGER.info("Клиент отключился");
        HumanSessionStorage.stopClientSessions(ctx, GameStatus.INTERRUPTED);
        ctx.close();
    }
}
