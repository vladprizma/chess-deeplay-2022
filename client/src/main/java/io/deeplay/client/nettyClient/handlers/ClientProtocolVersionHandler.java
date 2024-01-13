package io.deeplay.client.nettyClient.handlers;

import io.deeplay.client.ui.UI;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.serverToClient.ProtocolVersionResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * В этом блоке ожидаем подтверждение версии протокола взаимодействия с сервером.
 */
public class ClientProtocolVersionHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProtocolVersionHandler.class);

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        if (command instanceof ProtocolVersionResponse) {
            ProtocolVersionResponse pvr = (ProtocolVersionResponse) command;
            if (pvr.isVersionMatch()) {
                LOGGER.info("Версия протокола подтверждена сервером");
                //Если версия протокола подтверждена, удаляем из конвеера текущий хэндлер и добавляем блок авторизации
                ctx.channel().pipeline().remove(this);
                ctx.channel().pipeline().addLast(new ClientStartGameHandler());
            } else {
                LOGGER.info("Версия протокола отклонена сервером");
            }
        } else {
            LOGGER.info("Ожидаем от сервера подтверждения версии протокола");
        }
    }
}
