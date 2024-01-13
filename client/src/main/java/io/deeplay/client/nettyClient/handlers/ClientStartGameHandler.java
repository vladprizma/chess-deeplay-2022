package io.deeplay.client.nettyClient.handlers;

import io.deeplay.client.session.ClientGameSession;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.PlayerType;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;
import io.deeplay.interaction.clientToServer.StartGameRequest;
import io.deeplay.interaction.serverToClient.StartGameResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * В этом блоке направляем запрос на начало игры с заданными параметрами.
 */
public class ClientStartGameHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientStartGameHandler.class);
    private Side side;

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        // TODO Реализовать получение настроек игры от пользователя
        ClientGameSession session = new ClientGameSession(ctx);
        //Если игра создана успешно, удаляем из конвеера текущий хэндлер и добавляем CommandHandler
        ctx.channel().pipeline().remove(this);
        ctx.channel().pipeline().addLast(new ClientInboundCommandHandler(session));
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        if (command.getCommandType() == CommandType.START_GAME_RESPONSE) {
            StartGameResponse startGameResponse = (StartGameResponse) command;
            if (startGameResponse.isGameStarted()) {
                LOGGER.info("Начало игры");
            } else {
                LOGGER.info("Игра не создана {}", startGameResponse.getErrorMessage());
            }
        } else {
            LOGGER.info("Ожидаем от сервера подтверждение создания игры");
        }
    }
}
