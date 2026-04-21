package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

/**
 * Called for every packet that is sent by the server once the player has been created at the end of the
 * resource pack sequence.
 * Note: Packets sent via the player's BedrockServerSession are not affected by this. Please use the methods provided
 * for this purpose in the player class.
 * @see Player#dataPacket(BedrockPacket)
 * @see Player#dataPacketImmediately(BedrockPacket)
 */
public class PacketSendEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BedrockPacket packet;
    private final Player player;

    public PacketSendEvent(Player player, BedrockPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public BedrockPacket getPacket() {
        return packet;
    }
}
