package org.powernukkitx.network;

import io.netty.channel.Channel;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockSessionFactory;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.RejectedExecutionException;

/**
 * A peer whose queued packets can be written on demand.
 * <p>
 * {@link org.cloudburstmc.protocol.bedrock.BedrockSession#sendPacket} only queues, and the peer
 * writes its queue from a 50 ms timer of its own. That timer runs out of step with the server tick,
 * so everything a tick sends waits another 25 ms on average before it leaves. {@link #flushQueued()}
 * lets the server write the queue as soon as a tick is done instead; the timer stays as a fallback
 * for packets queued outside the tick.
 */
@ApiStatus.Internal
public class FlushablePeer extends BedrockPeer {
    private final Runnable flushTask = this::onTick;

    public FlushablePeer(Channel channel, BedrockSessionFactory sessionFactory) {
        super(channel, sessionFactory);
    }

    /**
     * Writes the queued packets on the peer's event loop. Safe to call from any thread; does nothing
     * when nothing is queued or the peer is closed.
     */
    public void flushQueued() {
        if (this.packetQueue.isEmpty() || this.closed.get()) {
            return;
        }
        try {
            this.channel.eventLoop().execute(this.flushTask);
        } catch (RejectedExecutionException ignored) {
        }
    }
}
