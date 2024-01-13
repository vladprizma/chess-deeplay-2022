package io.deeplay.client.nettyClient.handlers;

import io.deeplay.client.session.ClientGameSession;
import io.deeplay.interaction.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientInboundCommandHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientInboundCommandHandler.class);

    private final ClientGameSession session;
    private final ExecutorService executorService;

    public ClientInboundCommandHandler(final ClientGameSession session) {
        this.session = session;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * @param ctx ссылка на контекст между сервером и клиентом
     * @param command в этом блоке получаем на вход десериализованный объект класса Command
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Command command) {
        //Команду от сервера обрабатываем в отдельном потоке в классе ClientGameSession
        executorService.execute(() -> session.acceptCommand(command));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        LOGGER.error("Прервано соединение с сервером", cause);
        ctx.close();
    }
}
