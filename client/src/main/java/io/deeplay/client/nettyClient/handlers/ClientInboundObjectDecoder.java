package io.deeplay.client.nettyClient.handlers;

import io.deeplay.client.nettyClient.ChessNettyClient;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.clientToServer.ProtocolVersionRequest;
import io.deeplay.interaction.utils.CommandSerializator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

public class ClientInboundObjectDecoder extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientInboundObjectDecoder.class);

    /**
     * После успешного подключения к серверу направляем команду ProtocolVersionRequest с актульной версией
     * протокола взаимодействия.
     * @param ctx - ссылка на контекст взаимодействия клиента и сервера
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        LOGGER.info("Успешно подключено к серверу");
        ctx.writeAndFlush(new ProtocolVersionRequest(ChessNettyClient.getProtocolVersion()));
        LOGGER.info("Ожидаем от сервера подтверждения версии протокола");
    }

    /**
     * При получении входящего сообщения десериализуем ByteBuf в объект класса Command и направляем дальше по конвееру.
     * @param ctx
     * @param msg - входящее ByteBuf сообщение.
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        try {
            buf.readBytes(bytes);
            Command inputCommand = CommandSerializator.deserializeByteArray(bytes);
            LOGGER.info("Поступила команда от сервера: {}", inputCommand);
            ctx.fireChannelRead(inputCommand);
        } catch (IOException e) {
            LOGGER.error("Ошибка при десериализации входящего сообщение", e);
        } finally {
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        LOGGER.error("Отключено от сервера", cause);
        ctx.close();
    }
}
