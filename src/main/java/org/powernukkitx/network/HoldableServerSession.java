package org.powernukkitx.network;

import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * A server session whose outbound packets can be held back while a burst is written from a thread
 * other than the session's event loop.
 * <p>
 * {@link #sendPacketImmediately} writes on the spot when called on the event loop, while
 * {@link #sendPacket} only queues until the event loop flushes its queue on the next session tick.
 * The login sequence relies on that: everything sent immediately reaches the client before anything
 * that was queued, from any thread, in the meantime. When the sequence runs on a level thread
 * instead, the event loop may flush queued packets halfway through it, so a client could receive
 * world packets before {@code StartGamePacket}. Holding the session restores the order: packets
 * sent while held are replayed on the event loop by {@link #releaseOutbound()}, the immediate ones
 * first in the order they were sent, followed by the queued ones.
 */
@ApiStatus.Internal
public class HoldableServerSession extends BedrockServerSession {
    private final Object holdLock = new Object();
    private List<Held> held;

    public HoldableServerSession(BedrockPeer peer, int subClientId) {
        super(peer, subClientId);
    }

    /**
     * Runs {@code action} at this point of the outbound stream: right away when nothing is held,
     * otherwise on the event loop between the held packets around it. Meant for changes to the
     * codec helper that the packets after them depend on.
     */
    public void runInOrder(Runnable action) {
        synchronized (this.holdLock) {
            if (this.held != null) {
                this.held.add(new HeldAction(action));
                return;
            }
        }
        action.run();
    }

    /**
     * Starts holding back every outbound packet until {@link #releaseOutbound()}.
     */
    public void holdOutbound() {
        synchronized (this.holdLock) {
            if (this.held == null) {
                this.held = new ArrayList<>();
            }
        }
    }

    /**
     * Sends the packets held since {@link #holdOutbound()} on the session's event loop and stops
     * holding. Safe to call when nothing is held.
     */
    public void releaseOutbound() {
        synchronized (this.holdLock) {
            if (this.held == null) {
                return;
            }
        }
        this.getPeer().getChannel().eventLoop().execute(this::flushHeld);
    }

    private void flushHeld() {
        synchronized (this.holdLock) {
            final List<Held> entries = this.held;
            this.held = null;
            if (entries == null) {
                return;
            }
            // On the event loop, immediate packets are written inline and queued ones wait for the
            // next session tick, which is the order an event loop caller would have produced
            for (Held entry : entries) {
                if (entry instanceof HeldAction action) {
                    action.action().run();
                } else if (entry instanceof HeldPacket packet && packet.immediate()) {
                    super.sendPacketImmediately(packet.packet());
                } else if (entry instanceof HeldPacket packet) {
                    super.sendPacket(packet.packet());
                }
            }
        }
    }

    @Override
    public void sendPacket(BedrockPacket packet) {
        synchronized (this.holdLock) {
            if (this.held != null) {
                this.held.add(new HeldPacket(packet, false));
                return;
            }
        }
        super.sendPacket(packet);
    }

    @Override
    public void sendPacketImmediately(BedrockPacket packet) {
        synchronized (this.holdLock) {
            if (this.held != null) {
                this.held.add(new HeldPacket(packet, true));
                return;
            }
        }
        super.sendPacketImmediately(packet);
    }

    private sealed interface Held permits HeldPacket, HeldAction {
    }

    private record HeldPacket(BedrockPacket packet, boolean immediate) implements Held {
    }

    private record HeldAction(Runnable action) implements Held {
    }
}
