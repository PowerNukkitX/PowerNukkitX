package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

/**
 * Called for every packet sent by the client for which a corresponding packet handler exists,
 * as soon as the player has been created at the end of the resource pack sequence.
 * This event is called after the {@link PacketReceiveEvent} which also covers packets that are not handled by the
 * server.
 * @author Kaooot
 */
@Getter
@RequiredArgsConstructor
public class PacketHandleEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final BedrockPacket packet;
}