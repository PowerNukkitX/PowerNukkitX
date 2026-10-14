package org.powernukkitx.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.cloudburstmc.netty.channel.nethernet.NetherNetChannelFactory;
import org.cloudburstmc.netty.channel.nethernet.signaling.NetherNetHTTPServerSignaling;
import org.cloudburstmc.netty.util.nethernet.OperatorIdentity;
import org.junit.jupiter.api.Test;
import tel.schich.libdatachannel.LibDataChannelArchDetect;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the native ICE and DTLS stack loads on this platform and that signalling actually binds,
 * which nothing else in the suite covers.
 */
class NetherNetBindSmokeTest {

    @Test
    void bindsSignalling() throws Exception {
        LibDataChannelArchDetect.initialize();

        OperatorIdentity identity = OperatorIdentity.generate("PowerNukkitX Test");
        NetherNetHTTPServerSignaling signaling = new NetherNetHTTPServerSignaling.Builder()
            .setIdentity(identity)
            .setServeHttp(true)
            .build();

        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Channel channel = new ServerBootstrap()
                .group(group)
                .channelFactory(NetherNetChannelFactory.server(signaling))
                .childHandler(new io.netty.channel.ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                    }
                })
                .bind(new InetSocketAddress("127.0.0.1", 0))
                .syncUninterruptibly()
                .channel();
            assertTrue(channel.isOpen());
            channel.close().syncUninterruptibly();
        } finally {
            group.shutdownGracefully();
        }
    }
}
