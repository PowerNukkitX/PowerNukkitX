package org.powernukkitx.event.server;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.jetbrains.annotations.Nullable;

/**
 * Called for every packet that is sent by the client, including the packets exchanged during the login and
 * resource pack sequence. The player is only available once it has been created at the end of that sequence,
 * so {@link #getPlayer()} returns null for the earlier packets.
 */
public class PacketReceiveEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BedrockPacket packet;
    private final Player player;

    public PacketReceiveEvent(@Nullable Player player, BedrockPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public BedrockPacket getPacket() {
        return packet;
    }

    /**
     * @return the player the packet was received from, or null when it has not been created yet
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

}
