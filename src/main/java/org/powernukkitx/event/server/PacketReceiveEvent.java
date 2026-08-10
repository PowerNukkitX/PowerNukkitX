package org.powernukkitx.event.server;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Called for every packet that is sent by the client.
 * <p>
 * Fires for pre-login packets too (for example {@code LoginPacket} or
 * {@code RequestNetworkSettingsPacket}); those run before a player object
 * exists, so {@link #getPlayer()} is {@code null} then. The session is always
 * available, so listeners can still reach the connection - for example
 * {@code event.getAddress()} - and {@link #getSession()} is the session.
 * Pre-login packets are handled on the network thread, so listener code for
 * them must not touch world, chunk, entity or player state.
 */
public class PacketReceiveEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BedrockPacket packet;

    /** The player that sent the packet, or {@code null} while the login sequence is still in progress. */
    @Nullable
    private final Player player;

    private final PlayerSessionHolder session;

    public PacketReceiveEvent(@Nullable Player player, PlayerSessionHolder session, BedrockPacket packet) {
        this.packet = packet;
        this.player = player;
        this.session = session;
    }

    public BedrockPacket getPacket() {
        return packet;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * The session the packet arrived on. Useful while the player object does not exist yet (pre-login), where the
     * connection can still be reached through {@link PlayerSessionHolder#getSession()} or {@link #getAddress()}.
     *
     * @return the session the packet was received on
     */
    @NotNull
    public PlayerSessionHolder getSession() {
        return session;
    }

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
