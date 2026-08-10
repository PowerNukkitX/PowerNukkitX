package org.powernukkitx.event.server;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.network.process.PlayerSessionHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Called for every packet sent by the client for which a corresponding packet handler exists,
 * as soon as the player has been created at the end of the resource pack sequence.
 * This event is called after the {@link PacketReceiveEvent} which also covers packets that are not handled by the
 * server. The session is exposed so listeners can reach connection information even before the player object exists.
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
    private final PlayerSessionHolder session;
    private final BedrockPacket packet;

    /**
     * The remote address of the connection the packet arrived on, or {@code null} when the session has no usable
     * socket address.
     *
     * @return the remote {@link InetSocketAddress}, or {@code null}
     */
    @Nullable
    public InetSocketAddress getAddress() {
        final SocketAddress address = session.getSession().getSocketAddress();
        return address instanceof InetSocketAddress inet ? inet : null;
    }
}