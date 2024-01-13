package io.deeplay.server.session;

import io.deeplay.core.SelfPlay;
import io.deeplay.core.model.GameStatus;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.Player;
import io.deeplay.core.player.PlayerFactory;
import io.deeplay.core.player.PlayerType;
import io.deeplay.server.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSession.class);
    private final String sessionToken;
    private final Player firstPlayer;
    private Player secondPlayer;
    private SelfPlay selfPlay;

    private final ExecutorService executorService;

    public GameSession(final Player firstPlayer, final Player secondPlayer) {
        this.sessionToken = UUID.randomUUID().toString();
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.selfPlay = new SelfPlay(firstPlayer, secondPlayer);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Конструктор используется для создания сессии, ожидающей подключения оппонента.
     */
    public GameSession(final Player firstPlayer) {
        this.sessionToken = UUID.randomUUID().toString();
        this.firstPlayer = firstPlayer;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * К сессии, ожидающей оппонента, подключается второй игрок.
     */
    public void setSecondPlayer(final Player secondPlayer) {
        this.secondPlayer = secondPlayer;
        selfPlay = new SelfPlay(firstPlayer, secondPlayer);
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public void start() {
        executorService.execute(() -> {
            try {
                selfPlay.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                executorService.shutdown();
            }
        });
    }

    public void stopSession(final GameStatus gameStatus) {
        if (selfPlay != null) {
            selfPlay.getGameInfo().setGameStatus(gameStatus);
            executorService.shutdownNow();
        }
    }

    public static GameSession createGameSession(final Client client, final PlayerType enemyType) {
        GameSession gameSession;
        if (enemyType == PlayerType.HUMAN) {
            if (HumanSessionStorage.hasAwaitSessions()) {
                gameSession = new GameSession(client);
                HumanSessionStorage.addAwaitSession(gameSession);
                LOGGER.info("В партии {} ожидается подключение оппонента.", gameSession.getSessionToken());
            } else {
                gameSession = HumanSessionStorage.getAwaitSession();
                HumanSessionStorage.makeSessionActive(gameSession);
                client.setSide(Side.otherSide(gameSession.getFirstPlayer().getSide()));
                gameSession.setSecondPlayer(client);
                LOGGER.info("Начало партии {}.", gameSession.getSessionToken());
                gameSession.start();
            }
        } else {
            Player enemy = PlayerFactory.createPlayer(enemyType,
                    Side.otherSide(client.getSide()));
            gameSession = new GameSession(client, enemy);
            LOGGER.info("Начало партии {}.", gameSession.getSessionToken());
            gameSession.start();
        }
        return gameSession;
    }
}
