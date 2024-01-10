package cn.nukkit.network.connection.netty.codec.batch;

import cn.nukkit.utils.ByteBufVarInt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;

public class BedrockBatchEncoder extends ChannelOutboundHandlerAdapter {

    public static final String NAME = "bedrock-batch-encoder";

    private static final InternalLogger log = InternalLoggerFactory.getInstance(BedrockBatchEncoder.class);

    private final Queue<ByteBuf> messages = new ArrayDeque<>();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            super.write(ctx, msg, promise);
            return;
        }

        // Accumulate messages to batch
        this.messages.add(((ByteBuf) msg).slice());
        promise.trySuccess(); // complete write promise here
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (messages.isEmpty()) {
            super.flush(ctx);
            return;
        }

        CompositeByteBuf buf = ctx.alloc().compositeDirectBuffer(messages.size() * 2);
        try {
            ByteBuf message;
            while ((message = messages.poll()) != null) try {
                ByteBuf header = ctx.alloc().ioBuffer(5);
                ByteBufVarInt.writeUnsignedInt(header, message.readableBytes());
                buf.addComponent(true, header);
                buf.addComponent(true, message.retain());
            } finally {
                message.release();
            }
            ctx.write(buf.retain());
        } finally {
            buf.release();
        }

        super.flush(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message;
        while ((message = messages.poll()) != null) {
            message.release();
        }
        super.handlerRemoved(ctx);
    }
}
