package org.powernukkitx.network.query;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powernukkitx.network.query.codec.QueryPacketCodec;
import org.powernukkitx.network.query.enveloped.DirectAddressedQueryPacket;
import org.powernukkitx.network.query.packet.HandshakePacket;
import org.powernukkitx.network.query.packet.StatisticsPacket;

import java.net.InetSocketAddress;

class QueryPacketCodecTest {

    private static final InetSocketAddress RECIPIENT = new InetSocketAddress("127.0.0.1", 19132);
    private static final InetSocketAddress SENDER = new InetSocketAddress("127.0.0.1", 50000);

    private static EmbeddedChannel channel() {
        return new EmbeddedChannel(new QueryPacketCodec());
    }

    private static DatagramPacket datagram(int... bytes) {
        ByteBuf content = Unpooled.buffer(bytes.length);
        for (int b : bytes) {
            content.writeByte(b);
        }
        return new DatagramPacket(content, RECIPIENT, SENDER);
    }

    @Test
    void truncatedHandshakeIsDropped() {
        EmbeddedChannel channel = channel();

        Assertions.assertFalse(channel.writeInbound(datagram(0xFE, 0xFD, 0x09, 0x00, 0x00, 0x00)));
        Assertions.assertFalse(channel.writeInbound(datagram(0xFE, 0xFD, 0x09)));

        channel.finishAndReleaseAll();
    }

    @Test
    void truncatedStatisticsIsDropped() {
        EmbeddedChannel channel = channel();

        Assertions.assertFalse(channel.writeInbound(datagram(0xFE, 0xFD, 0x00, 0x00, 0x00, 0x00, 0x00)));

        channel.finishAndReleaseAll();
    }

    @Test
    void completeHandshakeIsDecoded() {
        EmbeddedChannel channel = channel();

        Assertions.assertTrue(channel.writeInbound(datagram(0xFE, 0xFD, 0x09, 0x00, 0x00, 0x00, 0x07)));
        DirectAddressedQueryPacket packet = channel.readInbound();
        HandshakePacket handshake = Assertions.assertInstanceOf(HandshakePacket.class, packet.content());
        Assertions.assertEquals(7, handshake.getSessionId());

        channel.finishAndReleaseAll();
    }

    @Test
    void completeStatisticsIsDecoded() {
        EmbeddedChannel channel = channel();

        Assertions.assertTrue(channel.writeInbound(datagram(
                0xFE, 0xFD, 0x00,
                0x00, 0x00, 0x00, 0x07,
                0x00, 0x00, 0x00, 0x01,
                0x01)));
        DirectAddressedQueryPacket packet = channel.readInbound();
        StatisticsPacket statistics = Assertions.assertInstanceOf(StatisticsPacket.class, packet.content());
        Assertions.assertEquals(7, statistics.getSessionId());
        Assertions.assertEquals(1, statistics.getToken());
        Assertions.assertTrue(statistics.isFull());

        channel.finishAndReleaseAll();
    }
}
