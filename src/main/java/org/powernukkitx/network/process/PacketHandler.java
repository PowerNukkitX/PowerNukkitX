package org.powernukkitx.network.process;

import org.powernukkitx.Server;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.powernukkitx.Player;

/**
 * @author Kaooot
 */
public interface PacketHandler<T extends BedrockPacket> {

    void handle(T packet, PlayerSessionHolder holder, Server server);

    /**
     * When {@code true} the handler always runs on the Netty thread and is never deferred to the
     * main tick thread. Reserve for protocol/latency packets that must stay prompt (e.g. ping,
     * tick-sync). Gameplay handlers should leave this {@code false} so they run on the main thread
     * via {@link Player#scheduleInbound}, serialized with the tick.
     */
    default boolean runsOnNetworkThread() {
        return false;
    }
}