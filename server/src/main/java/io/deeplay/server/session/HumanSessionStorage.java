package io.deeplay.server.session;

import io.deeplay.core.model.GameStatus;
import io.deeplay.server.client.Client;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class HumanSessionStorage {
    private static final CopyOnWriteArrayList<GameSession> AWAIT_SESSIONS = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<GameSession> ACTIVE_SESSIONS = new CopyOnWriteArrayList<>();

    public static void addAwaitSession(final GameSession gameSession) {
        AWAIT_SESSIONS.addIfAbsent(gameSession);
    }

    public static void makeSessionActive(final GameSession gameSession) {
        AWAIT_SESSIONS.remove(gameSession);
        ACTIVE_SESSIONS.add(gameSession);

    }

    public static boolean hasAwaitSessions() {
        return AWAIT_SESSIONS.isEmpty();
    }

    public static boolean hasActiveSessions() {
        return ACTIVE_SESSIONS.isEmpty();
    }

    public static GameSession getAwaitSession() {
        return AWAIT_SESSIONS.get(0);
    }

    /**
     * Метод для прерывания активных или ожидающих противника сессий клиента.
     */
    public static void stopClientSessions(final ChannelHandlerContext ctx, final GameStatus gameStatus) {
        if (!hasAwaitSessions()) {
            Iterator<GameSession> iterator = AWAIT_SESSIONS.iterator();
            while (iterator.hasNext()) {
                GameSession gs = iterator.next();
                if (((Client) gs.getFirstPlayer()).getCtx().channel() == ctx.channel()) {
                    gs.stopSession(gameStatus);
                    AWAIT_SESSIONS.remove(gs);
                }
            }
        }
        if (!hasActiveSessions()) {
            Iterator<GameSession> iterator = ACTIVE_SESSIONS.iterator();
            while (iterator.hasNext()) {
                GameSession gs = iterator.next();
                if (((Client) gs.getFirstPlayer()).getCtx().channel() == ctx.channel() ||
                        ((Client) gs.getSecondPlayer()).getCtx().channel() == ctx.channel()) {
                    gs.stopSession(gameStatus);
                    ACTIVE_SESSIONS.remove(gs);
                }
            }
        }
    }

    /**
     *Метод для завершения активной сессии клиента.
     */
    public static void deleteActiveSession(final ChannelHandlerContext ctx) {
        if (!hasActiveSessions()) {
            Iterator<GameSession> iterator = ACTIVE_SESSIONS.iterator();
            while (iterator.hasNext()) {
                GameSession gs = iterator.next();
                if (((Client) gs.getFirstPlayer()).getCtx().channel() == ctx.channel() ||
                        ((Client) gs.getSecondPlayer()).getCtx().channel() == ctx.channel()) {
                    ACTIVE_SESSIONS.remove(gs);
                }
            }
        }
    }
}
