package io.deeplay.client.session;

import ch.qos.logback.classic.Logger;
import io.deeplay.client.gui.Gui;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.PlayerType;
import io.deeplay.interaction.Command;
import io.deeplay.interaction.clientToServer.MoveRequest;
import io.deeplay.interaction.clientToServer.StartGameRequest;
import io.deeplay.interaction.serverToClient.GameOverResponse;
import io.deeplay.interaction.serverToClient.MoveResponse;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class ClientGameSession {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ClientGameSession.class);

    private GameInfo gameInfo;

    private final Gui gui;

    private final ChannelHandlerContext ctx;

    public ClientGameSession(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.gameInfo = new GameInfo();
        final Consumer<MoveInfo> sendMove = x -> ctx.writeAndFlush(new MoveRequest(x));
        final BiConsumer<Side, PlayerType> sendNewGameRequest = (side, playerType) -> ctx.writeAndFlush(new StartGameRequest(side, playerType));
        gui = new Gui(gameInfo, sendMove, sendNewGameRequest);
        gui.setVisible(true);
    }

    public void acceptCommand(final Command command) {
        switch (command.getCommandType()) {
            case GET_ANSWER:
                makeMove();
                break;
            case MOVE_RESPONSE:
                MoveResponse moveResponse = (MoveResponse) command;
                updateBoard(moveResponse.getMoveInfo());
                break;
            case GAME_OVER_RESPONSE:
                GameOverResponse gameOverResponse = (GameOverResponse) command;
                gui.gameOver(gameOverResponse.getGameStatus().getMessage());
                LOGGER.info("Игра завершена: {}", gameOverResponse.getGameStatus().getMessage());
                break;
            case START_GAME_RESPONSE:
                gameInfo = new GameInfo();
                gui.restart(gameInfo);
                break;
            default:
                LOGGER.info("Некорректная команда: {}", command);}
    }

    private void updateBoard(final MoveInfo moveInfo) {
        gameInfo.updateBoard(moveInfo);
        gui.updateBoard(gameInfo, moveInfo);
    }

    private void makeMove() {
    }
}
