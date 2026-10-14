package org.powernukkitx.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.ReferenceCountUtil;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakDisconnectReason;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.powernukkitx.config.category.network.RakNetSettings;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * What {@code network-settings.raknet.maxQueuedBytes} means for a client that cannot keep up: once
 * more than that many bytes wait in a connection's send queue, RakNet drops the connection with
 * {@link RakDisconnectReason#QUEUE_TOO_LONG}. A client that stops reading stands in for a slow link
 * here, since both leave the server without acknowledgements and the queue only grows.
 */
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class RakNetQueueLimitTest {
    private static final int PROTOCOL_VERSION = 11;
    private static final int MESSAGE_BYTES = 64 * 1024;
    private static final int MEGABYTE = 1024 * 1024;

    private EventLoopGroup group;
    private Channel server;
    private Channel client;

    @BeforeEach
    void setUp() {
        this.group = new NioEventLoopGroup(2);
    }

    @AfterEach
    void tearDown() {
        if (this.client != null) {
            this.client.close().syncUninterruptibly();
        }
        if (this.server != null) {
            this.server.close().syncUninterruptibly();
        }
        this.group.shutdownGracefully(0, 1, TimeUnit.SECONDS).syncUninterruptibly();
    }

    @Test
    void defaultLeavesRoomForLargeBursts() {
        Assertions.assertEquals(64 * MEGABYTE, new RakNetSettings().maxQueuedBytes());
    }

    @Test
    void stalledClientIsKickedOnceTheQueueExceedsTheLimit() throws Exception {
        Outcome outcome = this.burstToStalledClient(16 * MEGABYTE, 24 * MEGABYTE);
        Assertions.assertEquals(RakDisconnectReason.QUEUE_TOO_LONG, outcome.reason());
    }

    @Test
    void stalledClientSurvivesTheSameBurstWithTheDefaultLimit() throws Exception {
        Outcome outcome = this.burstToStalledClient(new RakNetSettings().maxQueuedBytes(), 24 * MEGABYTE);
        Assertions.assertNull(outcome.reason(), "disconnected with " + outcome.reason());
    }

    private record Outcome(RakDisconnectReason reason) {
    }

    /**
     * Connects a client, stops it from reading, then sends {@code burstBytes} to it and waits two
     * seconds for RakNet to give up on it. Well below the ten second session timeout, so a
     * disconnect in that time comes from the queue limit.
     */
    private Outcome burstToStalledClient(int maxQueuedBytes, int burstBytes) throws Exception {
        CompletableFuture<Channel> serverSide = new CompletableFuture<>();
        CompletableFuture<RakDisconnectReason> disconnect = new CompletableFuture<>();

        this.server = new ServerBootstrap()
            .channelFactory(RakChannelFactory.server(NioDatagramChannel.class))
            .group(this.group)
            .option(RakChannelOption.RAK_SUPPORTED_PROTOCOLS, new int[]{PROTOCOL_VERSION})
            .option(RakChannelOption.RAK_GUID, 1L)
            .childOption(RakChannelOption.RAK_MAX_QUEUED_BYTES, maxQueuedBytes)
            .childHandler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel channel) {
                    channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) {
                            serverSide.complete(ctx.channel());
                            ctx.fireChannelActive();
                        }

                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object event) {
                            if (event instanceof RakDisconnectReason reason) {
                                disconnect.complete(reason);
                            }
                            ctx.fireUserEventTriggered(event);
                        }
                    });
                }
            })
            .bind(new InetSocketAddress("127.0.0.1", 0))
            .syncUninterruptibly()
            .channel();

        this.client = new Bootstrap()
            .channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
            .group(this.group)
            .option(RakChannelOption.RAK_PROTOCOL_VERSION, PROTOCOL_VERSION)
            .option(RakChannelOption.RAK_GUID, 2L)
            .handler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                    ReferenceCountUtil.release(msg);
                }
            })
            .connect(this.server.localAddress())
            .syncUninterruptibly()
            .channel();

        Channel serverChannel = serverSide.get(10, TimeUnit.SECONDS);
        this.client.parent().config().setAutoRead(false);

        serverChannel.eventLoop().submit(() -> {
            byte[] payload = new byte[MESSAGE_BYTES];
            for (int sent = 0; sent < burstBytes; sent += MESSAGE_BYTES) {
                serverChannel.write(Unpooled.wrappedBuffer(payload));
            }
            serverChannel.flush();
        }).get(10, TimeUnit.SECONDS);

        try {
            return new Outcome(disconnect.get(2, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            return new Outcome(null);
        }
    }
}
