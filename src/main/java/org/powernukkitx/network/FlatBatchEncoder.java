package org.powernukkitx.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.common.util.VarInts;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Drop-in replacement for the protocol library's {@code BedrockBatchEncoder} that writes a batch
 * into one buffer instead of a composite of two buffers per packet.
 * <p>
 * The batch bytes are the same: every packet in queue order, each prefixed with its length as an
 * unsigned varint. What changes is the cost of building them. The composite took a fresh header
 * buffer for every packet and was copied into a contiguous buffer again by the compressor, which
 * needs one; here the packets are copied once, into a buffer sized up front.
 */
@ApiStatus.Internal
public final class FlatBatchEncoder extends ChannelOutboundHandlerAdapter {
    private static final int MAX_VARINT_BYTES = 5;

    private final Queue<BedrockPacketWrapper> messages = new ArrayDeque<>();
    private int pendingBytes;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof BedrockPacketWrapper wrapper)) {
            super.write(ctx, msg, promise);
            return;
        }
        final ByteBuf packet = wrapper.getPacketBuffer();
        this.messages.add(wrapper);
        this.pendingBytes += MAX_VARINT_BYTES + (packet != null ? packet.readableBytes() : 0);
        promise.trySuccess();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.messages.isEmpty()) {
            super.flush(ctx);
            return;
        }

        final ByteBuf buf = ctx.alloc().ioBuffer(this.pendingBytes);
        this.pendingBytes = 0;
        final BedrockBatchWrapper batch = BedrockBatchWrapper.newInstance();
        try {
            BedrockPacketWrapper packet;
            while ((packet = this.messages.poll()) != null) {
                try {
                    final ByteBuf message = packet.getPacketBuffer();
                    if (message == null) {
                        throw new IllegalArgumentException("BedrockPacket is not encoded");
                    }
                    VarInts.writeUnsignedInt(buf, message.readableBytes());
                    buf.writeBytes(message, message.readerIndex(), message.readableBytes());
                    batch.addPacket(packet.retain());
                } finally {
                    packet.release();
                }
            }

            batch.setUncompressed(buf.retain());
            ctx.write(batch.retain());
        } finally {
            buf.release();
            batch.release();
        }

        super.flush(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        BedrockPacketWrapper message;
        while ((message = this.messages.poll()) != null) {
            message.release();
        }
        this.pendingBytes = 0;
        super.handlerRemoved(ctx);
    }
}
