package io.deeplay.client.nettyClient.handlers;

import io.deeplay.core.model.Side;
import io.deeplay.core.player.Player;
import io.deeplay.core.player.RandomBot;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;
import io.deeplay.interaction.clientToServer.AuthRequest;
import io.deeplay.interaction.serverToClient.AuthResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * В этом блоке направляем запрос на авторизацию. Идентификатором пользователя является сторона.
 */
public class ClientAuthHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAuthHandler.class);

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        //TODO с GUI или TUI запросить у пользователя цвет фигур, которыми он будет играть и тип игрока, которым он
        // хочет игарть
        ctx.writeAndFlush(new AuthRequest(Side.WHITE));
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        if (command.getCommandType() == CommandType.AUTH_RESPONSE) {
            AuthResponse authResponse = (AuthResponse) command;
            if (authResponse.isAuthorized()) {
                LOGGER.info("Успешно авторизованы на сервере");
                //TODO Создаем игрока по параметрам, заданным пользователем
//                Player player = new HumanPlayer(Side.WHITE);
                Player player = new RandomBot(Side.WHITE);
                //Если авторизация прошла успешно, удаляем из конвеера текущий хэндлер и добавляем ClientStartGameHandler
                ctx.channel().pipeline().remove(this);
                ctx.channel().pipeline().addLast(new ClientStartGameHandler());
            } else {
                LOGGER.info("Авторизация не пройдена {}", authResponse.getErrorMessage());
            }
        } else {
            LOGGER.info("Ожидаем от сервера подтверждения авторизации");
        }
    }
}
