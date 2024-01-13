package io.deeplay.server.handlers;

import io.deeplay.interaction.Command;
import io.deeplay.interaction.clientToServer.ProtocolVersionRequest;
import io.deeplay.interaction.serverToClient.ProtocolVersionResponse;
import io.deeplay.server.ChessNettyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolVersionHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolVersionHandler.class);

    /**
     * В этом хэндлере ожидаем от клиента ProtocolVersionRequest
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        if (command instanceof ProtocolVersionRequest) {
            ProtocolVersionRequest pvr = (ProtocolVersionRequest) command;
            if (ChessNettyServer.checkProtocolVersion(pvr.getProtocolVersion())) {
                ctx.writeAndFlush(new ProtocolVersionResponse(true));
                LOGGER.info("Версия протокола подтверждена");
                LOGGER.info("Ожидаем запрос о начале игры");
                /* Если версия протокола подтверждена, удаляем из конвеера текущий хэндлер и добавляем
                AuthorizationHandler
                */
                ctx.channel().pipeline().remove(this);
//                ctx.channel().pipeline().addLast(new AuthorizationHandler());
                ctx.channel().pipeline().addLast(new StartGameHandler());
            } else {
                LOGGER.info("Версия протокола отклонена");
                ctx.writeAndFlush(
                        new ProtocolVersionResponse(
                                false,
                                String.format("Версия протокола %s отклонена, на сервере используется версия - %s",
                                        pvr.getProtocolVersion(), ChessNettyServer.getProtocolVersion())));
            }
        }
    }
}
