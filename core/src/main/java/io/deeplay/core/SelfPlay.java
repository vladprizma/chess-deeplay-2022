package io.deeplay.core;

import io.deeplay.core.listener.GameInfoGroup;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.Player;
import io.deeplay.core.statistics.AllGamesStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfPlay {
    private static final Logger LOGGER = LoggerFactory.getLogger(SelfPlay.class);
    private final Player firstPlayer;
    private final Player secondPlayer;
    private final GameInfo gameInfo;
    private final GameInfoGroup gameInfoGroup;
    private Player currentPlayerToMove;

    /**
     * Количество партий, которые оба игрока будут играть подряд(не меняя стороны).
     */
    private final int gamesAmount;

    public SelfPlay(final Player firstPlayer,
                    final Player secondPlayer,
                    final GameInfo gameInfo,
                    final int gamesAmount,
                    final boolean gatherStatistics) {
        if (firstPlayer.getSide() == secondPlayer.getSide()) {
            throw new IllegalArgumentException("Соперники не могут играть одним цветом");
        }
        this.gameInfo = gameInfo;
        if (firstPlayer.getSide() == Side.WHITE) {
            this.firstPlayer = firstPlayer;
            this.secondPlayer = secondPlayer;
        } else {
            this.firstPlayer = secondPlayer;
            this.secondPlayer = firstPlayer;
        }
        this.gameInfoGroup = new GameInfoGroup(gameInfo);
        gameInfoGroup.addListener(firstPlayer);
        gameInfoGroup.addListener(secondPlayer);
        this.gamesAmount = gamesAmount;
        if (gatherStatistics) {
            gameInfoGroup.addListener(new AllGamesStatistics(this));
        }
    }

    public SelfPlay(final Player firstPlayer,
                    final Player secondPlayer,
                    final GameInfo gameInfo) {
        this(firstPlayer, secondPlayer, gameInfo, 1, false);
    }

    public SelfPlay(final Player firstPlayer,
                    final Player secondPlayer,
                    final int gamesAmount,
                    final boolean gatherStatistics) {
        this(firstPlayer, secondPlayer, new GameInfo(), gamesAmount, gatherStatistics);
    }

    public SelfPlay(final Player firstPlayer, final Player secondPlayer) {
        this(firstPlayer, secondPlayer, new GameInfo());
    }

    /**
     * Метод переключает текущего игрока, чей ход ожидается
     */
    private void changeCurrentPlayerToMove() {
        if (currentPlayerToMove.getSide() == firstPlayer.getSide()) {
            currentPlayerToMove = secondPlayer;
            return;
        }
        currentPlayerToMove = firstPlayer;
    }

    public void play() throws InterruptedException {
        gameInfoGroup.playerSeated(firstPlayer.getSide());
        LOGGER.info("{} присоединился к партии за белых", firstPlayer.getName());
        gameInfoGroup.playerSeated(secondPlayer.getSide());
        LOGGER.info("{} присоединился к партии за черных", secondPlayer.getName());
        int countGamesAmount = 0;
        while (countGamesAmount++ < gamesAmount) {
            currentPlayerToMove = gameInfo.whoseMove() == firstPlayer.getSide() ? firstPlayer : secondPlayer;
            gameInfoGroup.gameStarted();
            LOGGER.info("Партия началась, {} из {}", countGamesAmount, gamesAmount);
            //Пока игра не закончена, рассылаем всем слушателям ходы игроков
            while (gameInfo.isGameOver()) {
                LOGGER.info("Ходят {}", currentPlayerToMove.getSide().getDescription());
                final MoveInfo moveInfo = currentPlayerToMove.getAnswer(gameInfo);
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                gameInfoGroup.playerActed(currentPlayerToMove.getSide(), moveInfo);
                LOGGER.info("{} совершил ход: {}", currentPlayerToMove.getName(), moveInfo.toString());
                changeCurrentPlayerToMove();
            }
            gameInfoGroup.gameOver(gameInfo.getGameStatus());
            LOGGER.info("Партия {} из {} - закончена. {}", countGamesAmount, gamesAmount,
                    gameInfo.getGameStatus().getMessage());
            if (countGamesAmount < gamesAmount)
                gameInfo.resetGame();
        }
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public int getGamesAmount() {
        return gamesAmount;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }
}
