package io.deeplay.server.client;

import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.GameStatus;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.Player;
import io.deeplay.interaction.serverToClient.GameOverResponse;
import io.deeplay.interaction.serverToClient.GetAnswer;
import io.deeplay.interaction.serverToClient.MoveResponse;
import io.deeplay.interaction.serverToClient.StartGameResponse;
import io.deeplay.server.session.HumanSessionStorage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Client extends Player {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    //Контекст общения клиента и сервера
    private final ChannelHandlerContext ctx;
    private final Object monitor;
    private MoveInfo currentMove;

    public Client(final Side side, final ChannelHandlerContext ctx) {
        super(side);
        this.ctx = ctx;
        monitor = new Object();
    }

    public void setCurrentMove(final MoveInfo currentMove) {
        this.currentMove = currentMove;
        monitor.notifyAll();
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public Object getMonitor() {
        return monitor;
    }

    @Override
    public MoveInfo getAnswer(final GameInfo gameInfo) {
        ctx.writeAndFlush(new GetAnswer());
        synchronized (monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return currentMove;
    }

    @Override
    public String getName() {
        return "Client";
    }

    @Override
    public void gameStarted() {
        ctx.writeAndFlush(new StartGameResponse(true));
    }

    @Override
    public void playerSeated(final Side side) {

    }

    @Override
    public void playerActed(final Side side, final MoveInfo moveInfo) {
        ctx.writeAndFlush(new MoveResponse(moveInfo, side));
    }

    @Override
    public void offerDraw(final Side side) {
    }

    @Override
    public void acceptDraw(final Side side) {
    }

    @Override
    public void playerRequestsTakeBack(final Side side) {
    }

    @Override
    public void playerAgreesTakeBack(final Side side) {
    }

    @Override
    public void playerResigned(final Side side) {
        ctx.writeAndFlush(new GameOverResponse(true,
                side == Side.WHITE ? GameStatus.BLACK_WON : GameStatus.WHITE_WON,
                null));
    }

    @Override
    public void draw() {
        ctx.writeAndFlush(new GameOverResponse(true,
                GameStatus.DRAW, // TODO: передавать разные виды ничей из GameInfo
                null));
    }

    @Override
    public void playerWon(final Side side) {
        ctx.writeAndFlush(new GameOverResponse(true,
                side == Side.WHITE ? GameStatus.WHITE_WON : GameStatus.BLACK_WON,
                null));
    }

    @Override
    public void gameOver(final GameStatus gameStatus) {
        ctx.writeAndFlush(new GameOverResponse(true, gameStatus));
        HumanSessionStorage.deleteActiveSession(ctx);
    }
}
