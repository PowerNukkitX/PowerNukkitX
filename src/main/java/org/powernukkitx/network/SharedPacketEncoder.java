package org.powernukkitx.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.jetbrains.annotations.ApiStatus;

/**
 * Writes a {@link SharedPacket} as its shared encoded bytes, so the packet codec after it passes
 * them on instead of encoding the packet again. Sits right before the packet codec in the
 * outbound direction. A shared packet that cannot be shared for this session is handed to the
 * codec as the packet it wraps, which then encodes and reports it as usual.
 */
@ApiStatus.Internal
public final class SharedPacketEncoder extends ChannelOutboundHandlerAdapter {
    public static final String NAME = "shared-packet-encoder";

    private BedrockPacketCodec packetCodec;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof BedrockPacketWrapper wrapper && wrapper.getPacketBuffer() == null
            && wrapper.getPacket() instanceof SharedPacket shared) {
            this.encode(ctx, wrapper, shared);
        }
        ctx.write(msg, promise);
    }

    private void encode(ChannelHandlerContext ctx, BedrockPacketWrapper wrapper, SharedPacket shared) {
        if (this.packetCodec == null) {
            this.packetCodec = ctx.pipeline().get(BedrockPacketCodec.class);
        }
        final ByteBuf encoded = this.packetCodec != null ? shared.encoded(this.packetCodec, wrapper) : null;
        if (encoded == null) {
            wrapper.setPacket(shared.getPacket());
            return;
        }
        wrapper.setPacketBuffer(encoded);
    }
}
