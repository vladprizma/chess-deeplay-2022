package io.deeplay.server.handlers;

import io.deeplay.interaction.Command;
import io.deeplay.interaction.CommandType;
import io.deeplay.interaction.clientToServer.StartGameRequest;
import io.deeplay.server.session.GameSession;
import io.deeplay.server.client.Client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * В этом хэндлере ожидаем от клиента StartGameRequest
 */
public class StartGameHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartGameHandler.class);

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        if (command.getCommandType() == CommandType.START_GAME_REQUEST) {
            StartGameRequest startGameRequest = (StartGameRequest) command;
            Client client = new Client(startGameRequest.getSide(), ctx);
            GameSession gameSession = GameSession.createGameSession(client, startGameRequest.getEnemyType());
            //Если сессия создана, удаляем из конвейера текущий хэндлер и добавляем CommandHandler
            ctx.channel().pipeline().remove(this);
            ctx.channel().pipeline().addLast(new InboundCommandHandler(client, gameSession));
        }
    }
}
