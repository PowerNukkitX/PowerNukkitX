package org.powernukkitx.network.nethernet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.cloudburstmc.protocol.bedrock.codec.PacketSerializeException;

final class MalformedPacketGuard extends ChannelInboundHandlerAdapter {
    static final String NAME = "malformed-packet-guard";

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (isDecodeFailure(cause)) {
            ctx.close();
            return;
        }
        ctx.fireExceptionCaught(cause);
    }

    private static boolean isDecodeFailure(Throwable cause) {
        for (Throwable current = cause; current != null; current = current.getCause()) {
            if (current instanceof PacketSerializeException) {
                return true;
            }
        }
        return false;
    }
}
