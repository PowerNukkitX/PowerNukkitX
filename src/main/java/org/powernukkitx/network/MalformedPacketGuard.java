package org.powernukkitx.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.cloudburstmc.protocol.bedrock.codec.PacketSerializeException;

final class MalformedPacketGuard extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (isDecodeFailure(cause)) {
            ctx.close();
            return;
        }
        ctx.fireExceptionCaught(cause);
    }

    private static boolean isDecodeFailure(Throwable cause) {
        for (Throwable t = cause; t != null; t = t.getCause()) {
            if (t instanceof PacketSerializeException) {
                return true;
            }
        }
        return false;
    }
}
