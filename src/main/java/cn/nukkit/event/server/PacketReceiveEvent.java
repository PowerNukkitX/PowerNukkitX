package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

/**
 * Called for every packet that is sent by the client once the player has been created at the end of the resource pack sequence.
 */
public class PacketReceiveEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BedrockPacket packet;
    private final Player player;

    public PacketReceiveEvent(Player player, BedrockPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public BedrockPacket getPacket() {
        return packet;
    }

    public Player getPlayer() {
        return player;
    }

}
