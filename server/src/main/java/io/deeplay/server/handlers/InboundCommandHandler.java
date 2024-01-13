package io.deeplay.server.handlers;

import io.deeplay.core.model.GameStatus;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.clientToServer.GameOverRequest;
import io.deeplay.interaction.clientToServer.MoveRequest;
import io.deeplay.interaction.clientToServer.StartGameRequest;
import io.deeplay.server.client.Client;
import io.deeplay.server.session.HumanSessionStorage;
import io.deeplay.server.session.GameSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class InboundCommandHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InboundCommandHandler.class);
    private final Client client;
    private GameSession gameSession;

    public InboundCommandHandler(final Client client, final GameSession gameSession) {
        this.client = client;
        this.gameSession = gameSession;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        switch (command.getCommandType()) {
            case MOVE_REQUEST:
                MoveRequest moveRequest = (MoveRequest) command;
                MoveInfo moveInfo = moveRequest.getMoveInfo();
                synchronized (client.getMonitor()) {
                    client.setCurrentMove(moveInfo);
                }
                break;
            case GAME_OVER_REQUEST:
                GameOverRequest gameOverRequest = (GameOverRequest) command;
                gameSession.stopSession(gameOverRequest.getGameStatus());
                ctx.channel().pipeline().remove(this);
                ctx.channel().pipeline().addLast(new StartGameHandler());
                break;
            case START_GAME_REQUEST:
                gameSession.stopSession(GameStatus.INTERRUPTED);
                HumanSessionStorage.stopClientSessions(ctx, GameStatus.INTERRUPTED);
                StartGameRequest startGameRequest = (StartGameRequest) command;
                client.setSide(startGameRequest.getSide());
                gameSession = GameSession.createGameSession(client, startGameRequest.getEnemyType());
                break;
            default:
                LOGGER.info("Некорректная команда: {}", command);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        LOGGER.error("Соединение с клиентом прервано", cause);
        ctx.close();
    }
}
