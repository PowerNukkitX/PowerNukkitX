package cn.nukkit.player;

import cn.nukkit.Server;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.session.NetworkPlayerSession;
import java.net.InetSocketAddress;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
public class PlayerConnection {

    protected boolean connected = true;

    @Setter
    private boolean spawned = false;

    @Setter
    private boolean loggedIn = false;

    @Setter
    private boolean locallyInitialized = false;

    @Setter
    private boolean playedBefore;

    protected final InetSocketAddress rawSocketAddress;
    protected InetSocketAddress socketAddress;

    private final Player player;
    private final NetworkPlayerSession networkSession;
    private final Server server;

    public PlayerConnection(Player player, NetworkPlayerSession networkSession, InetSocketAddress socketAddress) {
        this.player = player;
        this.networkSession = networkSession;
        this.rawSocketAddress = socketAddress;
        this.socketAddress = socketAddress;
        this.server = player.getServer();
    }

    /**
     * Get the original address
     *
     * @return {@link String}
     */
    public String getRawAddress() {
        return rawSocketAddress.getAddress().getHostAddress();
    }

    /**
     * Get the original port
     *
     * @return int
     */
    public int getRawPort() {
        return rawSocketAddress.getPort();
    }

    /**
     * Get the original socket address
     *
     * @return {@link InetSocketAddress}
     */
    public InetSocketAddress getRawSocketAddress() {
        return rawSocketAddress;
    }

    /**
     * If waterdogpe compatibility is enabled, the address is modified to be waterdogpe compatible,
     * otherwise it is the same as {@link #rawSocketAddress}
     *
     * @return {@link String}
     */
    public String getAddress() {
        return this.socketAddress.getAddress().getHostAddress();
    }

    /**
     * @see #getRawPort
     */
    public int getPort() {
        return this.socketAddress.getPort();
    }

    /**
     * If waterdogpe compatibility is enabled, the address is modified to be waterdogpe compatible,
     * otherwise it is the same as {@link #rawSocketAddress}
     *
     * @return {@link InetSocketAddress}
     */
    public InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    public int getPing() {
        return player.getSourceInterface().getNetworkLatency(player);
    }

    /**
     * Transfers a player to another server
     *
     * @param address the address
     */
    public void transfer(String address, int port) {
        TransferPacket packet = new TransferPacket();
        packet.address = address;
        packet.port = port;
        this.sendPacket(packet);
    }

    /**
     * Send data packet
     *
     * @param packet Packet to send
     * @return Packet successfully sent
     */
    public boolean sendPacket(DataPacket packet) {
        if (!this.isConnected()) {
            return false;
        }

        DataPacketSendEvent event = new DataPacketSendEvent(player, packet);
        event.call();
        if (event.isCancelled()) {
            return false;
        }

        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", player.getName(), packet);
        }

        this.getNetworkSession().sendPacket(packet);
        return true;
    }

    public void sendPacketImmediately(DataPacket packet, Runnable callback) {
        this.getNetworkSession().sendImmediatePacket(packet, (callback == null ? () -> {} : callback));
    }

    public boolean sendPacketImmediately(DataPacket packet) {
        if (!this.isConnected()) {
            return false;
        }

        DataPacketSendEvent event = new DataPacketSendEvent(player, packet);
        event.call();
        if (event.isCancelled()) {
            return false;
        }

        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Immediate Outbound {}: {}", player.getName(), packet);
        }

        this.getNetworkSession().sendImmediatePacket(packet);
        return true;
    }

    public boolean sendResourcePacket(DataPacket packet) {
        if (!this.isConnected()) {
            return false;
        }

        DataPacketSendEvent event = new DataPacketSendEvent(player, packet);
        event.call();
        if (event.isCancelled()) {
            return false;
        }

        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Resource Outbound {}: {}", player.getName(), packet);
        }

        player.getSourceInterface().putResourcePacket(player, packet);
        return true;
    }
}
