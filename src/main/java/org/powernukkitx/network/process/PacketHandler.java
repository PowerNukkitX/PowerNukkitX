package org.powernukkitx.network.process;

import org.powernukkitx.Server;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

/**
 * @author Kaooot
 */
public interface PacketHandler<T extends BedrockPacket> {

    void handle(T packet, PlayerSessionHolder holder, Server server);

    /**
     * When {@code true} the handler always runs on the Netty thread and is never deferred to the
     * main tick thread. Reserve for protocol/latency packets that must stay prompt (e.g. ping,
     * tick-sync). Gameplay handlers should leave this {@code false} so they run on the main thread
     * via {@link org.powernukkitx.Player#scheduleInbound}, serialized with the tick.
     */
    default boolean runsOnNetworkThread() {
        return false;
    }

    /**
     * When {@code true} the packet is dropped until the session has created its
     * {@link org.powernukkitx.Player}. Until then a handler runs inline on the Netty thread, so its
     * code is reachable by a client that has not authenticated yet. Only the login sequence
     * handlers, which must run before the player exists, should return {@code false}.
     */
    default boolean requiresPlayer() {
        return true;
    }
}