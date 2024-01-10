package cn.nukkit.network.connection.netty.codec.packet;

import cn.nukkit.network.protocol.*;
import cn.nukkit.registry.Registries;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import cn.nukkit.network.connection.netty.BedrockPacketWrapper;

import java.util.List;

public abstract class BedrockPacketCodec extends MessageToMessageCodec<ByteBuf, DataPacket> {

    public static final String NAME = "bedrock-packet-codec";
    private static final InternalLogger log = InternalLoggerFactory.getInstance(BedrockPacketCodec.class);

    @Override
    protected final void encode(ChannelHandlerContext ctx, DataPacket msg, List<Object> out) throws Exception {
        byte[] buffer = msg.getBuffer();
        ByteBuf buf = ctx.alloc().buffer(128);
        if (buffer != null) {
            // We have a pre-encoded packet buffer, just use that.
            encodeHeader(buf, msg);
            buf.writeBytes(buffer);
            out.add(buf.retain());
        } else {
            try {
                encodeHeader(buf, msg);
                msg.tryEncode();
                buffer = msg.getBuffer();
                buf.writeBytes(buffer);
                out.add(buf.retain());
            } catch (Throwable t) {
                log.error("Error encoding packet {}", msg, t);
            } finally {
                buf.release();
            }
        }
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        BedrockPacketWrapper wrapper = new BedrockPacketWrapper();
        wrapper.setPacketBuffer(msg.retainedSlice());
        try {
            int index = msg.readerIndex();
            this.decodeHeader(msg, wrapper);
            wrapper.setHeaderLength(msg.readerIndex() - index);
            DataPacket dataPacket = Registries.PACKET.get(wrapper.getPacketId());
            if (dataPacket == null) {
                log.info("Failed to decode packet for packetId {}", wrapper.getPacketId());
                return;
            }
            byte[] data = new byte[msg.readableBytes()];
            msg.writeBytes(data);
            dataPacket.setBuffer(data);
            dataPacket.decode();
            wrapper.setPacket(dataPacket);
            out.add(wrapper.retain());
        } catch (Throwable t) {
            log.info("Failed to decode packet", t);
            throw t;
        } finally {
            wrapper.release();
        }
    }

    public abstract void encodeHeader(ByteBuf buf, DataPacket msg);

    public abstract void decodeHeader(ByteBuf buf, BedrockPacketWrapper msg);
}
