package org.powernukkitx.network.query.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
import org.junit.jupiter.api.Test;
import org.powernukkitx.network.query.enveloped.DirectAddressedQueryPacket;
import org.powernukkitx.network.query.packet.HandshakePacket;
import org.powernukkitx.network.query.packet.StatisticsPacket;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Query is read straight off a datagram channel, so anything on the internet can reach this codec
 * with a packet of its choosing and a source address of its choosing. A datagram that stops short
 * of the body must be dropped rather than read past its end.
 */
class QueryPacketCodecTest {

    private static final int HANDSHAKE = 0x09;
    private static final int STATISTICS = 0x00;

    private static final InetSocketAddress SENDER = new InetSocketAddress("203.0.113.7", 40000);
    private static final InetSocketAddress RECIPIENT = new InetSocketAddress("198.51.100.1", 19132);

    private static EmbeddedChannel channel() {
        return new EmbeddedChannel(new QueryPacketCodec());
    }

    private static DatagramPacket datagram(int id, byte[] body) {
        ByteBuf content = Unpooled.buffer();
        content.writeByte(0xFE);
        content.writeByte(0xFD);
        content.writeByte(id);
        content.writeBytes(body);
        return new DatagramPacket(content, RECIPIENT, SENDER);
    }

    private static byte[] intBytes(int... values) {
        ByteBuf buffer = Unpooled.buffer();
        for (int value : values) {
            buffer.writeInt(value);
        }
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return bytes;
    }

    @Test
    void readsAHandshakeRequest() {
        EmbeddedChannel channel = channel();

        assertTrue(channel.writeInbound(datagram(HANDSHAKE, intBytes(0x0102_0304))));
        channel.checkException();

        DirectAddressedQueryPacket read = channel.readInbound();
        HandshakePacket handshake = assertInstanceOf(HandshakePacket.class, read.content());
        assertEquals(0x0102_0304, handshake.getSessionId());
        assertEquals(SENDER, read.sender());
        assertFalse(channel.finish());
    }

    @Test
    void readsAStatisticsRequest() {
        EmbeddedChannel channel = channel();

        assertTrue(channel.writeInbound(datagram(STATISTICS, intBytes(7, 0x0BAD_F00D))));
        channel.checkException();

        DirectAddressedQueryPacket read = channel.readInbound();
        StatisticsPacket statistics = assertInstanceOf(StatisticsPacket.class, read.content());
        assertEquals(7, statistics.getSessionId());
        assertEquals(0x0BAD_F00D, statistics.getToken());
        assertFalse(statistics.isFull(), "a request without padding asks for the short query");
        assertFalse(channel.finish());
    }

    @Test
    void readsAFullStatisticsRequest() {
        EmbeddedChannel channel = channel();

        assertTrue(channel.writeInbound(datagram(STATISTICS, intBytes(7, 0x0BAD_F00D, 0))));
        channel.checkException();

        DirectAddressedQueryPacket read = channel.readInbound();
        assertTrue(assertInstanceOf(StatisticsPacket.class, read.content()).isFull());
        assertFalse(channel.finish());
    }

    @Test
    void dropsADatagramThatCarriesOnlyTheHeader() {
        for (int id : new int[]{HANDSHAKE, STATISTICS}) {
            EmbeddedChannel channel = channel();

            channel.writeInbound(datagram(id, new byte[0]));
            channel.checkException();

            assertNull(channel.readInbound(), "a header-only datagram must not decode");
            assertFalse(channel.finish());
        }
    }

    @Test
    void dropsADatagramWhoseBodyIsCutShort() {
        EmbeddedChannel handshake = channel();
        handshake.writeInbound(datagram(HANDSHAKE, new byte[]{1, 2, 3}));
        handshake.checkException();
        assertNull(handshake.readInbound());
        assertFalse(handshake.finish());

        EmbeddedChannel statistics = channel();
        statistics.writeInbound(datagram(STATISTICS, new byte[]{1, 2, 3, 4, 5, 6, 7}));
        statistics.checkException();
        assertNull(statistics.readInbound());
        assertFalse(statistics.finish());
    }

    @Test
    void ignoresADatagramThatIsNotQuery() {
        EmbeddedChannel channel = channel();

        channel.writeInbound(new DatagramPacket(Unpooled.copiedBuffer(new byte[]{1, 2, 3, 4}), RECIPIENT, SENDER));
        channel.checkException();

        assertNull(channel.readInbound());
        assertFalse(channel.finish());
    }

    /**
     * The payload of a statistics response is a buffer of its own, and the envelope around the
     * packet cannot free it because a query packet is not reference counted.
     */
    @Test
    void releasesTheStatisticsPayloadItWrote() {
        EmbeddedChannel channel = channel();
        ByteBuf payload = Unpooled.buffer().writeBytes("the long query".getBytes());

        StatisticsPacket statistics = new StatisticsPacket();
        statistics.setSessionId(7);
        statistics.setPayload(payload);

        assertTrue(channel.writeOutbound(new DirectAddressedQueryPacket(statistics, SENDER, RECIPIENT)));
        channel.checkException();

        DatagramPacket written = channel.readOutbound();
        assertEquals(SENDER, written.recipient());
        written.release();

        assertEquals(0, payload.refCnt(), "the response payload was not released");
        assertFalse(channel.finish());
    }
}
