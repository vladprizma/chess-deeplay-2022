package io.deeplay.server.handlers;

import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;
import io.deeplay.interaction.clientToServer.AuthRequest;
import io.deeplay.interaction.serverToClient.AuthResponse;
import io.deeplay.server.client.Client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * В этом хэндлере ожидаем от клиента AuthRequest
 */
public class AuthorizationHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationHandler.class);

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        if (command.getCommandType() == CommandType.AUTH_REQUEST) {
            AuthRequest authRequest = (AuthRequest) command;
            if (authRequest.getSide() != null) {
                Client client = new Client(authRequest.getSide(), ctx);
                LOGGER.info("Пользователь авторизован, сторона - {}", authRequest.getSide().getDescription());
                ctx.writeAndFlush(new AuthResponse(true));
                //Если авторизация подтверждена, удаляем из конвейера текущий хэндлер и добавляем StartGameHandler
                ctx.channel().pipeline().remove(this);
                ctx.channel().pipeline().addLast(new StartGameHandler());
            } else {
                LOGGER.info("Пользователь не авторизован.");
                ctx.writeAndFlush(new AuthResponse(false, "Не удалось авторизовать пользователя."));
            }
        }
    }
}
