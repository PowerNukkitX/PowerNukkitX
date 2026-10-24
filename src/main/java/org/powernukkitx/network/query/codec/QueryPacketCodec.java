package org.powernukkitx.network.query.codec;

import io.netty.util.ReferenceCountUtil;
import org.powernukkitx.network.query.QueryPacket;
import org.powernukkitx.network.query.enveloped.DirectAddressedQueryPacket;
import org.powernukkitx.network.query.packet.HandshakePacket;
import org.powernukkitx.network.query.packet.StatisticsPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Arrays;
import java.util.List;

public class QueryPacketCodec extends MessageToMessageCodec<DatagramPacket, DirectAddressedQueryPacket> {
    private static final byte[] QUERY_SIGNATURE = new byte[]{(byte) 0xFE, (byte) 0xFD};
    private static final int HANDSHAKE = 0x09;
    private static final short STATISTICS = 0x00;
    /** Session Id */
    private static final int HANDSHAKE_BODY_LENGHT = 4;
    /** Session Id and Token */
    private static final int STATISTICS_BODY_LENGHT = 8;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, DirectAddressedQueryPacket packet, List<Object> list) throws Exception {
        ByteBuf buf = ByteBufAllocator.DEFAULT.ioBuffer();
        boolean encoded = false;
        try {
            buf.writeByte(packet.content().getId() & 0xFF);
            packet.content().encode(buf);
            list.add(new DatagramPacket(buf, packet.recipient(), packet.sender()));
            encoded = true;
        } finally {
            if (!encoded){
                buf.release();
            }
            if (packet.content() instanceof StatisticsPacket statisticsPacket){
                ReferenceCountUtil.release(statisticsPacket.getPayload());
            }
            packet.release();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket packet, List<Object> list) throws Exception {
        ByteBuf buf = packet.content();
        if (buf.readableBytes() < 3) {
            // not interested
            return;
        }
        buf.markReaderIndex();

        byte[] prefix = new byte[2];
        buf.readBytes(prefix);
        if (Arrays.equals(prefix, QUERY_SIGNATURE)) {
            short id = buf.readUnsignedByte();
            QueryPacket networkPacket;
            int bodyLenght;
            switch (id) {
                case HANDSHAKE:
                    networkPacket = new HandshakePacket();
                    bodyLenght = HANDSHAKE_BODY_LENGHT;
                    break;
                case STATISTICS:
                    networkPacket = new StatisticsPacket();
                    bodyLenght = STATISTICS_BODY_LENGHT;
                    break;
                default:
                    buf.resetReaderIndex();
                    return;
            }
            if (buf.readableBytes() < bodyLenght){
                buf.resetReaderIndex();
                return;
            }
            networkPacket.decode(buf);
            list.add(new DirectAddressedQueryPacket(networkPacket, packet.recipient(), packet.sender()));
        } else {
            buf.resetReaderIndex();
        }
    }
}
