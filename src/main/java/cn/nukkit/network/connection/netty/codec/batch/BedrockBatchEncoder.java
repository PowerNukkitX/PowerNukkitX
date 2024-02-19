package cn.nukkit.network.connection.netty.codec.batch;

import cn.nukkit.network.connection.netty.BedrockBatchWrapper;
import cn.nukkit.network.connection.netty.BedrockPacketWrapper;
import cn.nukkit.utils.ByteBufVarInt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.util.ArrayDeque;
import java.util.Queue;

public class BedrockBatchEncoder extends ChannelOutboundHandlerAdapter {
    public static final String NAME = "bedrock-batch-encoder";
    private final Queue<BedrockPacketWrapper> messages = new ArrayDeque<>();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof BedrockPacketWrapper)) {
            super.write(ctx, msg, promise);
            return;
        }

        // Accumulate messages to batch
        this.messages.add((BedrockPacketWrapper) msg);
        promise.trySuccess(); // complete write promise here
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (messages.isEmpty()) {
            super.flush(ctx);
            return;
        }

        CompositeByteBuf buf = ctx.alloc().compositeDirectBuffer(messages.size() * 2);
        BedrockBatchWrapper batch = BedrockBatchWrapper.newInstance();

        try {
            BedrockPacketWrapper packet;
            while ((packet = messages.poll()) != null) try {
                ByteBuf message = packet.getPacketBuffer();
                if (message == null) {
                    throw new IllegalArgumentException("BedrockPacket is not encoded");
                }

                ByteBuf header = ctx.alloc().ioBuffer(5);
                ByteBufVarInt.writeUnsignedInt(header, message.readableBytes());
                buf.addComponent(true, header);
                buf.addComponent(true, message.retain());
                batch.addPacket(packet.retain());
            } finally {
                packet.release();
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
        while ((message = messages.poll()) != null) {
            message.release();
        }
        super.handlerRemoved(ctx);
    }
}
