package io.deeplay.server.handlers;

import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;
import io.deeplay.interaction.clientToServer.Pong;
import io.deeplay.interaction.serverToClient.ConnectionClosedResponse;
import io.deeplay.interaction.serverToClient.Ping;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Отслеживает дисконнекты клиентов по таймаутам, закрывает соединения неактивных клиентов.
 * Принимает и отсылает команды поддержки соединения Ping и Pong.
 */
public class PingPongHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PingPongHandler.class);

    /**
     * Событие автоматически генерируется сервером, если по заданным таймаутам не выполнены операции чтения или
     * отправки сообщений клиенту.
     * @param ctx
     * @param evt
     */
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
        if (evt instanceof IdleStateEvent) {
            final IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE) {
                Ping ping = new Ping();
                ctx.writeAndFlush(ping);
            }
            if (e.state() == IdleState.READER_IDLE) {
                ConnectionClosedResponse closedRs = new ConnectionClosedResponse(
                        "Слишком долгое отсутствие активности. Соединение закрыто");
                LOGGER.info("Закрывем соединение по таймауту");
                ctx.writeAndFlush(closedRs).addListener(ChannelFutureListener.CLOSE);
            }
            return;
        }
        ctx.fireUserEventTriggered(evt);
    }

    /**
     * В этом хэндлере обрабатываем только входящие команды Ping и Pong, остальные толкаем дальше по конвейеру.
     * @param ctx
     * @param command
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        if (CommandType.PING.equals(command.getCommandType())) {
            Pong pong = new Pong();
            ctx.writeAndFlush(pong);
            return;
        }
        if (CommandType.PONG.equals(command.getCommandType())) {
            return;
        }
        ctx.fireChannelRead(command);
    }

}
