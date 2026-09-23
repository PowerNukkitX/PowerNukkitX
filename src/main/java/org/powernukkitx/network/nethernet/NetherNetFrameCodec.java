package org.powernukkitx.network.nethernet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.codec.FrameIdCodec;

import java.util.List;

/**
 * @author xRookieFight
 * @since 23/09/2026
 */
@ChannelHandler.Sharable
public final class NetherNetFrameCodec extends MessageToMessageCodec<ByteBuf, BedrockBatchWrapper> {

    public static final String NAME = "nethernet-frame-codec";
    public static final NetherNetFrameCodec INSTANCE = new NetherNetFrameCodec();

    private NetherNetFrameCodec() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, BedrockBatchWrapper msg, List<Object> out) {
        if (msg.getCompressed() == null) {
            throw new IllegalStateException("Bedrock batch was not compressed");
        }
        out.add(msg.getCompressed().retainedSlice());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        if (!msg.isReadable()) {
            return;
        }
        out.add(BedrockBatchWrapper.newInstance(msg.readRetainedSlice(msg.readableBytes()), null));
    }
}
