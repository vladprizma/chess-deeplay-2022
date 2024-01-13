package io.deeplay.client.nettyClient.handlers;

import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;
import io.deeplay.interaction.clientToServer.Pong;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * В этом хэндлере обрабатываем только входящие команды Ping, остальные толкаем дальше по конвейеру
 */
public class ClientPingHandler extends SimpleChannelInboundHandler<Command> {
    /**
     * При получении команды Ping, отвечаем командой Pong.
     * @param ctx Контекст общения между клиентом и сервером
     * @param command Входящая команда от сервера
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        if (CommandType.PING.equals(command.getCommandType())) {
            ctx.writeAndFlush(new Pong());
            return;
        }
        if (CommandType.PONG.equals(command.getCommandType())) {
            return;
        }
        ctx.fireChannelRead(command);
    }
}
