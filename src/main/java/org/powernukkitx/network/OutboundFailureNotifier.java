package org.powernukkitx.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.powernukkitx.utils.CrashReporter;

/**
 * Tail handler that surfaces outbound write failures instead of letting them vanish silently.
 * <p>
 * When an outbound write fails, Netty only fails the associated promise - and a promise nobody
 * listens to just swallows its cause. Packet serialization happens as part of that write, on the
 * Netty thread, far from the game code that originally queued the packet. As a result, a packet
 * built with a field accidentally left null - the most common cause - used to silently drop an
 * entire session's batch with nothing printed anywhere. The listener registered here isn't tied
 * to any particular session, so whatever goes wrong stays attached to the channel it happened on.
 */
final class OutboundFailureNotifier extends ChannelDuplexHandler {

    private static final ChannelFutureListener NOTIFY_ON_FAILURE = future -> {
        final Throwable cause = future.cause();
        if (cause != null) {
            CrashReporter.log("Writing to a client session", future.channel().remoteAddress(), cause);
        }
    };

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (!promise.isVoid()) {
            promise.addListener(NOTIFY_ON_FAILURE);
        }
        ctx.write(msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Deliberately not forwarded further down the pipeline: Netty's built-in tail handler would
        // just log it again as "nobody handled this exception", which adds nothing useful here.
        CrashReporter.log("Handling a channel event for a session", ctx.channel().remoteAddress(), cause);
    }
}
